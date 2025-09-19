package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex.NilOccurrence
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex.Occurrence

class EventStreamFilterVertex<EventT>(
    private val sourceEventStreamVertex: EventStreamVertex<EventT>,
    private val predicate: (EventT) -> Boolean,
) : SimpleDerivedEventStreamVertex<EventT>() {
    override fun process(
        context: Transaction.ProcessingContext,
        processingMode: ProcessingMode,
    ): Occurrence<EventT> {
        val sourceOccurrence = sourceEventStreamVertex.pullOccurrence(
            context = context,
            processingMode = processingMode,
        )

        return when (sourceOccurrence) {
            is EventStreamVertex.EffectiveOccurrence -> when {
                predicate(sourceOccurrence.event) -> sourceOccurrence

                else -> NilOccurrence
            }

            NilOccurrence -> NilOccurrence
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
