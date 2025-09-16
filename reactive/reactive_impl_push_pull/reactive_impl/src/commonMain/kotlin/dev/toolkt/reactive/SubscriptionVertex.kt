package dev.toolkt.reactive

import dev.toolkt.reactive.cell.vertices.DynamicEventStreamVertex
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex

class SubscriptionVertex<EventT>(
    private val sourceEventStreamVertex: DynamicEventStreamVertex<EventT>,
    private val handle: (EventT) -> Unit,
) : OperativeVertex() {
    private var mutableReceivedEventOccurrence: EventStreamVertex.Occurrence<EventT>? = null

    val receivedEventOccurrence: EventStreamVertex.Occurrence<EventT>?
        get() = mutableReceivedEventOccurrence

    override fun visit(
        processingContext: Transaction.ProcessingContext,
    ) {
        val receivedEventOccurrence = sourceEventStreamVertex.pullOccurrence(
            processingContext = processingContext,
        ) ?: return

        mutableReceivedEventOccurrence = receivedEventOccurrence

        ensureMarkedDirty(
            processingContext = processingContext,
        )
    }

    override fun postProcessEarlyOp(
        earlyPostProcessingContext: Transaction.EarlyPostProcessingContext,
    ) {
        receivedEventOccurrence?.let {
            handle(it.event)
        }
    }

    override fun postProcessLateOp(
        latePostProcessingContext: Transaction.LatePostProcessingContext,
    ) {
        mutableReceivedEventOccurrence = null
    }
}
