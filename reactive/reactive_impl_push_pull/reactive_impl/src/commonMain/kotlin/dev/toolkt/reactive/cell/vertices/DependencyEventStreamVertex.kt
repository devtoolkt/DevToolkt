package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.DependencyVertex
import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex.Occurrence

interface DependencyEventStreamVertex<EventT> : EventStreamVertex<EventT>, DependencyVertex {
    /**
     * Returns the occurrence of this event stream, triggering processing if necessary.
     */
    fun pullOccurrence(
        processingContext: Transaction.ProcessingContext,
    ): Occurrence<EventT>?
}
