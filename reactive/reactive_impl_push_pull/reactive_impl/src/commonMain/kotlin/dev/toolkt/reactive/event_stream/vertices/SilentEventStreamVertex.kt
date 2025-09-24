package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.DependentVertex
import dev.toolkt.reactive.Transaction

data object SilentEventStreamVertex : EventStreamVertex<Nothing> {
    override fun pullOccurrenceSubscribing(
        context: Transaction.ProcessingContext,
        dependentVertex: DependentVertex,
    ): EventStreamVertex.NilOccurrence = EventStreamVertex.NilOccurrence

    override fun pullOccurrenceSubsequent(
        context: Transaction.ProcessingContext,
    ): EventStreamVertex.Occurrence<Nothing> = EventStreamVertex.NilOccurrence

    override fun subscribe(
        dependentVertex: DependentVertex,
    ) {
    }

    override fun unsubscribe(
        dependentVertex: DependentVertex,
    ) {
    }

    override fun commit() {
    }

    override fun reset() {
    }
}
