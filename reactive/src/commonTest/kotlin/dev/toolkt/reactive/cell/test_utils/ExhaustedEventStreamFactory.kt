package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.NeverEventStream
import dev.toolkt.reactive.event_stream.map

sealed interface ExhaustedEventStreamFactory {
    companion object {
        val values = listOf(
            Never,
            TransformedNever,
        )
    }

    data object Never : ExhaustedEventStreamFactory {
        override fun <EventT> createExternally(): EventStream<EventT> = NeverEventStream
    }

    data object TransformedNever : ExhaustedEventStreamFactory {
        override fun <EventT> createExternally(): EventStream<EventT> = NeverEventStream.map {
            // This code path should never be entered
            it
        }
    }

    fun <EventT> createExternally(): EventStream<EventT>
}
