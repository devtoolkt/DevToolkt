package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.NeverEventStream
import dev.toolkt.reactive.event_stream.map

sealed interface QuietEventStreamFactory {
    companion object {
        val values = listOf(
            Never,
            TransformedNever,
            PotentiallyVocal,
            TransformedPotentiallyVocal,
        )
    }

    data object Never : QuietEventStreamFactory {
        context(momentContext: MomentContext) override fun <EventT> create(): EventStream<EventT> = NeverEventStream
    }

    data object TransformedNever : QuietEventStreamFactory {
        context(momentContext: MomentContext) override fun <EventT> create(): EventStream<EventT> =
            NeverEventStream.map {
                // This code path should never be entered
                it
            }
    }

    data object PotentiallyVocal : QuietEventStreamFactory {
        context(momentContext: MomentContext) override fun <EventT> create(): EventStream<EventT> = EmitterEventStream()
    }

    data object TransformedPotentiallyVocal : QuietEventStreamFactory {
        context(momentContext: MomentContext) override fun <EventT> create(): EventStream<EventT> =
            PotentiallyVocal.create<EventT>().map {
                // This code path should never be entered
                it
            }
    }

    context(momentContext: MomentContext) fun <EventT> create(): EventStream<EventT>
}
