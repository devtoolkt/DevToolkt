package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.DependencyEventStreamVertex

class EventStreamFilterVertex<SourceEventT>(
    private val sourceEventStreamVertex: DependencyEventStreamVertex<SourceEventT>,
    private val predicate: (SourceEventT) -> Boolean,
) : DerivedEventStreamVertex<SourceEventT>() {
    override fun process(
        context: Transaction.Context,
    ): EventStreamVertex.EmittedEvent<SourceEventT>? {
        val sourceOccurrence = sourceEventStreamVertex.pullEmittedEvent(
            context = context,
        ) ?: return null

        return when {
            predicate(sourceOccurrence.event) -> sourceOccurrence
            else -> null
        }
    }

    override fun resume() {
        sourceEventStreamVertex.addDependent(
            dependentVertex = this,
        )
    }

    override fun pause() {
        sourceEventStreamVertex.removeDependent(
            dependentVertex = this,
        )
    }
}
