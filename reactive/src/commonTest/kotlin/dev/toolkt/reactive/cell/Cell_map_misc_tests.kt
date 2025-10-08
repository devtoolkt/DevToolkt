package dev.toolkt.reactive.cell

import dev.toolkt.reactive.cell.test_utils.CellVerificationStrategy
import dev.toolkt.reactive.cell.test_utils.DynamicCellFactory
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.map
import kotlin.test.Test

@Suppress("ClassName")
class Cell_map_misc_tests {
    private fun test_state_sourceFilteredOut(
        sourceCellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceCell = sourceCellFactory.createFilteredOutExternally(
            initialValue = 10,
            doTrigger = doTrigger,
        )

        val mapCell = sourceCell.map { it.toString() }

        val verifier = verificationStrategy.begin(
            subjectCell = mapCell,
        )

        verifier.verifyDoesNotUpdate(
            doTriggerPotentialUpdate = doTrigger,
            expectedNonUpdatedValue = "10",
        )
    }

    private fun test_state_sourceFilteredOut(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { sourceCellFactory ->
            test_state_sourceFilteredOut(
                sourceCellFactory = sourceCellFactory,
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_state_sourceFilteredOut_passive() {
        test_state_sourceFilteredOut(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_state_sourceFilteredOut_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_state_sourceFilteredOut(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_state_sourceFilteredOut_quick() {
        test_state_sourceFilteredOut(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    private fun test_deactivation(
        sourceCellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy.Active,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceCell = sourceCellFactory.createDynamicExternally(
            initialValue = 10,
            doUpdate = doTrigger.map { 11 },
        )

        val mapCell = sourceCell.map { it.toString() }

        verificationStrategy.verifyDeactivation(
            subjectCell = mapCell,
            doTrigger = doTrigger,
        )
    }

    private fun test_deactivation(
        verificationStrategy: CellVerificationStrategy.Active,
    ) {
        DynamicCellFactory.values.forEach { sourceCellFactory ->
            test_deactivation(
                sourceCellFactory = sourceCellFactory,
                verificationStrategy = verificationStrategy,
            )
        }
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
