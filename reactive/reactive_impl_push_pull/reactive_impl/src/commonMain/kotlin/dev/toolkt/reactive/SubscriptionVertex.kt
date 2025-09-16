package dev.toolkt.reactive

import dev.toolkt.reactive.cell.vertices.DependencyEventStreamVertex

class SubscriptionVertex<EventT>(
    private val sourceEventStreamVertex: DependencyEventStreamVertex<EventT>,
    private val handle: (EventT) -> Unit,
) : DependentVertex {
    override fun visit(
        processingContext: Transaction.ProcessingContext,
    ) {
        val receivedEventOccurrence = sourceEventStreamVertex.pullEmittedEvent(
            processingContext = processingContext,
        ) ?: return

        processingContext.enqueueSideEffect(
            sideEffect = object : Transaction.SideEffect {
                override fun execute() {
                    handle(receivedEventOccurrence.event)
                }
            },
        )
    }
}
