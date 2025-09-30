package dev.toolkt.reactive.cell

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.test_utils.CellVerificationStrategy
import dev.toolkt.reactive.cell.test_utils.ConstCellFactory
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.map
import kotlin.test.Test

@Suppress("ClassName")
class Cell_map_combo_tests {
    private fun test_initial(
        sourceConstCellFactory: ConstCellFactory,
        verificationStrategy: CellVerificationStrategy.Total,
    ) {
        val sourceCell = MomentContext.execute {
            sourceConstCellFactory.create(10)
        }

        val mapCell = sourceCell.map {
            it.toString()
        }

        verificationStrategy.begin(
            subjectCell = mapCell,
        ).verifyCurrentValue(
            expectedCurrentValue = "10",
        )
    }

    private fun test_initial(
        verificationStrategy: CellVerificationStrategy.Total,
    ) {
        ConstCellFactory.values.forEach { sourceConstCellFactory ->
            test_initial(
                sourceConstCellFactory = sourceConstCellFactory,
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_initial_passive() {
        test_initial(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_initial_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_initial(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_sourceUpdate(
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doUpdate = EmitterEventStream<Unit>()

        val sourceCell = MomentContext.execute {
            Cell.define(
                initialValue = 10,
                newValues = doUpdate.map { 20 },
            )
        }

        val mapCell = sourceCell.map { it.toString() }

        val verifier = verificationStrategy.begin(
            subjectCell = mapCell,
        )

        verifier.verifyUpdates(
            doTrigger = doUpdate,
            expectedUpdatedValue = "20",
        )
    }

    @Test
    fun test_sourceUpdate_passive() {
        test_sourceUpdate(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_sourceUpdate_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_sourceUpdate(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_sourceUpdate_quick() {
        test_sourceUpdate(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    private fun test_deactivation(
        verificationStrategy: CellVerificationStrategy.Active,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceCell = MomentContext.execute {
            Cell.define(
                initialValue = 10,
                newValues = doTrigger.map { 11 },
            )
        }

        val mapCell = sourceCell.map { it.toString() }

        verificationStrategy.verifyDeactivation(
            subjectCell = mapCell,
            doTrigger = doTrigger,
        )
    }

    @Test
    fun test_deactivation() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_deactivation(
                verificationStrategy = verificationStrategy,
            )
        }
    }
}
