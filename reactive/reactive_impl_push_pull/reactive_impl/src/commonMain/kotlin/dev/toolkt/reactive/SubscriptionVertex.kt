package dev.toolkt.reactive

import dev.toolkt.reactive.cell.vertices.DynamicEventStreamVertex
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex

class SubscriptionVertex<EventT>(
    private val sourceEventStreamVertex: DynamicEventStreamVertex<EventT>,
    private val handle: (EventT) -> Unit,
) : OperativeVertex() {
    private var receivedEventOccurrence: EventStreamVertex.Occurrence<EventT>? = null

    override fun prepare(
        preProcessingContext: Transaction.PreProcessingContext,
    ) {
        val occurrence = sourceEventStreamVertex.pullOccurrence(
            preProcessingContext = preProcessingContext,
        ) ?: return

        receivedEventOccurrence = occurrence
    }

    override fun affect(
        interProcessingContext: Transaction.InterProcessingContext,
    ) {
        receivedEventOccurrence?.let {
            handle(it.event)
        }
    }

    override fun settle(
        stabilizationContext: Transaction.StabilizationContext,
    ) {
        receivedEventOccurrence = null
    }
}
