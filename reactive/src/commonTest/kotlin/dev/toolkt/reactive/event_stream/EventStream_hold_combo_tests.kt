package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.test_utils.CellSamplingStrategy
import dev.toolkt.reactive.cell.test_utils.UpdateVerificationStrategy
import kotlin.test.Test

@Suppress("ClassName")
class EventStream_hold_combo_tests {
    private fun test_initial(
        samplingStrategy: CellSamplingStrategy,
    ) {
        val sourceEventStream = EmitterEventStream<Int>()

        val holdCell = MomentContext.execute {
            sourceEventStream.hold(initialValue = 10)
        }

        samplingStrategy.perceive(holdCell).assertCurrentValueEquals(
            expectedCurrentValue = 10,
        )
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

    private fun test_sourceOccurrence(
        updateVerificationStrategy: UpdateVerificationStrategy,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceEventStream = doTrigger.map { 11 }

        val holdCell = MomentContext.execute {
            sourceEventStream.hold(initialValue = 10)
        }

        val updateVerifier = updateVerificationStrategy.begin(
            subjectCell = holdCell,
        )

        updateVerifier.verifyUpdates(
            doTrigger = doTrigger,
            expectedUpdatedValue = 11,
        )
    }

    @Test
    fun test_sourceOccurrence_passive() {
        test_sourceOccurrence(
            updateVerificationStrategy = UpdateVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_sourceOccurrence_active() {
        UpdateVerificationStrategy.Active.values.forEach { updateVerificationStrategy ->
            test_sourceOccurrence(
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }

    @Test
    fun test_sourceOccurrence_quick() {
        test_sourceOccurrence(
            updateVerificationStrategy = UpdateVerificationStrategy.Quick,
        )
    }

    private fun test_deactivation(
        updateVerificationStrategy: UpdateVerificationStrategy.Active,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceEventStream = doTrigger.map { 11 }

        val holdCell = MomentContext.execute {
            sourceEventStream.hold(initialValue = 10)
        }

        updateVerificationStrategy.verifyDeactivation(
            subjectCell = holdCell,
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
