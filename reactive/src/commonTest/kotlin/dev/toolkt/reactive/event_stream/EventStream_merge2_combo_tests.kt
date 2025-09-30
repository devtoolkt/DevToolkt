package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.event_stream.test_utils.OccurrenceVerificationStrategy
import kotlin.test.Ignore
import kotlin.test.Test

@Suppress("ClassName")
class EventStream_merge2_combo_tests {
    private fun test_sameSource(
        occurrenceVerificationStrategy: OccurrenceVerificationStrategy,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceEventStream = doTrigger.map { 11 }

        val merge2EventStream = EventStream.merge2(
            sourceEventStream,
            sourceEventStream,
        )

        val updateVerifier = occurrenceVerificationStrategy.begin(
            subjectEventStream = merge2EventStream,
        )

        updateVerifier.verifyOccurrencePropagates(
            doTrigger = doTrigger,
            expectedPropagatedEvent = 11,
        )
    }

    @Ignore // FIXME: Flaky test
    @Test
    fun test_sameSource() {
        OccurrenceVerificationStrategy.values.forEach { occurrenceVerificationStrategy ->
            test_sameSource(
                occurrenceVerificationStrategy,
            )
        }
    }

    private fun test_source1Occurrence(
        occurrenceVerificationStrategy: OccurrenceVerificationStrategy,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceEventStream1 = doTrigger.map { 11 }

        val sourceEventStream2 = EmitterEventStream<Int>()

        val merge2EventStream = EventStream.merge2(
            sourceEventStream1,
            sourceEventStream2,
        )

        val updateVerifier = occurrenceVerificationStrategy.begin(
            subjectEventStream = merge2EventStream,
        )

        updateVerifier.verifyOccurrencePropagates(
            doTrigger = doTrigger,
            expectedPropagatedEvent = 11,
        )
    }

    @Test
    fun test_source1Occurrence() {
        OccurrenceVerificationStrategy.values.forEach { occurrenceVerificationStrategy ->
            test_source1Occurrence(
                occurrenceVerificationStrategy = occurrenceVerificationStrategy,
            )
        }
    }

    private fun test_source2Occurrence(
        occurrenceVerificationStrategy: OccurrenceVerificationStrategy,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceEventStream1 = EmitterEventStream<Int>()

        val sourceEventStream2 = doTrigger.map { 21 }

        val merge2EventStream = EventStream.merge2(
            sourceEventStream1,
            sourceEventStream2,
        )

        val updateVerifier = occurrenceVerificationStrategy.begin(
            subjectEventStream = merge2EventStream,
        )

        updateVerifier.verifyOccurrencePropagates(
            doTrigger = doTrigger,
            expectedPropagatedEvent = 21,
        )
    }

    @Test
    fun test_source2Occurrence() {
        OccurrenceVerificationStrategy.values.forEach { occurrenceVerificationStrategy ->
            test_source2Occurrence(
                occurrenceVerificationStrategy = occurrenceVerificationStrategy,
            )
        }
    }

    private fun test_simultaneousOccurrences(
        occurrenceVerificationStrategy: OccurrenceVerificationStrategy,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceEventStream1 = doTrigger.map { 11 }

        val sourceEventStream2 = doTrigger.map { 21 }

        val merge2EventStream = EventStream.merge2(
            sourceEventStream1,
            sourceEventStream2,
        )

        val updateVerifier = occurrenceVerificationStrategy.begin(
            subjectEventStream = merge2EventStream,
        )

        updateVerifier.verifyOccurrencePropagates(
            doTrigger = doTrigger,
            expectedPropagatedEvent = 11,
        )
    }

    @Test
    fun test_simultaneousOccurrences_active() {
        OccurrenceVerificationStrategy.values.forEach { occurrenceVerificationStrategy ->
            test_simultaneousOccurrences(
                occurrenceVerificationStrategy = occurrenceVerificationStrategy,
            )
        }
    }

    private fun test_deactivation(
        occurrenceVerificationStrategy: OccurrenceVerificationStrategy,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceEventStream1 = doTrigger.map { 11 }

        val sourceEventStream2 = doTrigger.map { 21 }

        val merge2EventStream = EventStream.merge2(
            sourceEventStream1,
            sourceEventStream2,
        )

        occurrenceVerificationStrategy.verifyDeactivation(
            subjectEventStream = merge2EventStream,
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
