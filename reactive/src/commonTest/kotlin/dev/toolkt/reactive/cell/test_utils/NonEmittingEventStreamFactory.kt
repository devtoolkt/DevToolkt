package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.NeverEventStream
import dev.toolkt.reactive.event_stream.map

sealed interface NonEmittingEventStreamFactory {
    companion object {
        val values = listOf(
            Never,
            Dynamic,
            TransformedDynamic,
        )
    }

    data object Never : NonEmittingEventStreamFactory {
        context(momentContext: MomentContext) override fun <EventT> create(
        ): EventStream<EventT> = NeverEventStream
    }

    data object Dynamic : NonEmittingEventStreamFactory {
        context(momentContext: MomentContext) override fun <EventT> create(
        ): EventStream<EventT> = EmitterEventStream()
    }

    data object TransformedDynamic : NonEmittingEventStreamFactory {
        context(momentContext: MomentContext) override fun <EventT> create(
        ): EventStream<EventT> = EmitterEventStream<EventT>().map {
            // This code path should never be entered
            throw UnsupportedOperationException()
        }
    }

    context(momentContext: MomentContext) fun <EventT> create(): EventStream<EventT>
}
