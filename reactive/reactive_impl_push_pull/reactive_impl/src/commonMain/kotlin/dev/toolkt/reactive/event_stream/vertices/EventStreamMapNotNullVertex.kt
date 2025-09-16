package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.DependencyEventStreamVertex

class EventStreamMapNotNullVertex<SourceEventT, TransformedEventT : Any>(
    private val sourceEventStreamVertex: DependencyEventStreamVertex<SourceEventT>,
    private val transform: (SourceEventT) -> TransformedEventT?,
) : StatelessEventStreamVertex<TransformedEventT>() {
    override fun process(
        processingContext: Transaction.ProcessingContext,
    ): EventStreamVertex.Occurrence<TransformedEventT>? {
        val sourceOccurrence = sourceEventStreamVertex.pullOccurrence(
            processingContext = processingContext,
        )

        return sourceOccurrence?.mapNotNull(
            transform = transform,
        )
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
