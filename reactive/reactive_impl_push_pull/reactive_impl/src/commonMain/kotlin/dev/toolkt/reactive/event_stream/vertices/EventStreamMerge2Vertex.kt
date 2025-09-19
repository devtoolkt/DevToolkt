package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.SimpleDerivedEventStreamVertex
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex.NilOccurrence

class EventStreamMerge2Vertex<EventT>(
    private val sourceEventStream1Vertex: EventStreamVertex<EventT>,
    private val sourceEventStream2Vertex: EventStreamVertex<EventT>,
) : SimpleDerivedEventStreamVertex<EventT>() {
    override fun process(
        context: Transaction.ProcessingContext,
        processingMode: ProcessingMode,
    ): EventStreamVertex.Occurrence<EventT> {
        val sourceOccurrence1 = sourceEventStream1Vertex.pullOccurrence(
            context = context,
            processingMode = processingMode,
        )

        if (sourceOccurrence1 != NilOccurrence) {
            return sourceOccurrence1
        }

        val sourceOccurrence2 = sourceEventStream2Vertex.pullOccurrence(
            context = context,
            processingMode = processingMode,
        )

        if (sourceOccurrence2 != NilOccurrence) {
            return sourceOccurrence2
        }

        return NilOccurrence
    }

    override fun resume() {
        sourceEventStream1Vertex.subscribe(
            dependentVertex = this,
        )

        sourceEventStream2Vertex.subscribe(
            dependentVertex = this,
        )
    }

    override fun pause() {
        sourceEventStream1Vertex.unsubscribe(
            dependentVertex = this,
        )

        sourceEventStream2Vertex.unsubscribe(
            dependentVertex = this,
        )
    }
}
