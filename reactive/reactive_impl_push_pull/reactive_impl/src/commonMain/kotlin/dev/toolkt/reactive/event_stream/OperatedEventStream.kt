package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex

internal class OperatedEventStream<EventT>(
    override val vertex: EventStreamVertex<EventT>,
) : EventStream<EventT>
