package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex.Occurrence
import dev.toolkt.reactive.event_stream.vertices.StatefulEventStreamVertex

class EmitterEventStreamVertex<EventT>() : StatefulEventStreamVertex<EventT>() {
    private var preparedOccurrence: Occurrence<EventT>? = null

    fun preProcess(
        processingContext: Transaction.ProcessingContext,
        event: EventT,
    ) {
        preparedOccurrence = Occurrence(
            event = event,
        )

        ensureEffectivelyProcessed(
            processingContext = processingContext,
        )
    }

    override fun prepare(
        processingContext: Transaction.ProcessingContext,
    ): Occurrence<EventT>? = preparedOccurrence

    override fun stabilize(
        latePostProcessingContext: Transaction.LatePostProcessingContext,
        message: Occurrence<EventT>?,
    ) {
        preparedOccurrence = null
    }
}
