package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.event_stream.test_utils.EventStreamVerificationStrategy
import kotlin.test.Ignore
import kotlin.test.Test

@Suppress("ClassName")
class EventStream_merge2_combo_tests {
    private fun test_sameSource(
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceEventStream = doTrigger.map { 11 }

        val merge2EventStream = EventStream.merge2(
            sourceEventStream,
            sourceEventStream,
        )

        val verifier = verificationStrategy.begin(
            subjectEventStream = merge2EventStream,
        )

        verifier.verifyOccurrencePropagates(
            doTrigger = doTrigger,
            expectedPropagatedEvent = 11,
        )
    }

    @Ignore // FIXME: Flaky test
    @Test
    fun test_sameSource() {
        EventStreamVerificationStrategy.values.forEach { verificationStrategy ->
            test_sameSource(
                verificationStrategy,
            )
        }
    }

    private fun test_source1Occurrence(
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceEventStream1 = doTrigger.map { 11 }

        val sourceEventStream2 = EmitterEventStream<Int>()

        val merge2EventStream = EventStream.merge2(
            sourceEventStream1,
            sourceEventStream2,
        )

        val verifier = verificationStrategy.begin(
            subjectEventStream = merge2EventStream,
        )

        verifier.verifyOccurrencePropagates(
            doTrigger = doTrigger,
            expectedPropagatedEvent = 11,
        )
    }

    @Test
    fun test_source1Occurrence() {
        EventStreamVerificationStrategy.values.forEach { verificationStrategy ->
            test_source1Occurrence(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_source2Occurrence(
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceEventStream1 = EmitterEventStream<Int>()

        val sourceEventStream2 = doTrigger.map { 21 }

        val merge2EventStream = EventStream.merge2(
            sourceEventStream1,
            sourceEventStream2,
        )

        val verifier = verificationStrategy.begin(
            subjectEventStream = merge2EventStream,
        )

        verifier.verifyOccurrencePropagates(
            doTrigger = doTrigger,
            expectedPropagatedEvent = 21,
        )
    }

    @Test
    fun test_source2Occurrence() {
        EventStreamVerificationStrategy.values.forEach { verificationStrategy ->
            test_source2Occurrence(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_simultaneousOccurrences(
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceEventStream1 = doTrigger.map { 11 }

        val sourceEventStream2 = doTrigger.map { 21 }

        val merge2EventStream = EventStream.merge2(
            sourceEventStream1,
            sourceEventStream2,
        )

        val verifier = verificationStrategy.begin(
            subjectEventStream = merge2EventStream,
        )

        verifier.verifyOccurrencePropagates(
            doTrigger = doTrigger,
            expectedPropagatedEvent = 11,
        )
    }

    @Test
    fun test_simultaneousOccurrences_active() {
        EventStreamVerificationStrategy.values.forEach { verificationStrategy ->
            test_simultaneousOccurrences(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_pausing(
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceEventStream1 = doTrigger.map { 11 }

        val sourceEventStream2 = doTrigger.map { 21 }

        val merge2EventStream = EventStream.merge2(
            sourceEventStream1,
            sourceEventStream2,
        )

        verificationStrategy.verifyPausing(
            subjectEventStream = merge2EventStream,
            doTrigger = doTrigger,
        )
    }

    @Test
    fun test_pausing() {
        EventStreamVerificationStrategy.values.forEach { verificationStrategy ->
            test_pausing(
                verificationStrategy = verificationStrategy,
            )
        }
    }
}
