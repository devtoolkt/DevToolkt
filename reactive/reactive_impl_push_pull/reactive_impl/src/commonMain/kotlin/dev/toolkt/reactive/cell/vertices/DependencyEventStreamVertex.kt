package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.DependentVertex
import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex.Occurrence

interface DependencyEventStreamVertex<EventT> : EventStreamVertex<EventT> {
    /**
     * Subscribe the [dependentVertex] to this vertex and pull an occurrence.
     *
     * If [dependentVertex] is the first dependent and this vertex supports resuming/pausing, this vertex will be
     * resumed.
     */
    fun pullOccurrenceSubscribing(
        context: Transaction.ProcessingContext,
        dependentVertex: DependentVertex,
    ): Occurrence<EventT>

    /**
     * Pull an occurrence from this vertex.
     *
     * It's required that this vertex has at least one dependent (if it supports resuming/pausing, it's required
     * that this vertex is resumed).
     */
    fun pullOccurrenceSubsequent(
        context: Transaction.ProcessingContext,
    ): Occurrence<EventT>

    fun subscribe(
        dependentVertex: DependentVertex,
    )

    /**
     * Unsubscribe the [dependentVertex] from this vertex.
     *
     * If [dependentVertex] was the last dependent and this vertex supports resuming/pausing, this vertex will be
     * deactivated.
     */
    fun unsubscribe(
        dependentVertex: DependentVertex,
    )
}
