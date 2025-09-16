package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.DependencyEventStreamVertex

class EventStreamMapVertex<SourceEventT, TransformedEventT>(
    private val sourceEventStreamVertex: DependencyEventStreamVertex<SourceEventT>,
    private val transform: (Transaction.Context, SourceEventT) -> TransformedEventT,
) : DerivedEventStreamVertex<TransformedEventT>() {
    override fun process(
        context: Transaction.Context,
    ): EventStreamVertex.EmittedEvent<TransformedEventT>? {
        val sourceOccurrence = sourceEventStreamVertex.pullEmittedEvent(
            context = context,
        )

        return sourceOccurrence?.map {
            transform(context, it)
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
