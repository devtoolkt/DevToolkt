package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.DependencyEventStreamVertex
import dev.toolkt.reactive.cell.vertices.DynamicEventStreamVertex

class EventStreamMapNotNullVertex<SourceEventT, TransformedEventT : Any>(
    private val sourceEventStreamVertex: DependencyEventStreamVertex<SourceEventT>,
    private val transform: (SourceEventT) -> TransformedEventT?,
) : StatelessEventStreamVertex<TransformedEventT>() {
    override fun prepare(
        preProcessingContext: Transaction.PreProcessingContext,
    ): EventStreamVertex.Occurrence<TransformedEventT>? {
        val sourceOccurrence = sourceEventStreamVertex.pullOccurrence(
            preProcessingContext = preProcessingContext,
        )

        return sourceOccurrence?.mapNotNull(
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
