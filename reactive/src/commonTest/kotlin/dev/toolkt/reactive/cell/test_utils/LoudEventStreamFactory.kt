package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.map

abstract class LoudEventStreamFactory {
    companion object {
        val values = listOf(
            Basic,
            TransformedBasic,
        )
    }

    data object Basic : LoudEventStreamFactory() {
        override fun <EventT> createExternally(
            doEmit: EventStream<EventT>,
        ): EventStream<EventT> = doEmit
    }

    data object TransformedBasic : LoudEventStreamFactory() {
        override fun <EventT> createExternally(
            doEmit: EventStream<EventT>,
        ): EventStream<EventT> = Basic.createExternally(
            doEmit = doEmit,
        ).map { it }
    }

    abstract fun <EventT> createExternally(
        doEmit: EventStream<EventT>,
    ): EventStream<EventT>

    fun <EventT> createExternally(
        doEmit: EventStream<EventT>,
        doTerminate: EventStream<Unit>,
    ): EventStream<EventT> {
        TODO()
    }
}
