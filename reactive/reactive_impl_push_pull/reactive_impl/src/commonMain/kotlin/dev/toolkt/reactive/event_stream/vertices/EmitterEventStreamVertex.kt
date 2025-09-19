package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex.EffectiveOccurrence
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex.Occurrence

class EmitterEventStreamVertex<EventT>() : InherentEventStreamVertex<EventT>() {
    private var sourceOccurrence: Occurrence<EventT> = EventStreamVertex.NilOccurrence

    fun emit(
        context: Transaction.ProcessingContext,
        event: EventT,
    ) {
        sourceOccurrence = EffectiveOccurrence(
            event = event,
        )

        ensureProcessedTriggered(
            context = context,
        )
    }

    override fun reset(
        tag: Tag,
    ) {
        sourceOccurrence = EventStreamVertex.NilOccurrence
    }

    override fun transit() {
    }

    override fun process(
        context: Transaction.ProcessingContext,
    ): Occurrence<EventT> = sourceOccurrence
}
