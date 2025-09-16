package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.IntermediateEventStreamVertex

abstract class StatefulIntermediateEventStreamVertex<ValueT> : IntermediateEventStreamVertex<ValueT>() {
    final override fun onFirstDependentAdded() {
    }

    final override fun onLastDependentRemoved() {
    }
}
