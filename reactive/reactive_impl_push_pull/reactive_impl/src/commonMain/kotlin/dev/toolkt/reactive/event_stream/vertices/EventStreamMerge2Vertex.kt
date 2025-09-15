package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.DependencyEventStreamVertex

class EventStreamMerge2Vertex<EventT>(
    private val sourceEventStream1Vertex: DependencyEventStreamVertex<EventT>,
    private val sourceEventStream2Vertex: DependencyEventStreamVertex<EventT>,
) : StatelessEventStreamVertex<EventT>() {
    override fun prepare(
        preProcessingContext: Transaction.PreProcessingContext,
    ): EventStreamVertex.Occurrence<EventT>? {
        val sourceOccurrence1 = sourceEventStream1Vertex.pullOccurrence(
            preProcessingContext = preProcessingContext,
        )

        if (sourceOccurrence1 != null) {
            return sourceOccurrence1
        }

        val sourceOccurrence2 = sourceEventStream2Vertex.pullOccurrence(
            preProcessingContext = preProcessingContext,
        )

        if (sourceOccurrence2 != null) {
            return sourceOccurrence2
        }

        return null
    }

    override fun onFirstDependentAdded(
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

    override fun onLastDependentRemoved(
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
