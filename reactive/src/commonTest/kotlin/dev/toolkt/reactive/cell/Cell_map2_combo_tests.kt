package dev.toolkt.reactive.cell

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.test_utils.CellObservationStrategy
import dev.toolkt.reactive.cell.test_utils.CellSamplingStrategy
import dev.toolkt.reactive.cell.test_utils.ConstCellFactory
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.hold
import dev.toolkt.reactive.event_stream.map
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

    private fun test_source1Update(
        source2ConstCellFactory: ConstCellFactory,
        observationStrategy: CellObservationStrategy,
    ) {
        val doUpdate = EmitterEventStream<Int>()

        val sourceCell1 = MomentContext.execute {
            doUpdate.hold(
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

        val asserter = observationStrategy.observeForTesting(
            trigger = doUpdate,
            cell = map2Cell,
        )

        doUpdate.emit(20)

        asserter.assertUpdatedValueEquals(
            expectedUpdatedValue = "20:A",
        )
    }

    @Test
    fun test_source1Update_source2Inert_passive() {
        test_source1Update(
            source2ConstCellFactory = ConstCellFactory.Inert,
            observationStrategy = CellObservationStrategy.Passive,
        )
    }

    @Test
    fun test_source1Update_source2Inert_activeUpdatedValues() {
        test_source1Update(
            source2ConstCellFactory = ConstCellFactory.Inert,
            observationStrategy = CellObservationStrategy.ActiveUpdatedValues,
        )
    }

    @Test
    fun test_source1Update_source2Inert_activeNewValues() {
        test_source1Update(
            source2ConstCellFactory = ConstCellFactory.Inert,
            observationStrategy = CellObservationStrategy.ActiveNewValues,
        )
    }

    @Test
    fun test_source1Update_source2Inert_activeSwitch() {
        test_source1Update(
            source2ConstCellFactory = ConstCellFactory.Inert,
            observationStrategy = CellObservationStrategy.ActiveSwitch,
        )
    }

    @Test
    fun test_source1Update_source2Dynamic_passive() {
        test_source1Update(
            source2ConstCellFactory = ConstCellFactory.Dynamic,
            observationStrategy = CellObservationStrategy.Passive,
        )
    }

    @Test
    fun test_source1Update_source2Dynamic_activeUpdatedValues() {
        test_source1Update(
            source2ConstCellFactory = ConstCellFactory.Dynamic,
            observationStrategy = CellObservationStrategy.ActiveUpdatedValues,
        )
    }

    @Test
    fun test_source1Update_source2Dynamic_activeNewValues() {
        test_source1Update(
            source2ConstCellFactory = ConstCellFactory.Dynamic,
            observationStrategy = CellObservationStrategy.ActiveNewValues,
        )
    }

    @Test
    fun test_source1Update_source2Dynamic_activeSwitch() {
        test_source1Update(
            source2ConstCellFactory = ConstCellFactory.Dynamic,
            observationStrategy = CellObservationStrategy.ActiveSwitch,
        )
    }

    private fun test_source2Update(
        source1ConstCellFactory: ConstCellFactory,
        observationStrategy: CellObservationStrategy,
    ) {
        val doUpdate = EmitterEventStream<Char>()

        val sourceCell1 = MomentContext.execute {
            source1ConstCellFactory.create(10)
        }

        val sourceCell2 = MomentContext.execute {
            doUpdate.hold(
                initialValue = 'A',
            )
        }

        val map2Cell = Cell.map2(
            sourceCell1,
            sourceCell2,
        ) { value1, value2 ->
            "$value1:$value2"
        }

        val asserter = observationStrategy.observeForTesting(
            trigger = doUpdate,
            cell = map2Cell,
        )

        doUpdate.emit('B')

        asserter.assertUpdatedValueEquals(
            expectedUpdatedValue = "10:B",
        )
    }

    @Test
    fun test_source2Update_source1Inert_passive() {
        test_source2Update(
            source1ConstCellFactory = ConstCellFactory.Inert,
            observationStrategy = CellObservationStrategy.Passive,
        )
    }

    @Test
    fun test_source2Update_source1Inert_activeUpdatedValues() {
        test_source2Update(
            source1ConstCellFactory = ConstCellFactory.Inert,
            observationStrategy = CellObservationStrategy.ActiveUpdatedValues,
        )
    }

    @Test
    fun test_source2Update_source1Inert_activeNewValues() {
        test_source2Update(
            source1ConstCellFactory = ConstCellFactory.Inert,
            observationStrategy = CellObservationStrategy.ActiveNewValues,
        )
    }

    @Test
    fun test_source2Update_source1Inert_activeSwitch() {
        test_source2Update(
            source1ConstCellFactory = ConstCellFactory.Inert,
            observationStrategy = CellObservationStrategy.ActiveSwitch,
        )
    }

    @Test
    fun test_source2Update_source1Dynamic_passive() {
        test_source2Update(
            source1ConstCellFactory = ConstCellFactory.Dynamic,
            observationStrategy = CellObservationStrategy.Passive,
        )
    }

    @Test
    fun test_source2Update_source1Dynamic_activeUpdatedValues() {
        test_source2Update(
            source1ConstCellFactory = ConstCellFactory.Dynamic,
            observationStrategy = CellObservationStrategy.ActiveUpdatedValues,
        )
    }

    @Test
    fun test_source2Update_source1Dynamic_activeNewValues() {
        test_source2Update(
            source1ConstCellFactory = ConstCellFactory.Dynamic,
            observationStrategy = CellObservationStrategy.ActiveNewValues,
        )
    }

    @Test
    fun test_source2Update_source1Dynamic_activeSwitch() {
        test_source2Update(
            source1ConstCellFactory = ConstCellFactory.Dynamic,
            observationStrategy = CellObservationStrategy.ActiveSwitch,
        )
    }

    private fun test_simultaneousUpdates(
        observationStrategy: CellObservationStrategy,
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

        val asserter = observationStrategy.observeForTesting(
            trigger = doUpdate,
            cell = map2Cell,
        )

        doUpdate.emit(Unit)

        asserter.assertUpdatedValueEquals(
            expectedUpdatedValue = "11:B",
        )
    }

    @Test
    fun test_simultaneousUpdates_passive() {
        test_simultaneousUpdates(
            observationStrategy = CellObservationStrategy.Passive,
        )
    }

    @Test
    fun test_simultaneousUpdates_activeUpdatedValues() {
        test_simultaneousUpdates(
            observationStrategy = CellObservationStrategy.ActiveUpdatedValues,
        )
    }

    @Test
    fun test_simultaneousUpdates_activeNewValues() {
        test_simultaneousUpdates(
            observationStrategy = CellObservationStrategy.ActiveNewValues,
        )
    }

    @Test
    fun test_simultaneousUpdates_activeSwitch() {
        test_simultaneousUpdates(
            observationStrategy = CellObservationStrategy.ActiveSwitch,
        )
    }
}
