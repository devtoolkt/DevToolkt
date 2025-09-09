package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.PureContext

sealed interface EventStream<out EventT> {
    interface Subscription {
        fun cancel()
    }

    companion object {
        context(pureContext: PureContext) fun <EventT> merge2(
            eventStream1: EventStream<EventT>,
            eventStream2: EventStream<EventT>,
        ): EventStream<EventT> = TODO()

        context(pureContext: PureContext) fun <EventT> merge3(
            eventStream1: EventStream<EventT>,
            eventStream2: EventStream<EventT>,
            eventStream3: EventStream<EventT>,
        ): EventStream<EventT> = TODO()

        context(pureContext: PureContext) fun <EventT> merge4(
            eventStream1: EventStream<EventT>,
            eventStream2: EventStream<EventT>,
            eventStream3: EventStream<EventT>,
            eventStream4: EventStream<EventT>,
        ): EventStream<EventT> = TODO()
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

/**
 * Subscribe to this event stream.
 *
 * This method should be only called from outside the reactive system.
 */
fun <EventT> EventStream<EventT>.subscribe(
    handle: (EventT) -> Unit,
): EventStream.Subscription {
    TODO()
}
