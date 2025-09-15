package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.DynamicEventStreamVertex

class EventStreamMapVertex<SourceEventT, TransformedEventT>(
    private val sourceEventStreamVertex: DynamicEventStreamVertex<SourceEventT>,
    private val transform: (SourceEventT) -> TransformedEventT,
) : StatelessEventStreamVertex<TransformedEventT>() {
    override fun prepare(
        processingContext: Transaction.ProcessingContext,
    ): EventStreamVertex.Occurrence<TransformedEventT>? {
        val sourceOccurrence = sourceEventStreamVertex.pullOccurrence(
            processingContext = processingContext,
        )

        return sourceOccurrence?.map(
            transform = transform,
        )
    }

    override fun activate(
        expansionContext: Transaction.ExpansionContext,
    ) {
        sourceEventStreamVertex.addDependent(
            expansionContext = expansionContext,
            vertex = this,
        )
    }

    override fun deactivate(
        shrinkageContext: Transaction.ShrinkageContext,
    ) {
        sourceEventStreamVertex.removeDependent(
            shrinkageContext = shrinkageContext,
            vertex = this,
        )
    }
}
