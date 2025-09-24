package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.event_stream.vertices.SilentEventStreamVertex

data object NeverEventStream : EventStream<Nothing> {
    override val vertex = SilentEventStreamVertex
}
