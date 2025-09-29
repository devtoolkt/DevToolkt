package dev.toolkt.reactive.cell

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.test_utils.CellSamplingStrategy
import dev.toolkt.reactive.cell.test_utils.ConstCellFactory
import dev.toolkt.reactive.cell.test_utils.UpdateVerificationStrategy
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.filter
import dev.toolkt.reactive.event_stream.hold
import dev.toolkt.reactive.event_stream.map
import kotlin.test.Ignore
import kotlin.test.Test

@Suppress("ClassName")
class Cell_map2_combo_tests {
    private fun test_initial(
        source1ConstCellFactory: ConstCellFactory,
        source2ConstCellFactory: ConstCellFactory,
        samplingStrategy: CellSamplingStrategy,
    ) {
        val sourceCell1 = MomentContext.execute {
            source1ConstCellFactory.create(10)
        }

        val sourceCell2 = MomentContext.execute {
            source2ConstCellFactory.create('A')
        }

        val map2Cell = Cell.map2(
            sourceCell1,
            sourceCell2,
        ) { value1, value2 ->
            "$value1:$value2"
        }

        samplingStrategy.perceive(map2Cell).assertCurrentValueEquals(
            expectedCurrentValue = "10:A",
        )
    }

    @Test
    fun test_initial_passive() {
        ConstCellFactory.values.forEach { source1ConstCellFactory ->
            ConstCellFactory.values.forEach { source2ConstCellFactory ->
                test_initial(
                    source1ConstCellFactory = source1ConstCellFactory,
                    source2ConstCellFactory = source2ConstCellFactory,
                    samplingStrategy = CellSamplingStrategy.Passive,
                )
            }
        }
    }

    @Test
    fun test_initial_active() {
        ConstCellFactory.values.forEach { source1ConstCellFactory ->
            ConstCellFactory.values.forEach { source2ConstCellFactory ->
                test_initial(
                    source1ConstCellFactory = source1ConstCellFactory,
                    source2ConstCellFactory = source2ConstCellFactory,
                    samplingStrategy = CellSamplingStrategy.Active,
                )
            }
        }
    }

    private fun test_sameSource(
        updateVerificationStrategy: UpdateVerificationStrategy,
    ) {
        val doUpdate = EmitterEventStream<Unit>()

        val sourceCell = MomentContext.execute {
            Cell.define(
                initialValue = 10,
                newValues = doUpdate.map { 20 },
            )
        }

        val map2Cell = Cell.map2(
            sourceCell,
            sourceCell,
        ) { value1, value2 ->
            "$value1:$value2"
        }

        val updateVerifier = updateVerificationStrategy.begin(
            subjectCell = map2Cell,
        )

        updateVerifier.verifyUpdates(
            doUpdate = doUpdate,
            expectedUpdatedValue = "20:20",
        )
    }

    @Test
    fun test_sameSource_passive() {
        test_sameSource(
            updateVerificationStrategy = UpdateVerificationStrategy.Passive,
        )
    }

    @Ignore // FIXME: Flaky test
    @Test
    fun test_sameSource_active() {
        UpdateVerificationStrategy.Active.values.forEach { updateVerificationStrategy ->
            test_sameSource(
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }

    @Ignore // FIXME: Flaky test
    @Test
    fun test_sameSource_quick() {
        test_sameSource(
            updateVerificationStrategy = UpdateVerificationStrategy.Quick,
        )
    }

    private fun test_allFilteredOut(
        updateVerificationStrategy: UpdateVerificationStrategy.Total,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceCell1 = MomentContext.execute {
            Cell.define(
                initialValue = 10,
                newValues = doTrigger.filter { false },
            )
        }

        val sourceCell2 = MomentContext.execute {
            Cell.define(
                initialValue = 'A',
                newValues = doTrigger.filter { false },
            )
        }

        val map2Cell = Cell.map2(
            sourceCell1,
            sourceCell2,
        ) { value1, value2 ->
            "$value1:$value2"
        }

        val updateVerifier = updateVerificationStrategy.begin(
            subjectCell = map2Cell,
        )

        updateVerifier.verifyDoesNotUpdate(
            doTrigger = doTrigger,
            expectedNonUpdatedValue = "10:A",
        )
    }

    @Test
    fun test_allFilteredOut_passive() {
        test_allFilteredOut(
            updateVerificationStrategy = UpdateVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_allFilteredOut_active() {
        UpdateVerificationStrategy.Active.values.forEach { updateVerificationStrategy ->
            test_allFilteredOut(
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }

    private fun test_source1Update(
        source2ConstCellFactory: ConstCellFactory,
        updateVerificationStrategy: UpdateVerificationStrategy,
    ) {
        val doUpdate = EmitterEventStream<Unit>()

        val sourceCell1 = MomentContext.execute {
            doUpdate.map { 20 }.hold(
                initialValue = 10,
            )
        }

        val sourceCell2 = MomentContext.execute {
            source2ConstCellFactory.create('A')
        }

        val map2Cell = Cell.map2(
            sourceCell1,
            sourceCell2,
        ) { value1, value2 ->
            "$value1:$value2"
        }

        val updateVerifier = updateVerificationStrategy.begin(
            subjectCell = map2Cell,
        )

        updateVerifier.verifyUpdates(
            doUpdate = doUpdate,
            expectedUpdatedValue = "20:A",
        )
    }

    private fun test_source1Update(
        updateVerificationStrategy: UpdateVerificationStrategy,
    ) {
        ConstCellFactory.values.forEach { source2ConstCellFactory ->
            test_source1Update(
                source2ConstCellFactory = source2ConstCellFactory,
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }

    @Test
    fun test_source1Update_passive() {
        test_source1Update(
            updateVerificationStrategy = UpdateVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_source1Update_active() {
        test_source1Update(
            updateVerificationStrategy = UpdateVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_source1Update_quick() {
        test_source1Update(
            updateVerificationStrategy = UpdateVerificationStrategy.Quick,
        )
    }

    private fun test_source2Update(
        source1ConstCellFactory: ConstCellFactory,
        updateVerificationStrategy: UpdateVerificationStrategy,
    ) {
        val doUpdate = EmitterEventStream<Unit>()

        val sourceCell1 = MomentContext.execute {
            source1ConstCellFactory.create(10)
        }

        val sourceCell2 = MomentContext.execute {
            doUpdate.map { 'B' }.hold(
                initialValue = 'A',
            )
        }

        val map2Cell = Cell.map2(
            sourceCell1,
            sourceCell2,
        ) { value1, value2 ->
            "$value1:$value2"
        }

        val updateVerifier = updateVerificationStrategy.begin(
            subjectCell = map2Cell,
        )

        updateVerifier.verifyUpdates(
            doUpdate = doUpdate,
            expectedUpdatedValue = "10:B",
        )
    }

    private fun test_source2Update(
        updateVerificationStrategy: UpdateVerificationStrategy,
    ) {
        ConstCellFactory.values.forEach { source1ConstCellFactory ->
            test_source2Update(
                source1ConstCellFactory = source1ConstCellFactory,
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }

    @Test
    fun test_source2Update_passive() {
        test_source2Update(
            updateVerificationStrategy = UpdateVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_source2Update_active() {
        UpdateVerificationStrategy.Active.values.forEach { updateVerificationStrategy ->
            test_source2Update(
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }

    @Test
    fun test_source2Update_quick() {
        test_source2Update(
            updateVerificationStrategy = UpdateVerificationStrategy.Quick,
        )
    }

    private fun test_simultaneousUpdates(
        updateVerificationStrategy: UpdateVerificationStrategy,
    ) {
        val doUpdate = EmitterEventStream<Unit>()

        val sourceCell1 = MomentContext.execute {
            Cell.define(
                initialValue = 10,
                newValues = doUpdate.map { 11 },
            )
        }

        val sourceCell2 = MomentContext.execute {
            Cell.define(
                initialValue = 'A',
                newValues = doUpdate.map { 'B' },
            )
        }

        val map2Cell = Cell.map2(
            sourceCell1,
            sourceCell2,
        ) { value1, value2 ->
            "$value1:$value2"
        }

        val updateVerifier = updateVerificationStrategy.begin(
            subjectCell = map2Cell,
        )

        updateVerifier.verifyUpdates(
            doUpdate = doUpdate,
            expectedUpdatedValue = "11:B",
        )
    }

    @Test
    fun test_simultaneousUpdates_passive() {
        test_simultaneousUpdates(
            updateVerificationStrategy = UpdateVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_simultaneousUpdates_active() {
        UpdateVerificationStrategy.Active.values.forEach { updateVerificationStrategy ->
            test_simultaneousUpdates(
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }

    @Test
    fun test_simultaneousUpdates_quick() {
        test_simultaneousUpdates(
            updateVerificationStrategy = UpdateVerificationStrategy.Quick,
        )
    }

    private fun test_deactivation(
        updateVerificationStrategy: UpdateVerificationStrategy.Active,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceCell1 = MomentContext.execute {
            Cell.define(
                initialValue = 10,
                newValues = doTrigger.map { 11 },
            )
        }

        val sourceCell2 = MomentContext.execute {
            Cell.define(
                initialValue = 'A',
                newValues = doTrigger.map { 'B' },
            )
        }

        val map2Cell = Cell.map2(
            sourceCell1,
            sourceCell2,
        ) { value1, value2 ->
            "$value1:$value2"
        }

        val updateVerifier = updateVerificationStrategy.begin(
            subjectCell = map2Cell,
        )

        updateVerifier.end()

        updateVerifier.verifyUpdateDidNotPropagate(
            doTrigger = doTrigger,
        )
    }

    @Test
    fun test_deactivation() {
        UpdateVerificationStrategy.Active.values.forEach { updateVerificationStrategy ->
            test_deactivation(
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }
}
