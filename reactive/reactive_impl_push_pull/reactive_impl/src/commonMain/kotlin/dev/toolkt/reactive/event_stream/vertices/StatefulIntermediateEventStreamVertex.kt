package dev.toolkt.reactive.event_stream.vertices

abstract class StatefulIntermediateEventStreamVertex<ValueT> : BaseIntermediateEventStreamVertex<ValueT>() {
    final override fun onFirstDependentAdded() {
    }

    final override fun onLastDependentRemoved() {
    }
}
