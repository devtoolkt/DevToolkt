package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.cell.vertices.InherentDependentEventStreamVertex

abstract class FiniteEventStreamVertex<ValueT> : InherentDependentEventStreamVertex<ValueT>() {
    final override fun reset(
        tag: BaseEventStreamVertex.Tag,
    ) {
    }
}
