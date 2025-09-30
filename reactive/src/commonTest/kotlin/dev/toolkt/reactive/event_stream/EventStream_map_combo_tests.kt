package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.event_stream.test_utils.OccurrenceVerificationStrategy
import kotlin.test.Test
import kotlin.test.assertNull

@Suppress("ClassName")
class EventStream_map_combo_tests {
    @Test
    fun test_silentSource() {
        // TODO: Silent source factory?

        val sourceEventStream: EventStream<Any> = NeverEventStream

        val mapEventStream = sourceEventStream.map { it.toString() }

        assertNull(
            mapEventStream.subscribe { },
        )
    }

    private fun test_sourceOccurrence(
        occurrenceVerificationStrategy: OccurrenceVerificationStrategy,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceEventStream = doTrigger.map { 10 }

        val mapEventStream = sourceEventStream.map { it.toString() }

        val occurrenceVerifier = occurrenceVerificationStrategy.begin(
            subjectEventStream = mapEventStream,
        )

        occurrenceVerifier.verifyOccurrencePropagates(
            doTrigger = doTrigger,
            expectedPropagatedEvent = "10",
        )
    }

    @Test
    fun test_sourceOccurrence() {
        OccurrenceVerificationStrategy.values.forEach { occurrenceVerificationStrategy ->
            test_sourceOccurrence(
                occurrenceVerificationStrategy = occurrenceVerificationStrategy,
            )
        }
    }

    private fun test_deactivation(
        occurrenceVerificationStrategy: OccurrenceVerificationStrategy,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceEventStream = doTrigger.map { 10 }

        val mapEventStream = sourceEventStream.map { it.toString() }

        occurrenceVerificationStrategy.verifyDeactivation(
            subjectEventStream = mapEventStream,
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
