package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex

sealed interface OperatedEventStream<EventT> : EventStream<EventT> {
    val vertex: EventStreamVertex<EventT>
}
