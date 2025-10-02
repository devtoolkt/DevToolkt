package dev.toolkt.reactive.cell.test_utils

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
        override fun <EventT> createExternally(): EventStream<EventT> = NeverEventStream
    }

    data object TransformedNever : QuietEventStreamFactory {
        override fun <EventT> createExternally(): EventStream<EventT> = NeverEventStream.map {
            // This code path should never be entered
            it
        }
    }

    data object PotentiallyVocal : QuietEventStreamFactory {
        override fun <EventT> createExternally(): EventStream<EventT> = EmitterEventStream()
    }

    data object TransformedPotentiallyVocal : QuietEventStreamFactory {
        override fun <EventT> createExternally(): EventStream<EventT> =
            PotentiallyVocal.createExternally<EventT>().map {
                // This code path should never be entered
                it
            }
    }

    fun <EventT> createExternally(): EventStream<EventT>
}
