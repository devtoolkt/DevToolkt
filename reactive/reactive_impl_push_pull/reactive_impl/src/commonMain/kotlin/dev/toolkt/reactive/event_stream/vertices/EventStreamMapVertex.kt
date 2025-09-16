package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.DependencyEventStreamVertex

class EventStreamMapVertex<SourceEventT, TransformedEventT>(
    private val sourceEventStreamVertex: DependencyEventStreamVertex<SourceEventT>,
    private val transform: (Transaction.ProcessingContext, SourceEventT) -> TransformedEventT,
) : StatelessEventStreamVertex<TransformedEventT>() {
    override fun prepare(
        processingContext: Transaction.ProcessingContext,
    ): EventStreamVertex.Occurrence<TransformedEventT>? {
        val sourceOccurrence = sourceEventStreamVertex.pullOccurrence(
            processingContext = processingContext,
        )

        return sourceOccurrence?.map {
            transform(processingContext, it)
        }
    }

    override fun resume(
        expansionContext: Transaction.ExpansionContext,
    ) {
        sourceEventStreamVertex.addDependent(
            expansionContext = expansionContext,
            vertex = this,
        )
    }

    override fun pause(
        shrinkageContext: Transaction.ShrinkageContext,
    ) {
        sourceEventStreamVertex.removeDependent(
            shrinkageContext = shrinkageContext,
            vertex = this,
        )
    }
}
