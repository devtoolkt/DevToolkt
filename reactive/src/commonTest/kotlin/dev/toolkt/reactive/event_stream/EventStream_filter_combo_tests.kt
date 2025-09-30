package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.event_stream.test_utils.OccurrenceVerificationStrategy
import kotlin.test.Test

@Suppress("ClassName")
class EventStream_filter_combo_tests {
    private fun test_sourceOccurrence_passed(
        occurrenceVerificationStrategy: OccurrenceVerificationStrategy,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceEventStream = doTrigger.map { 10 }

        val filterEventStream = sourceEventStream.filter { true }

        val occurrenceVerifier = occurrenceVerificationStrategy.begin(
            subjectEventStream = filterEventStream,
        )

        occurrenceVerifier.verifyOccurrencePropagates(
            doTrigger = doTrigger,
            expectedPropagatedEvent = 10,
        )
    }

    @Test
    fun test_sourceOccurrence_passed() {
        OccurrenceVerificationStrategy.values.forEach { occurrenceVerificationStrategy ->
            test_sourceOccurrence_passed(
                occurrenceVerificationStrategy = occurrenceVerificationStrategy,
            )
        }
    }

    private fun test_sourceOccurrence_filteredOut(
        occurrenceVerificationStrategy: OccurrenceVerificationStrategy,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceEventStream = doTrigger.map { 10 }

        val filterEventStream = sourceEventStream.filter { false }

        val occurrenceVerifier = occurrenceVerificationStrategy.begin(
            subjectEventStream = filterEventStream,
        )

        occurrenceVerifier.verifyOccurrenceDoesNotPropagate(
            doTrigger = doTrigger,
        )
    }

    @Test
    fun test_sourceOccurrence_filteredOut() {
        OccurrenceVerificationStrategy.values.forEach { occurrenceVerificationStrategy ->
            test_sourceOccurrence_filteredOut(
                occurrenceVerificationStrategy = occurrenceVerificationStrategy,
            )
        }
    }

    private fun test_deactivation(
        occurrenceVerificationStrategy: OccurrenceVerificationStrategy,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceEventStream = doTrigger.map { 10 }

        val filterEventStream = sourceEventStream.filter { true }

        occurrenceVerificationStrategy.verifyDeactivation(
            subjectEventStream = filterEventStream,
            doTrigger = doTrigger,
        )
    }

    @Test
    fun test_deactivation() {
        OccurrenceVerificationStrategy.values.forEach { occurrenceVerificationStrategy ->
            test_deactivation(
                occurrenceVerificationStrategy = occurrenceVerificationStrategy,
            )
        }
    }
}
