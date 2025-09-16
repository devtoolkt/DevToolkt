package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.BaseDependencyVertex
import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex.EmittedEvent

class EmitterEventStreamVertex<EventT>() : BaseDependencyVertex(), DependencyEventStreamVertex<EventT> {
    private var sourceEmittedEvent: EmittedEvent<EventT>? = null

    override fun pullEmittedEvent(
        context: Transaction.Context,
    ): EmittedEvent<EventT>? = sourceEmittedEvent

    override fun onFirstDependentAdded() {
    }

    override fun onLastDependentRemoved() {
    }

    fun emit(
        context: Transaction.Context,
        event: EventT,
    ) {
        sourceEmittedEvent = EmittedEvent(
            event = event,
        )

        enqueueDependentsForVisiting(
            context = context,
        )
    }

    fun reset() {
        sourceEmittedEvent = null
    }
}
