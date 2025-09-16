package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.DependencyEventStreamVertex

class EventStreamMerge2Vertex<EventT>(
    private val sourceEventStream1Vertex: DependencyEventStreamVertex<EventT>,
    private val sourceEventStream2Vertex: DependencyEventStreamVertex<EventT>,
) : StatelessEventStreamVertex<EventT>() {
    override fun process(
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

    override fun resume() {
        sourceEventStream1Vertex.addDependent(
            dependentVertex = this,
        )

        sourceEventStream2Vertex.addDependent(
            dependentVertex = this,
        )
    }

    override fun pause() {
        sourceEventStream1Vertex.removeDependent(
            dependentVertex = this,
        )

        sourceEventStream2Vertex.removeDependent(
            dependentVertex = this,
        )
    }
}
