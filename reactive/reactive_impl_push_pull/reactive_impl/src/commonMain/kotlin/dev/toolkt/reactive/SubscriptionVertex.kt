package dev.toolkt.reactive

import dev.toolkt.reactive.cell.vertices.DependencyEventStreamVertex

class SubscriptionVertex<EventT>(
    private val sourceEventStreamVertex: DependencyEventStreamVertex<EventT>,
    private val handle: (EventT) -> Unit,
) : DependentVertex {
    override fun visit(
        context: Transaction.Context,
    ) {
        val receivedEventOccurrence = sourceEventStreamVertex.pullEmittedEvent(
            context = context,
        ) ?: return

        context.enqueueSideEffect(
            sideEffect = object : Transaction.SideEffect {
                override fun execute() {
                    handle(receivedEventOccurrence.event)
                }
            },
        )
    }
}
