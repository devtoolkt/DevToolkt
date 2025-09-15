package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.DependencyEventStreamVertex

class EventStreamFilterVertex<SourceEventT>(
    private val sourceEventStreamVertex: DependencyEventStreamVertex<SourceEventT>,
    private val predicate: (SourceEventT) -> Boolean,
) : StatelessEventStreamVertex<SourceEventT>() {
    override fun prepare(
        preProcessingContext: Transaction.PreProcessingContext,
    ): EventStreamVertex.Occurrence<SourceEventT>? {
        val sourceOccurrence = sourceEventStreamVertex.pullOccurrence(
            preProcessingContext = preProcessingContext,
        ) ?: return null

        return when {
            predicate(sourceOccurrence.event) -> sourceOccurrence
            else -> null
        }
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
