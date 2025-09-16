package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.BaseDependencyVertex
import dev.toolkt.reactive.ResettableVertex
import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex.EmittedEvent

class EmitterEventStreamVertex<EventT>() : BaseDependencyVertex(), DependencyEventStreamVertex<EventT>,
    ResettableVertex {
    private var emittedEmittedEvent: EmittedEvent<EventT>? = null

    fun emit(
        processingContext: Transaction.ProcessingContext,
        event: EventT,
    ) {
        emittedEmittedEvent = EmittedEvent(
            event = event,
        )

        processingContext.enqueueDirtyVertex(
            dirtyVertex = this,
        )

        enqueueDependentsForVisiting(
            processingContext = processingContext,
        )
    }

    override fun pullEmittedEvent(
        processingContext: Transaction.ProcessingContext,
    ): EmittedEvent<EventT>? = emittedEmittedEvent

    override fun onFirstDependentAdded() {
    }

    override fun onLastDependentRemoved() {
    }

    override fun reset() {
        emittedEmittedEvent = null
    }
}
