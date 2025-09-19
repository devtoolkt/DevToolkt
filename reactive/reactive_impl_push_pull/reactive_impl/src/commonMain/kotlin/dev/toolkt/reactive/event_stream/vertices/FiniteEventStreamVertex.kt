package dev.toolkt.reactive.event_stream.vertices

abstract class FiniteEventStreamVertex<ValueT> : InherentDependentEventStreamVertex<ValueT>() {
    final override fun reset(
        tag: BaseEventStreamVertex.Tag,
    ) {
    }
}
