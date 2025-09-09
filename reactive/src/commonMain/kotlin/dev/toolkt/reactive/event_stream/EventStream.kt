package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.PureContext

sealed interface EventStream<out EventT> {
    companion object {
        context(pureContext: PureContext) fun <EventT1, EventT2, ResultT> merge2(
            eventStream1: EventStream<EventT1>,
            eventStream2: EventStream<EventT2>,
        ): EventStream<ResultT> = TODO()

        context(pureContext: PureContext) fun <EventT1, EventT2, EventT3, ResultT> merge3(
            eventStream1: EventStream<EventT1>,
            eventStream2: EventStream<EventT2>,
            eventStream3: EventStream<EventT3>,
        ): EventStream<ResultT> = TODO()

        context(pureContext: PureContext) fun <EventT1, EventT2, EventT3, EventT4, ResultT> merge4(
            eventStream1: EventStream<EventT1>,
            eventStream2: EventStream<EventT2>,
            eventStream3: EventStream<EventT3>,
            eventStream4: EventStream<EventT4>,
        ): EventStream<ResultT> = TODO()
    }
}

context(pureContext: PureContext) fun <EventT, TransformedEventT> EventStream<EventT>.map(
    transform: (EventT) -> TransformedEventT,
): EventStream<TransformedEventT> = TODO()

context(momentContext: MomentContext) fun <EventT> EventStream<EventT>.single(): EventStream<EventT> = TODO()

context(momentContext: MomentContext) fun <EventT> EventStream<EventT>.take(
    count: Int,
): EventStream<EventT> = when {
    count < 0 -> throw IllegalArgumentException("Count must be non-negative")

    count == 0 -> NeverEventStream

    count == 1 -> single()

    else -> TODO()
}
