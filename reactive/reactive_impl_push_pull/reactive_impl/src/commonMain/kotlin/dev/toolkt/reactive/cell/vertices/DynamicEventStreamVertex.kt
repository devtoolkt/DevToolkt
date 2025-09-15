package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.DynamicVertex
import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex.Occurrence

interface DynamicEventStreamVertex<EventT> : EventStreamVertex<EventT>, DynamicVertex {
    /**
     * Returns the occurrence of this event stream, triggering processing if necessary.
     */
    fun pullOccurrence(
        preProcessingContext: Transaction.PreProcessingContext,
    ): Occurrence<EventT>?
}
