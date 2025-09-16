package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.IntermediateEventStreamVertex

abstract class StatelessEventStreamVertex<EventT> : IntermediateEventStreamVertex<EventT>() {
    final override fun onFirstDependentAdded() {
        resume()
    }

    final override fun onLastDependentRemoved() {
        pause()
    }

    final override fun transit() {
    }

    protected abstract fun resume()

    protected abstract fun pause()
}
