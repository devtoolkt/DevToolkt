package dev.toolkt.reactive.event_stream

import dev.toolkt.core.utils.lazy.LazyUtils
import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.SubscriptionVertex
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.OperatedCell
import dev.toolkt.reactive.cell.sample
import dev.toolkt.reactive.cell.vertices.HoldCellVertex
import dev.toolkt.reactive.cell.vertices.PureCellVertex
import dev.toolkt.reactive.event_stream.vertices.DynamicEventStreamVertex
import dev.toolkt.reactive.event_stream.vertices.EventStreamFilterVertex
import dev.toolkt.reactive.event_stream.vertices.EventStreamMapNotNullVertex
import dev.toolkt.reactive.event_stream.vertices.EventStreamMapVertex
import dev.toolkt.reactive.event_stream.vertices.EventStreamMerge2Vertex
import dev.toolkt.reactive.event_stream.vertices.EventStreamMerge3Vertex
import dev.toolkt.reactive.event_stream.vertices.EventStreamSingleVertex
import dev.toolkt.reactive.event_stream.vertices.EventStreamTakeVertex
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex
import dev.toolkt.reactive.event_stream.vertices.SilentEventStreamVertex

sealed interface EventStream<out EventT> {
    /**
     * A mechanism for propagating an event into the reactive system.
     */
    interface EventPropagator<EventT> {
        /**
         * Propagate an event within the reactive system.
         *
         * This method must be called from outside the reactive system.
         */
        fun propagate(
            event: EventT,
        )
    }

    /**
     * A controller for registering and unregistering an external listener to an external source. The technical details
     * of what a "listener" exactly is are up to the external system.
     */
    interface ExternalListenerController {
        /**
         * Register the external listener. The listener registration shouldn't cause the external system to change its
         * behavior in an observable way.
         *
         * This method will be called from within the reactive system.
         */
        fun register()

        /**
         * Unregister the external listener. The listener unregistration shouldn't cause the external system to change
         * its behavior in an observable way.
         *
         * This method will be called from within the reactive system.
         */
        fun unregister()
    }

    /**
     * A subscription to an event stream.
     */
    interface Subscription {
        /**
         * Cancel the subscription.
         *
         * This method must be called from outside the reactive system.
         */
        fun cancel()
    }

    companion object {
        fun <EventT, ResultT> looped(
            block: (EventStream<EventT>) -> Pair<ResultT, EventStream<EventT>>,
        ): ResultT = LazyUtils.looped { loopedEventStreamLazy ->
            block(
                LazyEventStream(
                    eventStreamLazy = loopedEventStreamLazy,
                ),
            )
        }

        /**
         * Creates an event stream driven by an external event source.
         *
         * @param setup - A function that, given an [EventPropagator] bound to this stream, returns an
         * [ExternalListenerController] bound to the external event source. Should not directly mutate the external
         * system in an observable way.
         */
        fun <EventT> external(
            setup: (EventPropagator<EventT>) -> ExternalListenerController,
        ): EventStream<EventT> = TODO()

        fun <EventT> merge2(
            eventStream1: EventStream<EventT>,
            eventStream2: EventStream<EventT>,
        ): EventStream<EventT> = OperatedEventStream(
            EventStreamMerge2Vertex.construct0(
                uncheckedSourceEventStream1Vertex = eventStream1.vertex,
                uncheckedSourceEventStream2Vertex = eventStream2.vertex,
            )
        )

        fun <EventT> merge3(
            eventStream1: EventStream<EventT>,
            eventStream2: EventStream<EventT>,
            eventStream3: EventStream<EventT>,
        ): EventStream<EventT> = OperatedEventStream(
            EventStreamMerge3Vertex.construct0(
                uncheckedSourceEventStream1Vertex = eventStream1.vertex,
                uncheckedSourceEventStream2Vertex = eventStream2.vertex,
                uncheckedSourceEventStream3Vertex = eventStream3.vertex,
            )
        )
    }

    val vertex: EventStreamVertex<EventT>
}

fun <EventT, TransformedEventT> EventStream<EventT>.map(
    transform: (EventT) -> TransformedEventT,
): EventStream<TransformedEventT> = OperatedEventStream(
    vertex = when (val vertex = this.vertex) {
        SilentEventStreamVertex -> SilentEventStreamVertex

        is DynamicEventStreamVertex -> EventStreamMapVertex(
            sourceEventStreamVertex = vertex,
            transform = { _, event ->
                transform(event)
            },
        )
    }
)

fun <EventT, TransformedEventT : Any> EventStream<EventT>.mapNotNull(
    transform: (EventT) -> TransformedEventT?,
): EventStream<TransformedEventT> = OperatedEventStream(
    vertex = when (val vertex = this.vertex) {
        SilentEventStreamVertex -> SilentEventStreamVertex

        is DynamicEventStreamVertex -> EventStreamMapNotNullVertex(
            sourceEventStreamVertex = vertex,
            transform = { _, event ->
                transform(event)
            },
        )
    }
)

fun <EventT, TransformedEventT> EventStream<EventT>.mapAt(
    transform: context(MomentContext) (EventT) -> TransformedEventT,
): EventStream<TransformedEventT> = OperatedEventStream(
    vertex = when (val vertex = this.vertex) {
        SilentEventStreamVertex -> SilentEventStreamVertex

        is DynamicEventStreamVertex -> EventStreamMapVertex(
            sourceEventStreamVertex = vertex,
            transform = { context, event ->
                MomentContext(
                    context = context,
                ).run {
                    transform(event)
                }
            },
        )
    },
)

fun <EventT, TransformedEventT : Any> EventStream<EventT>.mapNotNullAt(
    transform: context(MomentContext) (EventT) -> TransformedEventT?,
): EventStream<TransformedEventT> =  OperatedEventStream(
    vertex = when (val vertex = this.vertex) {
        SilentEventStreamVertex -> SilentEventStreamVertex

        is DynamicEventStreamVertex -> EventStreamMapNotNullVertex(
            sourceEventStreamVertex = vertex,
            transform = { context, event ->
                MomentContext(
                    context = context,
                ).run {
                    transform(event)
                }
            },
        )
    }
)

fun <EventT> EventStream<EventT>.filter(
    predicate: (EventT) -> Boolean,
): EventStream<EventT> = OperatedEventStream(
    vertex = when (val vertex = this.vertex) {
        SilentEventStreamVertex -> SilentEventStreamVertex

        is DynamicEventStreamVertex -> EventStreamFilterVertex(
            sourceEventStreamVertex = vertex,
            predicate = predicate,
        )
    },
)

fun <EventT> EventStream<EventT>.filterAt(
    predicate: context(MomentContext) (EventT) -> Boolean,
): EventStream<EventT> = TODO()

context(momentContext: MomentContext) fun <EventT> EventStream<EventT>.single(): EventStream<EventT> =
    OperatedEventStream(
        vertex = when (val vertex = this.vertex) {
            SilentEventStreamVertex -> SilentEventStreamVertex

            is DynamicEventStreamVertex -> EventStreamSingleVertex.construct(
                context = momentContext.context,
                sourceEventStreamVertex = vertex,
            )
        }
    )


context(momentContext: MomentContext) fun <EventT> EventStream<EventT>.take(
    count: Int,
): EventStream<EventT> = when {
    count < 0 -> throw IllegalArgumentException("Count must be non-negative")

    count == 0 -> NeverEventStream

    count == 1 -> single()

    else -> OperatedEventStream(
        vertex = when (val vertex = this.vertex) {
            SilentEventStreamVertex -> SilentEventStreamVertex

            is DynamicEventStreamVertex -> EventStreamTakeVertex.construct(
                context = momentContext.context,
                sourceEventStreamVertex = vertex,
                totalCount = count,
            )
        }
    )
}


context(momentContext: MomentContext) fun <ValueT> EventStream<ValueT>.hold(
    initialValue: ValueT,
): Cell<ValueT> = OperatedCell(
    vertex = when (val vertex = this.vertex) {
        SilentEventStreamVertex -> PureCellVertex(
            value = initialValue,
        )

        is DynamicEventStreamVertex -> HoldCellVertex.construct(
            context = momentContext.context,
            sourceEventStreamVertex = vertex,
            initialValue = initialValue,
        )
    }
)

context(momentContext: MomentContext) fun <EventT, AccT> EventStream<EventT>.accumulate(
    initialAccValue: AccT,
    transform: (accValue: AccT, newEvent: EventT) -> AccT,
): Cell<AccT> = EventStream.looped<AccT, Cell<AccT>> { loopedNewAccValues ->
    val accCell = Cell.define(
        initialValue = initialAccValue,
        newValues = loopedNewAccValues,
    )

    val newAccValues = this@accumulate.mapAt { newEvent ->
        transform(
            accCell.sample(),
            newEvent,
        )
    }

    Pair(
        accCell,
        newAccValues,
    )
}

/**
 * Subscribe to this event stream.
 *
 * This method must be called from outside the reactive system.
 */
fun <EventT> EventStream<EventT>.subscribe(
    handle: (EventT) -> Unit,
): EventStream.Subscription? = when (val vertex = this.vertex) {
    SilentEventStreamVertex -> null

    is DynamicEventStreamVertex -> {
        val subscriptionVertex = SubscriptionVertex(
            sourceEventStreamVertex = this.vertex,
            handle = handle,
        )

        vertex.subscribe(
            dependentVertex = subscriptionVertex,
        )

        object : EventStream.Subscription {
            override fun cancel() {
                this@subscribe.vertex.unsubscribe(
                    dependentVertex = subscriptionVertex,
                )
            }
        }
    }
}
