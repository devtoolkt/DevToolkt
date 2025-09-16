package dev.toolkt.reactive

import dev.toolkt.reactive.cell.vertices.DynamicEventStreamVertex
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex

class SubscriptionVertex<EventT>(
    private val sourceEventStreamVertex: DynamicEventStreamVertex<EventT>,
    private val handle: (EventT) -> Unit,
) : DynamicVertex {
    private var mutableReceivedEventOccurrence: EventStreamVertex.Occurrence<EventT>? = null

    val receivedEventOccurrence: EventStreamVertex.Occurrence<EventT>?
        get() = mutableReceivedEventOccurrence

    override fun visit(
        processingContext: Transaction.ProcessingContext,
    ) {
        val receivedEventOccurrence = sourceEventStreamVertex.pullOccurrence(
            processingContext = processingContext,
        ) ?: return

        processingContext.enqueueForPostProcessing(
            processedVertex = this,
        )

        mutableReceivedEventOccurrence = receivedEventOccurrence
    }

    override fun postProcessEarly(
        earlyPostProcessingContext: Transaction.EarlyPostProcessingContext,
    ) {
        receivedEventOccurrence?.let {
            handle(it.event)
        }
    }

    override fun postProcessLate(
        latePostProcessingContext: Transaction.LatePostProcessingContext,
    ) {
        mutableReceivedEventOccurrence = null
    }
}
