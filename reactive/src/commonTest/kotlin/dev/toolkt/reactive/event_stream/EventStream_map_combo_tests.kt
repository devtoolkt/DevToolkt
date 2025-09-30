package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.event_stream.test_utils.EventStreamVerificationStrategy
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
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceEventStream = doTrigger.map { 10 }

        val mapEventStream = sourceEventStream.map { it.toString() }

        val verifier = verificationStrategy.begin(
            subjectEventStream = mapEventStream,
        )

        verifier.verifyOccurrencePropagates(
            doTrigger = doTrigger,
            expectedPropagatedEvent = "10",
        )
    }

    @Test
    fun test_sourceOccurrence() {
        EventStreamVerificationStrategy.values.forEach { verificationStrategy ->
            test_sourceOccurrence(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_deactivation(
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceEventStream = doTrigger.map { 10 }

        val mapEventStream = sourceEventStream.map { it.toString() }

        verificationStrategy.verifyDeactivation(
            subjectEventStream = mapEventStream,
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
