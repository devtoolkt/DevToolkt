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
        processingContext: Transaction.ProcessingContext,
    ) {
        ensureProcessed(
            processingContext = processingContext,
        )
    }

    final override fun pullEmittedEvent(
        processingContext: Transaction.ProcessingContext,
    ): EventStreamVertex.EmittedEvent<EventT>? = ensureProcessed(
        processingContext = processingContext,
    )

    protected fun ensureProcessed(
        processingContext: Transaction.ProcessingContext,
    ): EventStreamVertex.EmittedEvent<EventT>? {
        if (isProcessed) {
            return cachedEmittedEvent
        }

        val computedEmittedEvent = process(
            processingContext = processingContext,
        )

        mutableIsProcessed = true
        mutableCachedEmittedEvent = computedEmittedEvent

        processingContext.enqueueDirtyVertex(
            dirtyVertex = this,
        )

        if (computedEmittedEvent != null) {
            enqueueDependentsForVisiting(
                processingContext = processingContext,
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
        processingContext: Transaction.ProcessingContext,
    ): EventStreamVertex.EmittedEvent<EventT>?

    protected abstract fun transit()
}
