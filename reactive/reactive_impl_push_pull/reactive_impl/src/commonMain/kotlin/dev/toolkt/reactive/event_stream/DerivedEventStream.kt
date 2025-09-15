package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.cell.vertices.DependencyEventStreamVertex

class DerivedEventStream<EventT>(
    override val vertex: DependencyEventStreamVertex<EventT>,
) : OperatedEventStream<EventT>
