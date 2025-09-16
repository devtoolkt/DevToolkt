package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.Vertex
import kotlin.jvm.JvmInline

// Thought: Merge with `DependentEventStreamVertex`?
interface EventStreamVertex<EventT> : Vertex {
    @JvmInline
    value class EmittedEvent<EventT>(
        val event: EventT,
    ) {
        fun <TransformedEventT> map(
            transform: (EventT) -> TransformedEventT,
        ): EmittedEvent<TransformedEventT> = EmittedEvent(
            event = transform(event),
        )

        fun <TransformedEventT : Any> mapNotNull(
            transform: (EventT) -> TransformedEventT?,
        ): EmittedEvent<TransformedEventT>? {
            val transformedEvent = transform(event) ?: return null

            return EmittedEvent(
                event = transformedEvent,
            )
        }
    }
}
