package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex.Occurrence
import dev.toolkt.reactive.event_stream.vertices.PropagativeEventStreamVertex

class EmitterEventStreamVertex<EventT>() : PropagativeEventStreamVertex<EventT>() {
    private var preparedVolatileOccurrence: Occurrence<EventT>? = null

    override fun prepare(
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

    override fun onFirstDependentAdded(
        expansionContext: Transaction.ExpansionContext,
    ) {
    }

    override fun onLastDependentRemoved(
        shrinkageContext: Transaction.ShrinkageContext,
    ) {
    }

    override fun stabilize(
        postProcessingContext: Transaction.PostProcessingContext,
        message: Occurrence<EventT>?,
    ) {
        preparedVolatileOccurrence = null
    }
}
