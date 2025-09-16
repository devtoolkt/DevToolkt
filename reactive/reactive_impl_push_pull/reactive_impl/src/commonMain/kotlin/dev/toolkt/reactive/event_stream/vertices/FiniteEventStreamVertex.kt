package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.IntermediateEventStreamVertex

abstract class FiniteEventStreamVertex<ValueT> : IntermediateEventStreamVertex<ValueT>() {
    final override fun onFirstDependentAdded() {
    }

    final override fun onLastDependentRemoved() {
    }
}
