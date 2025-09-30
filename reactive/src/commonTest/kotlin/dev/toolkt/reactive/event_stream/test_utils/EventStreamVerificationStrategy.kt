package dev.toolkt.reactive.event_stream.test_utils

import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.EventStream

sealed class EventStreamVerificationStrategy {
    data object Direct : EventStreamVerificationStrategy() {
        override fun <EventT> begin(
            subjectEventStream: EventStream<EventT>,
        ): EventStreamVerifier<EventT> = EventStreamVerifier.observeDirectly(
            subjectEventStream = subjectEventStream,
        )
    }

    data object ViaDivert : EventStreamVerificationStrategy() {
        override fun <EventT> begin(
            subjectEventStream: EventStream<EventT>,
        ): EventStreamVerifier<EventT> = EventStreamVerifier.observeViaDivert(
            subjectEventStream = subjectEventStream,
        )
    }

    companion object {
        val values by lazy {
            listOf(
                Direct,
                ViaDivert,
            )
        }
    }

    fun <EventT> verifyPausing(
        subjectEventStream: EventStream<EventT>,
        doTrigger: EmitterEventStream<Unit>,
    ) {
        val verifier = begin(
            subjectEventStream = subjectEventStream,
        )

        verifier.pause()

        verifier.verifyOccurrenceDoesNotPropagate(
            doTrigger = doTrigger,
        )
    }

    abstract fun <EventT> begin(
        subjectEventStream: EventStream<EventT>,
    ): EventStreamVerifier<EventT>
}
