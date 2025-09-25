package dev.toolkt.reactive.cell

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.test_utils.CellObservationStrategy
import dev.toolkt.reactive.cell.test_utils.CellSamplingStrategy
import dev.toolkt.reactive.cell.test_utils.ConstCellFactory
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.hold
import dev.toolkt.reactive.event_stream.mapNotNull
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

    private fun test_source1Update(
        source2ConstCellFactory: ConstCellFactory,
        source3ConstCellFactory: ConstCellFactory,
        observationStrategy: CellObservationStrategy,
    ) {
        val doUpdate = EmitterEventStream<Int>()

        val sourceCell1 = MomentContext.execute {
            doUpdate.hold(initialValue = 10)
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

        val asserter = observationStrategy.observeForTesting(
            trigger = doUpdate,
            cell = map3Cell,
        )

        doUpdate.emit(20)

        asserter.assertUpdatedValueEquals(
            expectedUpdatedValue = "20:A:true",
        )
    }

    private fun test_source1Update(
        observationStrategy: CellObservationStrategy,
    ) {
        ConstCellFactory.values.forEach { source2ConstCellFactory ->
            ConstCellFactory.values.forEach { source3ConstCellFactory ->
                test_source1Update(
                    source2ConstCellFactory = source2ConstCellFactory,
                    source3ConstCellFactory = source3ConstCellFactory,
                    observationStrategy = observationStrategy,
                )
            }
        }
    }

    @Test
    fun test_source1Update_passive() {
        test_source1Update(
            observationStrategy = CellObservationStrategy.Passive,
        )
    }

    @Test
    fun test_source1Update_activeUpdatedValues() {
        test_source1Update(
            observationStrategy = CellObservationStrategy.ActiveUpdatedValues,
        )
    }

    @Test
    fun test_source1Update_activeNewValues() {
        test_source1Update(
            observationStrategy = CellObservationStrategy.ActiveNewValues,
        )
    }

    @Test
    fun test_source1Update_activeSwitch() {
        test_source1Update(
            observationStrategy = CellObservationStrategy.ActiveSwitch,
        )
    }

    private fun test_source2Update(
        source1ConstCellFactory: ConstCellFactory,
        source3ConstCellFactory: ConstCellFactory,
        observationStrategy: CellObservationStrategy,
    ) {
        val doUpdate = EmitterEventStream<Char>()

        val sourceCell1 = MomentContext.execute {
            source1ConstCellFactory.create(10)
        }

        val sourceCell2 = MomentContext.execute {
            doUpdate.hold(initialValue = 'A')
        }

        val sourceCell3 = MomentContext.execute {
            source3ConstCellFactory.create(true)
        }

        val map3Cell = Cell.map3(
            sourceCell1,
            sourceCell2,
            sourceCell3,
        ) { v1, v2, v3 ->
            "$v1:$v2:$v3"
        }

        val asserter = observationStrategy.observeForTesting(
            trigger = doUpdate,
            cell = map3Cell,
        )

        doUpdate.emit('B')

        asserter.assertUpdatedValueEquals(
            expectedUpdatedValue = "10:B:true",
        )
    }

    private fun test_source2Update(
        observationStrategy: CellObservationStrategy,
    ) {
        ConstCellFactory.values.forEach { source1ConstCellFactory ->
            ConstCellFactory.values.forEach { source3ConstCellFactory ->
                test_source2Update(
                    source1ConstCellFactory = source1ConstCellFactory,
                    source3ConstCellFactory = source3ConstCellFactory,
                    observationStrategy = observationStrategy,
                )
            }
        }
    }

    @Test
    fun test_source2Update_passive() {
        test_source2Update(
            observationStrategy = CellObservationStrategy.Passive,
        )
    }

    @Test
    fun test_source2Update_activeUpdatedValues() {
        test_source2Update(
            observationStrategy = CellObservationStrategy.ActiveUpdatedValues,
        )
    }

    @Test
    fun test_source2Update_activeNewValues() {
        test_source2Update(
            observationStrategy = CellObservationStrategy.ActiveNewValues,
        )
    }

    @Test
    fun test_source2Update_activeSwitch() {
        test_source2Update(
            observationStrategy = CellObservationStrategy.ActiveSwitch,
        )
    }

    private fun test_source3Update(
        source1ConstCellFactory: ConstCellFactory,
        source2ConstCellFactory: ConstCellFactory,
        observationStrategy: CellObservationStrategy,
    ) {
        val doUpdate = EmitterEventStream<Boolean>()

        val sourceCell1 = MomentContext.execute {
            source1ConstCellFactory.create(10)
        }

        val sourceCell2 = MomentContext.execute {
            source2ConstCellFactory.create('A')
        }

        val sourceCell3 = MomentContext.execute {
            doUpdate.hold(initialValue = true)
        }

        val map3Cell = Cell.map3(
            sourceCell1,
            sourceCell2,
            sourceCell3,
        ) { v1, v2, v3 ->
            "$v1:$v2:$v3"
        }

        val asserter = observationStrategy.observeForTesting(
            trigger = doUpdate,
            cell = map3Cell,
        )

        doUpdate.emit(false)

        asserter.assertUpdatedValueEquals(
            expectedUpdatedValue = "10:A:false",
        )
    }

    private fun test_source3Update(
        observationStrategy: CellObservationStrategy,
    ) {
        ConstCellFactory.values.forEach { source1ConstCellFactory ->
            ConstCellFactory.values.forEach { source2ConstCellFactory ->
                test_source3Update(
                    source1ConstCellFactory = source1ConstCellFactory,
                    source2ConstCellFactory = source2ConstCellFactory,
                    observationStrategy = observationStrategy,
                )
            }
        }
    }

    @Test
    fun test_source3Update_passive() {
        test_source3Update(
            observationStrategy = CellObservationStrategy.Passive,
        )
    }

    @Test
    fun test_source3Update_activeUpdatedValues() {
        test_source3Update(
            observationStrategy = CellObservationStrategy.ActiveUpdatedValues,
        )
    }

    @Test
    fun test_source3Update_activeNewValues() {
        test_source3Update(
            observationStrategy = CellObservationStrategy.ActiveNewValues,
        )
    }

    @Test
    fun test_source3Update_activeSwitch() {
        test_source3Update(
            observationStrategy = CellObservationStrategy.ActiveSwitch,
        )
    }

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
            Cell.define(
                initialValue = initialSource1Value,
                newValues = doUpdate.mapNotNull { newSource1Value },
            )
        }

        val sourceCell2 = MomentContext.execute {
            Cell.define(
                initialValue = initialSource2Value,
                newValues = doUpdate.mapNotNull { newSource2Value },
            )
        }

        val sourceCell3 = MomentContext.execute {
            Cell.define(
                initialValue = initialSource3Value,
                newValues = doUpdate.mapNotNull { newSource3Value },
            )
        }

        val map3Cell = Cell.map3(
            sourceCell1,
            sourceCell2,
            sourceCell3,
        ) { value1, value2, value3 ->
            "$value1:$value2:$value3"
        }

        val asserter = CellObservationStrategy.ActiveUpdatedValues.observeForTesting(
            trigger = doUpdate,
            cell = map3Cell,
        )

        doUpdate.emit(Unit)

        val expectedValue1 = newSource1Value ?: initialSource1Value
        val expectedValue2 = newSource2Value ?: initialSource2Value
        val expectedValue3 = newSource3Value ?: initialSource3Value

        asserter.assertUpdatedValueEquals(
            expectedUpdatedValue = "$expectedValue1:$expectedValue2:$expectedValue3",
        )
    }

    @Test
    fun test_mixedUpdates_all() {
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
}
