package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.DependencyEventStreamVertex
import dev.toolkt.reactive.cell.vertices.SimpleDerivedEventStreamVertex

class EventStreamMapNotNullVertex<SourceEventT, TransformedEventT : Any>(
    private val sourceEventStreamVertex: DependencyEventStreamVertex<SourceEventT>,
    private val transform: (SourceEventT) -> TransformedEventT?,
) : SimpleDerivedEventStreamVertex<TransformedEventT>() {
    override fun process(
        context: Transaction.ProcessingContext,
        processingMode: ProcessingMode,
    ): EventStreamVertex.Occurrence<TransformedEventT> {
        val sourceOccurrence = sourceEventStreamVertex.pullOccurrence(
            context = context,
            processingMode = processingMode,
        )

        return sourceOccurrence.mapNotNull(
            transform = transform,
        )
    }

    override fun resume() {
        sourceEventStreamVertex.subscribe(
            dependentVertex = this,
        )
    }

    override fun pause() {
        sourceEventStreamVertex.unsubscribe(
            dependentVertex = this,
        )
    }
}
