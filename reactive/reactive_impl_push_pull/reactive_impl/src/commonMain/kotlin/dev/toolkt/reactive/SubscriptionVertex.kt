package dev.toolkt.reactive

import dev.toolkt.reactive.cell.vertices.DynamicEventStreamVertex
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex

class SubscriptionVertex<EventT>(
    private val sourceEventStreamVertex: DynamicEventStreamVertex<EventT>,
    private val handle: (EventT) -> Unit,
) : OperativeVertex() {
    private var receivedEventOccurrence: EventStreamVertex.Occurrence<EventT>? = null

    override fun visit(
        processingContext: Transaction.ProcessingContext,
    ) {
        receivedEventOccurrence = sourceEventStreamVertex.pullOccurrence(
            processingContext = processingContext,
        ) ?: return
    }

    override fun affect(
        earlyPostProcessingContext: Transaction.EarlyPostProcessingContext,
    ) {
        receivedEventOccurrence?.let {
            handle(it.event)
        }
    }

    override fun settle(
        postProcessingContext: Transaction.PostProcessingContext,
    ) {
        receivedEventOccurrence = null
    }
}
