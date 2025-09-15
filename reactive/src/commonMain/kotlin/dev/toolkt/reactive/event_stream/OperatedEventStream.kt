package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.cell.vertices.DynamicEventStreamVertex

class OperatedEventStream<EventT>(
    override val vertex: DynamicEventStreamVertex<EventT>,
) : BaseOperatedEventStream<EventT>
