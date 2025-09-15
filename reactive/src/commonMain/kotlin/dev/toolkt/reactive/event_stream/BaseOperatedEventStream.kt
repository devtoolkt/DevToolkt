package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.cell.vertices.DynamicEventStreamVertex

sealed interface BaseOperatedEventStream<EventT> : EventStream<EventT> {
    val vertex: DynamicEventStreamVertex<EventT>
}
