package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex

class DerivedEventStream<EventT>(
    override val vertex: EventStreamVertex<EventT>,
) : OperatedEventStream<EventT>
