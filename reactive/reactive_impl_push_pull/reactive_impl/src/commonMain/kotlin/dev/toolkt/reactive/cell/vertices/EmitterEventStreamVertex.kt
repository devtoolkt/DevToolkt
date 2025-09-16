package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex.Occurrence
import dev.toolkt.reactive.event_stream.vertices.StatefulEventStreamVertex

class EmitterEventStreamVertex<EventT>() : StatefulEventStreamVertex<EventT>() {
    private var preparedVolatileOccurrence: Occurrence<EventT>? = null

    fun preProcess(
        processingContext: Transaction.ProcessingContext,
        event: EventT,
    ) {
        preparedVolatileOccurrence = Occurrence(
            event = event,
        )

        ensureVisited(
            processingContext = processingContext,
        )
    }

    override fun prepare(
        processingContext: Transaction.ProcessingContext,
    ): Occurrence<EventT>? = preparedVolatileOccurrence

    override fun stabilize(
        latePostProcessingContext: Transaction.LatePostProcessingContext,
        message: Occurrence<EventT>?,
    ) {
        preparedVolatileOccurrence = null
    }
}
