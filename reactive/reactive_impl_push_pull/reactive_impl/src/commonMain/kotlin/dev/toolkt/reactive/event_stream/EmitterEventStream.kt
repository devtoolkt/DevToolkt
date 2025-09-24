package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.event_stream.vertices.EmitterEventStreamVertex

class EmitterEventStream<EventT> : EventStream<EventT> {
    /**
     * Emit an event to the stream.
     *
     * This method must be called from outside the reactive system.
     */
    fun emit(
        event: EventT,
    ) {
        Transaction.execute { context ->
            vertex.emit(
                context = context,
                event = event,
            )
        }
    }

    override val vertex = EmitterEventStreamVertex<EventT>()
}
