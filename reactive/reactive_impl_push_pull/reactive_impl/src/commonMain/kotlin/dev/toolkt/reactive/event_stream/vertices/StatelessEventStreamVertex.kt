package dev.toolkt.reactive.event_stream.vertices

abstract class StatelessEventStreamVertex<EventT> : BaseIntermediateEventStreamVertex<EventT>() {
    final override fun onFirstDependentAdded() {
        resume()
    }

    final override fun onLastDependentRemoved() {
        pause()
    }

    final override fun update(
        currentNotification: EventStreamVertex.Occurrence<EventT>,
    ) {
    }

    protected abstract fun resume()

    protected abstract fun pause()
}
