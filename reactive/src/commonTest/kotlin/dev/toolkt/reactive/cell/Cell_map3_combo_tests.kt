package dev.toolkt.reactive.cell

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.test_utils.CellSamplingStrategy
import dev.toolkt.reactive.cell.test_utils.ConstCellFactory
import dev.toolkt.reactive.cell.test_utils.UpdateVerifier
import dev.toolkt.reactive.cell.test_utils.UpdateVerificationStrategy
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.filter
import dev.toolkt.reactive.event_stream.hold
import dev.toolkt.reactive.event_stream.map
import dev.toolkt.reactive.event_stream.mapNotNull
import kotlin.test.Ignore
import kotlin.test.Test

@Suppress("ClassName")
class Cell_map3_combo_tests {
    private fun test_initial(
        source1ConstCellFactory: ConstCellFactory,
        source2ConstCellFactory: ConstCellFactory,
        source3ConstCellFactory: ConstCellFactory,
        samplingStrategy: CellSamplingStrategy,
    ) {
        val sourceCell1 = MomentContext.execute {
            source1ConstCellFactory.create(10)
        }

        val sourceCell2 = MomentContext.execute {
            source2ConstCellFactory.create('A')
        }

        val sourceCell3 = MomentContext.execute {
            source3ConstCellFactory.create(true)
        }

        val map3Cell = Cell.map3(
            sourceCell1,
            sourceCell2,
            sourceCell3,
        ) { value1, value2, value3 ->
            "$value1:$value2:$value3"
        }

        samplingStrategy.perceive(map3Cell).assertCurrentValueEquals(
            expectedCurrentValue = "10:A:true",
        )
    }

    private fun test_initial(
        samplingStrategy: CellSamplingStrategy,
    ) {
        ConstCellFactory.values.forEach { source1ConstCellFactory ->
            ConstCellFactory.values.forEach { source2ConstCellFactory ->
                ConstCellFactory.values.forEach { source3ConstCellFactory ->
                    test_initial(
                        source1ConstCellFactory = source1ConstCellFactory,
                        source2ConstCellFactory = source2ConstCellFactory,
                        source3ConstCellFactory = source3ConstCellFactory,
                        samplingStrategy = samplingStrategy,
                    )
                }
            }
        }
    }

    @Test
    fun test_initial_passive() {
        test_initial(
            samplingStrategy = CellSamplingStrategy.Passive,
        )
    }

    @Test
    fun test_initial_active() {
        test_initial(
            samplingStrategy = CellSamplingStrategy.Active,
        )
    }

    private fun test_sameSource(
        updateVerificationStrategy: UpdateVerificationStrategy,
    ) {
        val doUpdate = EmitterEventStream<Unit>()

        val sourceCell = MomentContext.execute {
            doUpdate.map { 20 }.hold(
                initialValue = 10,
            )
        }

        val map3Cell = Cell.map3(
            sourceCell,
            sourceCell,
            sourceCell,
        ) { value1, value2, value3 ->
            "$value1:$value2:$value3"
        }

        val updateVerifier = updateVerificationStrategy.begin(
            subjectCell = map3Cell,
        )

        updateVerifier.verifyUpdates(
            doUpdate = doUpdate,
            expectedUpdatedValue = "20:20:20",
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
        val doUpdate = EmitterEventStream<Unit>()

        val sourceCell1 = MomentContext.execute {
            Cell.define(
                initialValue = 10,
                newValues = doUpdate.filter { false },
            )
        }

        val sourceCell2 = MomentContext.execute {
            Cell.define(
                initialValue = 'A',
                newValues = doUpdate.filter { false },
            )
        }

        val sourceCell3 = MomentContext.execute {
            Cell.define(
                initialValue = true,
                newValues = doUpdate.filter { false },
            )
        }

        val map3Cell = Cell.map3(
            sourceCell1,
            sourceCell2,
            sourceCell3,
        ) { value1, value2, value3 ->
            "$value1:$value2:$value3"
        }

        val updateVerifier = updateVerificationStrategy.begin(
            subjectCell = map3Cell,
        )

        updateVerifier.verifyDoesNotUpdate(
            doTrigger = doUpdate,
            expectedNonUpdatedValue = "10:A:true",
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
        source3ConstCellFactory: ConstCellFactory,
        updateVerificationStrategy: UpdateVerificationStrategy,
    ) {
        val doUpdate = EmitterEventStream<Unit>()

        val sourceCell1 = MomentContext.execute {
            doUpdate.map { 20 }.hold(initialValue = 10)
        }

        val sourceCell2 = MomentContext.execute {
            source2ConstCellFactory.create('A')
        }

        val sourceCell3 = MomentContext.execute {
            source3ConstCellFactory.create(true)
        }

        val map3Cell = Cell.map3(
            sourceCell1,
            sourceCell2,
            sourceCell3,
        ) { value1, value2, value3 ->
            "$value1:$value2:$value3"
        }

        val updateVerifier = updateVerificationStrategy.begin(
            subjectCell = map3Cell,
        )

        updateVerifier.verifyUpdates(
            doUpdate = doUpdate,
            expectedUpdatedValue = "20:A:true",
        )
    }

    private fun test_source1Update(
        updateVerificationStrategy: UpdateVerificationStrategy,
    ) {
        ConstCellFactory.values.forEach { source2ConstCellFactory ->
            ConstCellFactory.values.forEach { source3ConstCellFactory ->
                test_source1Update(
                    source2ConstCellFactory = source2ConstCellFactory,
                    source3ConstCellFactory = source3ConstCellFactory,
                    updateVerificationStrategy = updateVerificationStrategy,
                )
            }
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
        UpdateVerificationStrategy.Active.values.forEach { updateVerificationStrategy ->
            test_source1Update(
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }

    @Test
    fun test_source1Update_quick() {
        test_source1Update(
            updateVerificationStrategy = UpdateVerificationStrategy.Quick,
        )
    }

    private fun test_source2Update(
        source1ConstCellFactory: ConstCellFactory,
        source3ConstCellFactory: ConstCellFactory,
        updateVerificationStrategy: UpdateVerificationStrategy,
    ) {
        val doUpdate = EmitterEventStream<Unit>()

        val sourceCell1 = MomentContext.execute {
            source1ConstCellFactory.create(10)
        }

        val sourceCell2 = MomentContext.execute {
            doUpdate.map { 'B' }.hold(initialValue = 'A')
        }

        val sourceCell3 = MomentContext.execute {
            source3ConstCellFactory.create(true)
        }

        val map3Cell = Cell.map3(
            sourceCell1,
            sourceCell2,
            sourceCell3,
        ) { value1, value2, value3 ->
            "$value1:$value2:$value3"
        }

        val updateVerifier = updateVerificationStrategy.begin(
            subjectCell = map3Cell,
        )

        updateVerifier.verifyUpdates(
            doUpdate = doUpdate,
            expectedUpdatedValue = "10:B:true",
        )
    }

    private fun test_source2Update(
        updateVerificationStrategy: UpdateVerificationStrategy,
    ) {
        ConstCellFactory.values.forEach { source1ConstCellFactory ->
            ConstCellFactory.values.forEach { source3ConstCellFactory ->
                test_source2Update(
                    source1ConstCellFactory = source1ConstCellFactory,
                    source3ConstCellFactory = source3ConstCellFactory,
                    updateVerificationStrategy = updateVerificationStrategy,
                )
            }
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

    private fun test_source3Update(
        source1ConstCellFactory: ConstCellFactory,
        source2ConstCellFactory: ConstCellFactory,
        updateVerificationStrategy: UpdateVerificationStrategy,
    ) {
        val doUpdate = EmitterEventStream<Unit>()

        val sourceCell1 = MomentContext.execute {
            source1ConstCellFactory.create(10)
        }

        val sourceCell2 = MomentContext.execute {
            source2ConstCellFactory.create('A')
        }

        val sourceCell3 = MomentContext.execute {
            doUpdate.map { false }.hold(initialValue = true)
        }

        val map3Cell = Cell.map3(
            sourceCell1,
            sourceCell2,
            sourceCell3,
        ) { value1, value2, value3 ->
            "$value1:$value2:$value3"
        }

        val updateVerifier = updateVerificationStrategy.begin(
            subjectCell = map3Cell,
        )

        updateVerifier.verifyUpdates(
            doUpdate = doUpdate,
            expectedUpdatedValue = "10:A:false",
        )
    }

    private fun test_source3Update(
        updateVerificationStrategy: UpdateVerificationStrategy,
    ) {
        ConstCellFactory.values.forEach { source1ConstCellFactory ->
            ConstCellFactory.values.forEach { source2ConstCellFactory ->
                test_source3Update(
                    source1ConstCellFactory = source1ConstCellFactory,
                    source2ConstCellFactory = source2ConstCellFactory,
                    updateVerificationStrategy = updateVerificationStrategy,
                )
            }
        }
    }

    @Test
    fun test_source3Update_passive() {
        test_source3Update(
            updateVerificationStrategy = UpdateVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_source3Update_active() {
        UpdateVerificationStrategy.Active.values.forEach { updateVerificationStrategy ->
            test_source3Update(
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }

    @Test
    fun test_source3Update_quick() {
        test_source3Update(
            updateVerificationStrategy = UpdateVerificationStrategy.Quick,
        )
    }

    @Suppress("SameParameterValue")
    private fun test_mixedUpdates(
        initialSource1Value: Int,
        newSource1Value: Int?,
        initialSource2Value: Char,
        newSource2Value: Char?,
        initialSource3Value: Boolean,
        newSource3Value: Boolean?,
    ) {
        val doUpdate = EmitterEventStream<Unit>()

        val sourceCell1 = MomentContext.execute {
            doUpdate.mapNotNull { newSource1Value }.hold(
                initialValue = initialSource1Value,
            )
        }

        val sourceCell2 = MomentContext.execute {
            doUpdate.mapNotNull { newSource2Value }.hold(
                initialValue = initialSource2Value,
            )
        }

        val sourceCell3 = MomentContext.execute {
            doUpdate.mapNotNull { newSource3Value }.hold(
                initialValue = initialSource3Value,
            )
        }

        val map3Cell = Cell.map3(
            sourceCell1,
            sourceCell2,
            sourceCell3,
        ) { value1, value2, value3 ->
            "$value1:$value2:$value3"
        }

        val updateVerifier = UpdateVerifier.observeActively(
            subjectCell = map3Cell,
        )

        val expectedValue1 = newSource1Value ?: initialSource1Value
        val expectedValue2 = newSource2Value ?: initialSource2Value
        val expectedValue3 = newSource3Value ?: initialSource3Value

        updateVerifier.verifyUpdates(
            doUpdate = doUpdate,
            expectedUpdatedValue = "$expectedValue1:$expectedValue2:$expectedValue3",
        )
    }

    @Test
    fun test_mixedUpdates() {
        val initialSource1Value = 10
        val initialSource2Value = 'A'
        val initialSource3Value = true

        listOf(null, 11).forEach { newSource1Value ->
            listOf(null, 'B').forEach { newSource2Value ->
                listOf(null, false).forEach { newSource3Value ->
                    if (newSource1Value != null || newSource2Value != null || newSource3Value != null) {
                        // At least one source must update

                        test_mixedUpdates(
                            initialSource1Value = initialSource1Value,
                            newSource1Value = newSource1Value,
                            initialSource2Value = initialSource2Value,
                            newSource2Value = newSource2Value,
                            initialSource3Value = initialSource3Value,
                            newSource3Value = newSource3Value,
                        )
                    }
                }
            }
        }
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

        val sourceCell3 = MomentContext.execute {
            Cell.define(
                initialValue = true,
                newValues = doTrigger.map { false },
            )
        }

        val map3Cell = Cell.map3(
            sourceCell1,
            sourceCell2,
            sourceCell3,
        ) { value1, value2, value3 ->
            "$value1:$value2:$value3"
        }

        updateVerificationStrategy.verifyDeactivation(
            subjectCell = map3Cell,
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
