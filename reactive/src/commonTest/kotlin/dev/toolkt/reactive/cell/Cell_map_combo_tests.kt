package dev.toolkt.reactive.cell

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.test_utils.CellObservationStrategy
import dev.toolkt.reactive.cell.test_utils.CellSamplingStrategy
import dev.toolkt.reactive.cell.test_utils.ConstCellFactory
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.hold
import kotlin.test.Test

@Suppress("ClassName")
class Cell_map_combo_tests {
    private fun test_initial(
        sourceConstCellFactory: ConstCellFactory,
        samplingStrategy: CellSamplingStrategy,
    ) {
        val mapCell = sourceConstCellFactory.create(10).map {
            it.toString()
        }

        samplingStrategy.perceive(mapCell).assertCurrentValueEquals(
            expectedCurrentValue = "10",
        )
    }

    @Test
    fun test_initial_sourceInert_passive() {
        test_initial(
            sourceConstCellFactory = ConstCellFactory.Inert,
            samplingStrategy = CellSamplingStrategy.Passive,
        )
    }

    @Test
    fun test_initial_sourceInert_active() {
        test_initial(
            sourceConstCellFactory = ConstCellFactory.Inert,
            samplingStrategy = CellSamplingStrategy.Active,
        )
    }

    @Test
    fun test_initial_sourceDynamic_passive() {
        test_initial(
            sourceConstCellFactory = ConstCellFactory.Dynamic,
            samplingStrategy = CellSamplingStrategy.Passive,
        )
    }

    @Test
    fun test_initial_sourceDynamic_active() {
        test_initial(
            sourceConstCellFactory = ConstCellFactory.Dynamic,
            samplingStrategy = CellSamplingStrategy.Active,
        )
    }

    private fun test_sourceUpdate(
        observationStrategy: CellObservationStrategy,
    ) {
        val doUpdate = EmitterEventStream<Int>()

        val sourceCell = MomentContext.execute {
            doUpdate.hold(
                initialValue = 10,
            )
        }

        val mapCell = sourceCell.map { it.toString() }

        val asserter = observationStrategy.observe(
            trigger = doUpdate,
            cell = mapCell,
        )

        doUpdate.emit(20)

        asserter.assertUpdatedValueEquals(
            expectedUpdatedValue = "20",
        )
    }

    @Test
    fun test_sourceUpdate_passive() {
        test_sourceUpdate(
            observationStrategy = CellObservationStrategy.Passive,
        )
    }

    @Test
    fun test_sourceUpdate_activeUpdatedValues() {
        test_sourceUpdate(
            observationStrategy = CellObservationStrategy.ActiveUpdatedValues,
        )
    }

    @Test
    fun test_sourceUpdate_activeNewValues() {
        test_sourceUpdate(
            observationStrategy = CellObservationStrategy.ActiveNewValues,
        )
    }

    @Test
    fun test_sourceUpdate_activeSwitch() {
        test_sourceUpdate(
            observationStrategy = CellObservationStrategy.ActiveSwitch,
        )
    }
}
