package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex.Occurrence

abstract class InherentEventStreamVertex<EventT>() : BaseEventStreamVertex<EventT>() {
    final override fun processSubscribed(
        context: Transaction.ProcessingContext,
        wasFirst: Boolean,
    ): Occurrence<EventT> = process(
        context = context,
    )

    final override fun processSubsequent(
        context: Transaction.ProcessingContext,
    ): Occurrence<EventT> = process(
        context = context,
    )

    final override fun onFirstSubscriberAdded() {
    }

    final override fun onLastSubscriberRemoved() {
    }

    protected abstract fun process(
        context: Transaction.ProcessingContext,
    ): Occurrence<EventT>
}
