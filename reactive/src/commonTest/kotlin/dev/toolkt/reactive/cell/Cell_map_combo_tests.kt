package dev.toolkt.reactive.cell

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.test_utils.CellSamplingStrategy
import dev.toolkt.reactive.cell.test_utils.ConstCellFactory
import dev.toolkt.reactive.cell.test_utils.UpdateVerificationStrategy
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.map
import kotlin.test.Test

@Suppress("ClassName")
class Cell_map_combo_tests {
    private fun test_initial(
        sourceConstCellFactory: ConstCellFactory,
        samplingStrategy: CellSamplingStrategy,
    ) {
        val sourceCell = MomentContext.execute {
            sourceConstCellFactory.create(10)
        }

        val mapCell = sourceCell.map {
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
        updateVerificationStrategy: UpdateVerificationStrategy,
    ) {
        val doUpdate = EmitterEventStream<Unit>()

        val sourceCell = MomentContext.execute {
            Cell.define(
                initialValue = 10,
                newValues = doUpdate.map { 20 },
            )
        }

        val mapCell = sourceCell.map { it.toString() }

        val updateVerifier = updateVerificationStrategy.begin(
            subjectCell = mapCell,
        )

        updateVerifier.verifyUpdates(
            doUpdate = doUpdate,
            expectedUpdatedValue = "20",
        )
    }

    @Test
    fun test_sourceUpdate_passive() {
        test_sourceUpdate(
            updateVerificationStrategy = UpdateVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_sourceUpdate_active() {
        UpdateVerificationStrategy.Active.values.forEach { updateVerificationStrategy ->
            test_sourceUpdate(
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }

    @Test
    fun test_sourceUpdate_quick() {
        test_sourceUpdate(
            updateVerificationStrategy = UpdateVerificationStrategy.Quick,
        )
    }

    private fun test_deactivation(
        updateVerificationStrategy: UpdateVerificationStrategy.Active,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceCell = MomentContext.execute {
            Cell.define(
                initialValue = 10,
                newValues = doTrigger.map { 11 },
            )
        }

        val mapCell = sourceCell.map { it.toString() }

        updateVerificationStrategy.verifyDeactivation(
            subjectCell = mapCell,
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
