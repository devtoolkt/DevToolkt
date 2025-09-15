package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex.Occurrence
import dev.toolkt.reactive.event_stream.vertices.StatelessEventStreamVertex

class EmitterEventStreamVertex<EventT>() : StatelessEventStreamVertex<EventT>() {
    private var preparedVolatileOccurrence: Occurrence<EventT>? = null

    override fun prepareMessage(
        preProcessingContext: Transaction.PreProcessingContext,
    ): Occurrence<EventT>? = preparedVolatileOccurrence

    fun prepareOccurrence(
        event: EventT,
    ) {
        if (preparedVolatileOccurrence != null) {
            throw IllegalStateException("There is already a pending prepared occurrence $preparedVolatileOccurrence")
        }

        preparedVolatileOccurrence = Occurrence(
            event = event,
        )
    }

    fun clearEvent() {
        preparedVolatileOccurrence = null
    }

    override fun activate(
        expansionContext: Transaction.ExpansionContext,
    ) {
        // The emitter vertex doesn't have dependencies
    }

    override fun deactivate(
        shrinkageContext: Transaction.ShrinkageContext,
    ) {
        // The emitter vertex doesn't have dependencies
    }
}
