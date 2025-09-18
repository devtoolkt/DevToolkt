package dev.toolkt.reactive

import dev.toolkt.reactive.cell.vertices.DependencyEventStreamVertex
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex

class SubscriptionVertex<EventT>(
    private val sourceEventStreamVertex: DependencyEventStreamVertex<EventT>,
    private val handle: (EventT) -> Unit,
) : DependentVertex {
    private var receivedEvent: EventStreamVertex.Occurrence<EventT>? = null

    override fun visit(
        context: Transaction.ProcessingContext,
    ) {
        val sourceOccurrence = sourceEventStreamVertex.pullOccurrenceSubsequent(
            context = context,
        )

        when (sourceOccurrence) {
            EventStreamVertex.NilOccurrence -> {}

            is EventStreamVertex.EffectiveOccurrence -> {
                context.markDirty(
                    dirtyVertex = this,
                )
            }
        }

        receivedEvent = sourceOccurrence

    }

    override fun commit() {
        when (val receivedEvent = this.receivedEvent) {
            is EventStreamVertex.EffectiveOccurrence -> {

                handle(receivedEvent.event)
            }

            else -> {}
        }
    }

    override fun reset() {
        receivedEvent = null
    }
}
