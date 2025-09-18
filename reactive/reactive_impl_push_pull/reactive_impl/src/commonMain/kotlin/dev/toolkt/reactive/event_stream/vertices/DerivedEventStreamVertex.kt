package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.DependentVertex
import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.Vertex
import dev.toolkt.reactive.cell.vertices.DependencyEventStreamVertex

abstract class DerivedEventStreamVertex<EventT> : BaseEventStreamVertex<EventT>(), DependencyEventStreamVertex<EventT>,
    DependentVertex, Vertex {
    private var cachedOccurrence: EventStreamVertex.Occurrence<EventT>? = null

    final override fun visit(
        context: Transaction.ProcessingContext,
    ) {
        ensureProcessedSubsequently(
            context = context,
        )
    }

    final override fun processSubscribed(
        context: Transaction.ProcessingContext,
        wasFirst: Boolean,
    ): EventStreamVertex.Occurrence<EventT> = when {
        wasFirst -> processResuming(
            context = context,
        )

        else -> processFollowing(
            context = context,
        )
    }

    final override fun processSubsequent(
        context: Transaction.ProcessingContext,
    ): EventStreamVertex.Occurrence<EventT> = processFollowing(
        context = context,
    )

    final override fun onFirstSubscriberAdded() {
        resume()
    }

    final override fun onLastSubscriberRemoved() {
        pause()
    }

    final override fun reset(
        tag: Tag,
    ) {
        cachedOccurrence = null
    }

    final override fun transit() {
    }

    /**
     * Process this vertex and resume it at the same time.
     *
     * This method is called only if the vertex is paused.
     */
    protected abstract fun processResuming(
        context: Transaction.ProcessingContext,
    ): EventStreamVertex.Occurrence<EventT>

    /**
     * Process this vertex.
     *
     * This method is called only if the vertex is resumed.
     */
    protected abstract fun processFollowing(
        context: Transaction.ProcessingContext,
    ): EventStreamVertex.Occurrence<EventT>


    protected abstract fun resume()

    /**
     * Pause this vertex.
     */
    protected abstract fun pause()
}
