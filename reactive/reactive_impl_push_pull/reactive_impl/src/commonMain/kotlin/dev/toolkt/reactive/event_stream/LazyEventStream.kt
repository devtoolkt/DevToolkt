package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex

class LazyEventStream<out EventT>(
    private val eventStreamLazy: Lazy<EventStream<EventT>>,
) : EventStream<EventT> {
    override val vertex: EventStreamVertex<EventT> by lazy {
        eventStreamLazy.value.vertex
    }
}
