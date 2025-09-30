package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.event_stream.test_utils.EventStreamVerificationStrategy
import kotlin.test.Test

@Suppress("ClassName")
class EventStream_filter_combo_tests {
    private fun test_sourceOccurrence_passed(
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceEventStream = doTrigger.map { 10 }

        val filterEventStream = sourceEventStream.filter { true }

        val verifier = verificationStrategy.begin(
            subjectEventStream = filterEventStream,
        )

        verifier.verifyOccurrencePropagates(
            doTrigger = doTrigger,
            expectedPropagatedEvent = 10,
        )
    }

    @Test
    fun test_sourceOccurrence_passed() {
        EventStreamVerificationStrategy.values.forEach { verificationStrategy ->
            test_sourceOccurrence_passed(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_sourceOccurrence_filteredOut(
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceEventStream = doTrigger.map { 10 }

        val filterEventStream = sourceEventStream.filter { false }

        val verifier = verificationStrategy.begin(
            subjectEventStream = filterEventStream,
        )

        verifier.verifyOccurrenceDoesNotPropagate(
            doTrigger = doTrigger,
        )
    }

    @Test
    fun test_sourceOccurrence_filteredOut() {
        EventStreamVerificationStrategy.values.forEach { verificationStrategy ->
            test_sourceOccurrence_filteredOut(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_deactivation(
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceEventStream = doTrigger.map { 10 }

        val filterEventStream = sourceEventStream.filter { true }

        verificationStrategy.verifyDeactivation(
            subjectEventStream = filterEventStream,
            doTrigger = doTrigger,
        )
    }

    @Test
    fun test_deactivation() {
        EventStreamVerificationStrategy.values.forEach { verificationStrategy ->
            test_deactivation(
                verificationStrategy = verificationStrategy,
            )
        }
    }
}
