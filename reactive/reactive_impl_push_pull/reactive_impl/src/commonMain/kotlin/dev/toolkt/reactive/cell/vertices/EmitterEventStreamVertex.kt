package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.BaseDependencyVertex
import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex.Occurrence

class EmitterEventStreamVertex<EventT>() : BaseDependencyVertex(), DependencyEventStreamVertex<EventT> {
    private var emittedOccurrence: Occurrence<EventT>? = null

    fun emit(
        processingContext: Transaction.ProcessingContext,
        event: EventT,
    ) {
        emittedOccurrence = Occurrence(
            event = event,
        )

        ensureMarkedDirty(
            processingContext = processingContext,
        )

        enqueueDependentsForVisiting(
            processingContext = processingContext,
        )
    }

    override fun pullOccurrence(
        processingContext: Transaction.ProcessingContext,
    ): Occurrence<EventT>? = emittedOccurrence

    override fun onFirstDependentAdded() {
    }

    override fun onLastDependentRemoved() {
    }

    override fun clean() {
        emittedOccurrence = null
    }
}
