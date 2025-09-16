package dev.toolkt.reactive

import dev.toolkt.reactive.cell.vertices.DependencyEventStreamVertex
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex

abstract class IntermediateEventStreamVertex<EventT> : BaseDependencyVertex(), DependencyEventStreamVertex<EventT>,
    DependentVertex, ResettableVertex {
    private var mutableIsProcessed = false

    private val isProcessed: Boolean
        get() = mutableIsProcessed

    private var mutableCachedEmittedEvent: EventStreamVertex.EmittedEvent<EventT>? = null

    private val cachedEmittedEvent: EventStreamVertex.EmittedEvent<EventT>?
        get() = mutableCachedEmittedEvent

    final override fun visit(
        context: Transaction.Context,
    ) {
        ensureProcessed(
            context = context,
        )
    }

    final override fun pullEmittedEvent(
        context: Transaction.Context,
    ): EventStreamVertex.EmittedEvent<EventT>? = ensureProcessed(
        context = context,
    )

    protected fun ensureProcessed(
        context: Transaction.Context,
    ): EventStreamVertex.EmittedEvent<EventT>? {
        if (isProcessed) {
            return cachedEmittedEvent
        }

        val computedEmittedEvent = process(
            context = context,
        )

        mutableIsProcessed = true
        mutableCachedEmittedEvent = computedEmittedEvent

        context.enqueueDirtyVertex(
            dirtyVertex = this,
        )

        if (computedEmittedEvent != null) {
            enqueueDependentsForVisiting(
                context = context,
            )
        }

        return computedEmittedEvent
    }

    final override fun reset() {
        val emittedEvent = this.cachedEmittedEvent

        clearEmittedEventCache()

        if (emittedEvent != null) {
            transit()
        }
    }

    protected fun clearEmittedEventCache() {
        mutableIsProcessed = false
        mutableCachedEmittedEvent = null
    }

    protected abstract fun process(
        context: Transaction.Context,
    ): EventStreamVertex.EmittedEvent<EventT>?

    protected abstract fun transit()
}
