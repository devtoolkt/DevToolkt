package dev.toolkt.reactive.cell

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.test_utils.CellSamplingStrategy
import dev.toolkt.reactive.cell.test_utils.ConstCellFactory
import dev.toolkt.reactive.cell.test_utils.UpdateVerificationStrategy
import dev.toolkt.reactive.cell.test_utils.assertDoesNotUpdate
import dev.toolkt.reactive.cell.test_utils.assertUpdates
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
    fun test_initial_source1Inert_source2Inert_passive() {
        test_initial(
            source1ConstCellFactory = ConstCellFactory.Inert,
            source2ConstCellFactory = ConstCellFactory.Inert,
            samplingStrategy = CellSamplingStrategy.Passive,
        )
    }

    @Test
    fun test_initial_source1Inert_source2Inert_active() {
        test_initial(
            source1ConstCellFactory = ConstCellFactory.Inert,
            source2ConstCellFactory = ConstCellFactory.Inert,
            samplingStrategy = CellSamplingStrategy.Active,
        )
    }

    @Test
    fun test_initial_source1Inert_source2Dynamic_passive() {
        test_initial(
            source1ConstCellFactory = ConstCellFactory.Inert,
            source2ConstCellFactory = ConstCellFactory.Dynamic,
            samplingStrategy = CellSamplingStrategy.Passive,
        )
    }

    @Test
    fun test_initial_source1Inert_source2Dynamic_active() {
        test_initial(
            source1ConstCellFactory = ConstCellFactory.Inert,
            source2ConstCellFactory = ConstCellFactory.Dynamic,
            samplingStrategy = CellSamplingStrategy.Active,
        )
    }

    @Test
    fun test_initial_source1Dynamic_source2Inert_passive() {
        test_initial(
            source1ConstCellFactory = ConstCellFactory.Dynamic,
            source2ConstCellFactory = ConstCellFactory.Inert,
            samplingStrategy = CellSamplingStrategy.Passive,
        )
    }

    @Test
    fun test_initial_source1Dynamic_source2Inert_active() {
        test_initial(
            source1ConstCellFactory = ConstCellFactory.Dynamic,
            source2ConstCellFactory = ConstCellFactory.Inert,
            samplingStrategy = CellSamplingStrategy.Active,
        )
    }

    @Test
    fun test_initial_source1Dynamic_source2Dynamic_passive() {
        test_initial(
            source1ConstCellFactory = ConstCellFactory.Dynamic,
            source2ConstCellFactory = ConstCellFactory.Dynamic,
            samplingStrategy = CellSamplingStrategy.Passive,
        )
    }

    @Test
    fun test_initial_source1Dynamic_source2Dynamic_active() {
        test_initial(
            source1ConstCellFactory = ConstCellFactory.Dynamic,
            source2ConstCellFactory = ConstCellFactory.Dynamic,
            samplingStrategy = CellSamplingStrategy.Active,
        )
    }

    private fun test_sameSource(
        updateVerificationStrategy: UpdateVerificationStrategy?,
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

        val updateVerificationProcess = updateVerificationStrategy?.begin(
            subjectCell = map2Cell,
        )

        assertUpdates(
            subjectCell = map2Cell,
            updateVerificationProcess = updateVerificationProcess,
            doTrigger = doUpdate,
            expectedUpdatedValue = "20:20",
        )
    }

    @Test
    fun test_sameSource_passive() {
        test_sameSource(
            updateVerificationStrategy = null,
        )
    }

    @Ignore // FIXME: Flaky test
    @Test
    fun test_sameSource_active() {
        UpdateVerificationStrategy.values.forEach { updateVerificationStrategy ->
            test_sameSource(
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }

    private fun test_allFilteredOut(
        updateVerificationStrategy: UpdateVerificationStrategy.Total?,
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

        val updateVerificationProcess = updateVerificationStrategy?.begin(
            subjectCell = map2Cell,
        )

        assertDoesNotUpdate(
            subjectCell = map2Cell,
            updateVerificationProcess = updateVerificationProcess,
            doTrigger = doTrigger,
            expectedNonUpdatedValue = "10:A",
        )
    }

    @Test
    fun test_allFilteredOut_passive() {
        test_allFilteredOut(
            updateVerificationStrategy = null,
        )
    }

    @Test
    fun test_allFilteredOut_active() {
        UpdateVerificationStrategy.Total.values.forEach { updateVerificationStrategy ->
            test_allFilteredOut(
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }

    private fun test_source1Update(
        source2ConstCellFactory: ConstCellFactory,
        updateVerificationStrategy: UpdateVerificationStrategy?,
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

        val updateVerificationProcess = updateVerificationStrategy?.begin(
            subjectCell = map2Cell,
        )

        assertUpdates(
            subjectCell = map2Cell,
            updateVerificationProcess = updateVerificationProcess,
            doTrigger = doUpdate,
            expectedUpdatedValue = "20:A",
        )
    }

    @Test
    fun test_source1Update_source2Inert_passive() {
        test_source1Update(
            source2ConstCellFactory = ConstCellFactory.Inert,
            updateVerificationStrategy = null,
        )
    }

    @Test
    fun test_source1Update_source2Inert_active() {
        UpdateVerificationStrategy.values.forEach { updateVerificationStrategy ->
            test_source1Update(
                source2ConstCellFactory = ConstCellFactory.Inert,
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }

    @Test
    fun test_source1Update_source2Dynamic_passive() {
        test_source1Update(
            source2ConstCellFactory = ConstCellFactory.Dynamic,
            updateVerificationStrategy = null,
        )
    }

    @Test
    fun test_source1Update_source2Dynamic_active() {
        UpdateVerificationStrategy.values.forEach { updateVerificationStrategy ->
            test_source1Update(
                source2ConstCellFactory = ConstCellFactory.Dynamic,
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }

    private fun test_source2Update(
        source1ConstCellFactory: ConstCellFactory,
        updateVerificationStrategy: UpdateVerificationStrategy?,
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

        val updateVerificationProcess = updateVerificationStrategy?.begin(
            subjectCell = map2Cell,
        )

        assertUpdates(
            subjectCell = map2Cell,
            updateVerificationProcess = updateVerificationProcess,
            doTrigger = doUpdate,
            expectedUpdatedValue = "10:B",
        )
    }

    @Test
    fun test_source2Update_source1Inert_passive() {
        test_source2Update(
            source1ConstCellFactory = ConstCellFactory.Inert,
            updateVerificationStrategy = null,
        )
    }

    @Test
    fun test_source2Update_source1Dynamic_active() {
        UpdateVerificationStrategy.values.forEach { updateVerificationStrategy ->
            test_source2Update(
                source1ConstCellFactory = ConstCellFactory.Dynamic,
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }

    @Test
    fun test_source2Update_source1Dynamic_passive() {
        test_source2Update(
            source1ConstCellFactory = ConstCellFactory.Dynamic,
            updateVerificationStrategy = null,
        )
    }

    @Test
    fun test_source2Update_source1Inert_active() {
        UpdateVerificationStrategy.values.forEach { updateVerificationStrategy ->
            test_source2Update(
                source1ConstCellFactory = ConstCellFactory.Inert,
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }

    private fun test_simultaneousUpdates(
        updateVerificationStrategy: UpdateVerificationStrategy?,
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

        val updateVerificationProcess = updateVerificationStrategy?.begin(
            subjectCell = map2Cell,
        )

        assertUpdates(
            subjectCell = map2Cell,
            updateVerificationProcess = updateVerificationProcess,
            doTrigger = doUpdate,
            expectedUpdatedValue = "11:B",
        )
    }

    @Test
    fun test_simultaneousUpdates_passive() {
        test_simultaneousUpdates(
            updateVerificationStrategy = null,
        )
    }

    @Test
    fun test_simultaneousUpdates_active() {
        UpdateVerificationStrategy.values.forEach { updateVerificationStrategy ->
            test_simultaneousUpdates(
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }
}
