package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.DependencyEventStreamVertex

class EventStreamFilterVertex<SourceEventT>(
    private val sourceEventStreamVertex: DependencyEventStreamVertex<SourceEventT>,
    private val predicate: (SourceEventT) -> Boolean,
) : StatelessEventStreamVertex<SourceEventT>() {
    override fun prepare(
        processingContext: Transaction.ProcessingContext,
    ): EventStreamVertex.Occurrence<SourceEventT>? {
        val sourceOccurrence = sourceEventStreamVertex.pullOccurrence(
            processingContext = processingContext,
        ) ?: return null

        return when {
            predicate(sourceOccurrence.event) -> sourceOccurrence
            else -> null
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
