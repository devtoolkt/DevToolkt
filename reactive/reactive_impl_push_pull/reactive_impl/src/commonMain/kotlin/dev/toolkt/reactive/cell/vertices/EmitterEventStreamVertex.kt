package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex.Occurrence
import dev.toolkt.reactive.event_stream.vertices.StatefulEventStreamVertex

class EmitterEventStreamVertex<EventT>() : StatefulEventStreamVertex<EventT>() {
    private var preparedVolatileOccurrence: Occurrence<EventT>? = null

    fun preProcess(
        preProcessingContext: Transaction.PreProcessingContext,
        event: EventT,
    ) {
        preparedVolatileOccurrence = Occurrence(
            event = event,
        )

        ensureVisited(
            preProcessingContext = preProcessingContext,
        )
    }

    override fun prepare(
        preProcessingContext: Transaction.PreProcessingContext,
    ): Occurrence<EventT>? = preparedVolatileOccurrence

    override fun stabilize(
        postProcessingContext: Transaction.PostProcessingContext,
        message: Occurrence<EventT>?,
    ) {
        preparedVolatileOccurrence = null
    }
}
