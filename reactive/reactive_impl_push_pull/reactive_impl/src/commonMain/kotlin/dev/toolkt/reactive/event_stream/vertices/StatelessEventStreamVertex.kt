package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.Transaction

abstract class StatelessEventStreamVertex<ValueT> : PropagativeEventStreamVertex<ValueT>() {
    final override fun stabilize(
        postProcessingContext: Transaction.PostProcessingContext,
        message: EventStreamVertex.Occurrence<ValueT>?,
    ) {
    }
}
