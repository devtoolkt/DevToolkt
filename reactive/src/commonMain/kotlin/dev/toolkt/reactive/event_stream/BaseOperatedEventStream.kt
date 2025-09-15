package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.cell.vertices.DependencyEventStreamVertex

sealed interface BaseOperatedEventStream<EventT> : EventStream<EventT> {
    val vertex: DependencyEventStreamVertex<EventT>
}
