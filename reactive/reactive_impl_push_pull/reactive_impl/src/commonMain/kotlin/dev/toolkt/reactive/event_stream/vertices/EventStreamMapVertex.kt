package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.Transaction

class EventStreamMapVertex<SourceEventT, TransformedEventT>(
    private val sourceEventStreamVertex: EventStreamVertex<SourceEventT>,
    private val transform: (Transaction.ProcessingContext, SourceEventT) -> TransformedEventT,
) : SimpleDerivedEventStreamVertex<TransformedEventT>() {
    override fun process(
        context: Transaction.ProcessingContext,
        processingMode: ProcessingMode,
    ): EventStreamVertex.Occurrence<TransformedEventT> {
        val sourceOccurrence = sourceEventStreamVertex.pullOccurrence(
            context = context,
            processingMode = processingMode,
        )

        return sourceOccurrence.map {
            transform(context, it)
        }
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
