package dev.toolkt.reactive.event_stream.test_utils

import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.EventStream

sealed class OccurrenceVerificationStrategy {
    data object Direct : OccurrenceVerificationStrategy() {
        override fun <EventT> begin(
            subjectEventStream: EventStream<EventT>,
        ): OccurrenceVerifier<EventT> = OccurrenceVerifier.observeDirectly(
            subjectEventStream = subjectEventStream,
        )
    }

    data object ViaDivert : OccurrenceVerificationStrategy() {
        override fun <EventT> begin(
            subjectEventStream: EventStream<EventT>,
        ): OccurrenceVerifier<EventT> = OccurrenceVerifier.observeViaDivert(
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

    fun <EventT> verifyDeactivation(
        subjectEventStream: EventStream<EventT>,
        doTrigger: EmitterEventStream<Unit>,
    ) {
        val occurrenceVerifier = begin(
            subjectEventStream = subjectEventStream,
        )

        occurrenceVerifier.end()

        occurrenceVerifier.verifyOccurrenceDidNotPropagate(
            doTrigger = doTrigger,
        )
    }

    abstract fun <EventT> begin(
        subjectEventStream: EventStream<EventT>,
    ): OccurrenceVerifier<EventT>
}
