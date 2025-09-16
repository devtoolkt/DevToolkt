package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.DependencyEventStreamVertex

class EventStreamMerge2Vertex<EventT>(
    private val sourceEventStream1Vertex: DependencyEventStreamVertex<EventT>,
    private val sourceEventStream2Vertex: DependencyEventStreamVertex<EventT>,
) : StatelessEventStreamVertex<EventT>() {
    override fun prepare(
        processingContext: Transaction.ProcessingContext,
    ): EventStreamVertex.Occurrence<EventT>? {
        val sourceOccurrence1 = sourceEventStream1Vertex.pullOccurrence(
            processingContext = processingContext,
        )

        if (sourceOccurrence1 != null) {
            return sourceOccurrence1
        }

        val sourceOccurrence2 = sourceEventStream2Vertex.pullOccurrence(
            processingContext = processingContext,
        )

        if (sourceOccurrence2 != null) {
            return sourceOccurrence2
        }

        return null
    }

    override fun resume(
        expansionContext: Transaction.ExpansionContext,
    ) {
        sourceEventStream1Vertex.addDependent(
            expansionContext = expansionContext,
            vertex = this,
        )

        sourceEventStream2Vertex.addDependent(
            expansionContext = expansionContext,
            vertex = this,
        )
    }

    override fun pause(
        shrinkageContext: Transaction.ShrinkageContext,
    ) {
        sourceEventStream1Vertex.removeDependent(
            shrinkageContext = shrinkageContext,
            vertex = this,
        )

        sourceEventStream2Vertex.removeDependent(
            shrinkageContext = shrinkageContext,
            vertex = this,
        )
    }
}
