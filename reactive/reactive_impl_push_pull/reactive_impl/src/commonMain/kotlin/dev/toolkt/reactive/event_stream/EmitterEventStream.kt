package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.EmitterEventStreamVertex

class EmitterEventStream<EventT> : OperatedEventStream<EventT> {
    /**
     * Emit an event to the stream.
     *
     * This method must be called from outside the reactive system.
     */
    fun emit(
        event: EventT,
    ) {
        Transaction.execute { processingContext ->
            vertex.preProcess(
                processingContext = processingContext,
                event = event,
            )
        }
    }

    override val vertex = EmitterEventStreamVertex<EventT>()
}
