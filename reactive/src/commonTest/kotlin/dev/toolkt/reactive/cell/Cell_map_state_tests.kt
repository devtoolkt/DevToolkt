package dev.toolkt.reactive.cell

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.test_utils.CellVerificationStrategy
import dev.toolkt.reactive.cell.test_utils.DynamicCellFactory
import dev.toolkt.reactive.cell.test_utils.FreezingCellFactory
import dev.toolkt.reactive.cell.test_utils.InertCellFactory
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.map
import kotlin.test.Ignore
import kotlin.test.Test

@Suppress("ClassName")
class Cell_map_state_tests {
    private fun test_state_initial(
        sourceCellFactory: InertCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val sourceCell = sourceCellFactory.createInertExternally(10)

        val mapCell = sourceCell.map {
            it.toString()
        }

        val verifier = verificationStrategy.begin(
            subjectCell = mapCell,
        )

        verifier.verifyCurrentValue(
            expectedCurrentValue = "10",
        )
    }

    private fun test_state_initial(
        verificationStrategy: CellVerificationStrategy,
    ) {
        InertCellFactory.values.forEach { sourceCellFactory ->
            test_state_initial(
                sourceCellFactory = sourceCellFactory,
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_state_initial_passive() {
        test_state_initial(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Ignore // FIXME: Subscription should not be null.
    @Test
    fun test_state_initial_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_state_initial(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_state_sourceUpdate(
        sourceCellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doUpdate = EmitterEventStream<Unit>()

        val sourceCell = sourceCellFactory.createDynamicExternally(
            initialValue = 10,
            doUpdate = doUpdate.map { 20 },
        )

        val mapCell = sourceCell.map { it.toString() }

        val verifier = verificationStrategy.begin(
            subjectCell = mapCell,
        )

        verifier.verifyUpdates(
            doTriggerUpdate = doUpdate,
            expectedUpdatedValue = "20",
        )
    }

    private fun test_state_sourceUpdate(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { sourceCellFactory ->
            test_state_sourceUpdate(
                sourceCellFactory = sourceCellFactory,
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_state_sourceUpdate_passive() {
        test_state_sourceUpdate(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_state_sourceUpdate_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_state_sourceUpdate(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_state_sourceUpdate_quick() {
        test_state_sourceUpdate(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    private fun test_state_sourceFreeze(
        sourceCellFactory: FreezingCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doFreeze = EmitterEventStream<Unit>()

        val sourceCell = MomentContext.execute {
            sourceCellFactory.create(
                value = 10,
                doFreeze = doFreeze,
            )
        }

        val mapCell = sourceCell.map { it.toString() }

        verificationStrategy.verifyCompleteFreeze(
            subjectCell = mapCell,
            doFreeze = doFreeze,
            expectedFrozenValue = "10",
        )
    }

    private fun test_state_sourceFreeze(
        verificationStrategy: CellVerificationStrategy,
    ) {
        FreezingCellFactory.values.forEach { sourceCellFactory ->
            test_state_sourceFreeze(
                sourceCellFactory = sourceCellFactory,
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_state_sourceFreeze_passive() {
        test_state_sourceFreeze(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_state_sourceFreeze_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_state_sourceFreeze(
                verificationStrategy = verificationStrategy,
            )
        }
    }
}
