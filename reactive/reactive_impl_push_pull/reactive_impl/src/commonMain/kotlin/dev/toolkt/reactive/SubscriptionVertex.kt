package dev.toolkt.reactive

import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex

class SubscriptionVertex<EventT>(
    private val sourceEventStreamVertex: EventStreamVertex<EventT>,
    private val subscriber: EventStream.Subscriber<EventT>,
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

            is dev.toolkt.reactive.event_stream.vertices.EventStreamVertex.EffectiveOccurrence -> {
                context.markDirty(
                    dirtyVertex = this,
                )
            }
        }

        receivedEvent = sourceOccurrence

    }

    override fun commit() {
        // TODO: Support termination
        when (val receivedEvent = this.receivedEvent) {
            is EventStreamVertex.EffectiveOccurrence -> {
                subscriber.handleNotification(
                    notification = EventStream.IntermediateEmissionNotification(
                        emittedEvent = receivedEvent.event,
                    )
                )
            }

            else -> {}
        }
    }

    override fun reset() {
        receivedEvent = null
    }
}
