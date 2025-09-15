package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.Vertex
import kotlin.jvm.JvmInline

// Thought: Merge with `DynamicEventStreamVertex`?
interface EventStreamVertex<EventT> : Vertex {
    @JvmInline
    value class Occurrence<EventT>(
        val event: EventT,
    ) {
        fun <TransformedEventT> map(
            transform: (EventT) -> TransformedEventT,
        ): Occurrence<TransformedEventT> = Occurrence(
            event = transform(event),
        )

        fun <TransformedEventT : Any> mapNotNull(
            transform: (EventT) -> TransformedEventT?,
        ): Occurrence<TransformedEventT>? {
            val transformedEvent = transform(event) ?: return null

            return Occurrence(
                event = transformedEvent,
            )
        }
    }
}
