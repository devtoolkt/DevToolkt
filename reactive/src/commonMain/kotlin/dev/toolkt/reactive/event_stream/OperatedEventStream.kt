package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.cell.vertices.DependencyEventStreamVertex

class OperatedEventStream<EventT>(
    override val vertex: DependencyEventStreamVertex<EventT>,
) : BaseOperatedEventStream<EventT>
