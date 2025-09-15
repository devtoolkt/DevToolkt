package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.EmitterEventStreamVertex

class EmitterEventStream<EventT> : BaseOperatedEventStream<EventT> {
    /**
     * Emit an event to the stream.
     *
     * This method must be called from outside the reactive system.
     */
    fun emit(
        event: EventT,
    ) {
        vertex.prepareOccurrence(
            event = event,
        )

        Transaction.execute { processingContext ->
            vertex.processDynamic(
                processingContext = processingContext,
            )
        }

        vertex.clearEvent()
    }

    override val vertex = EmitterEventStreamVertex<EventT>()
}
