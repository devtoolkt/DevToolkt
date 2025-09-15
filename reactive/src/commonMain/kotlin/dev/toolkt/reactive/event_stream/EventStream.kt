package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.PureContext
import dev.toolkt.reactive.SubscriptionVertex
import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.DerivedCell
import dev.toolkt.reactive.cell.vertices.HoldCellVertex
import dev.toolkt.reactive.event_stream.vertices.EventStreamFilterVertex
import dev.toolkt.reactive.event_stream.vertices.EventStreamMapNotNullVertex
import dev.toolkt.reactive.event_stream.vertices.EventStreamMapVertex
import dev.toolkt.reactive.event_stream.vertices.EventStreamMerge2Vertex
import dev.toolkt.reactive.event_stream.vertices.EventStreamSingleVertex

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

        context(pureContext: PureContext) fun <EventT> merge2(
            eventStream1: EventStream<EventT>,
            eventStream2: EventStream<EventT>,
        ): EventStream<EventT> {
            val operatedEventStream1 = eventStream1 as? OperatedEventStream ?: return eventStream2
            val operatedEventStream2 = eventStream2 as? OperatedEventStream ?: return NeverEventStream

            return DerivedEventStream(
                vertex = EventStreamMerge2Vertex(
                    sourceEventStream1Vertex = operatedEventStream1.vertex,
                    sourceEventStream2Vertex = operatedEventStream2.vertex,
                ),
            )
        }

        context(pureContext: PureContext) fun <EventT> merge3(
            eventStream1: EventStream<EventT>,
            eventStream2: EventStream<EventT>,
            eventStream3: EventStream<EventT>,
        ): EventStream<EventT> = TODO()
    }
}

context(pureContext: PureContext) fun <EventT, TransformedEventT> EventStream<EventT>.map(
    transform: (EventT) -> TransformedEventT,
): EventStream<TransformedEventT> = when (this) {
    NeverEventStream -> NeverEventStream

    is OperatedEventStream -> DerivedEventStream(
        vertex = EventStreamMapVertex(
            sourceEventStreamVertex = this.vertex,
            transform = { _, event ->
                transform(event)
            },
        )
    )
}

context(pureContext: PureContext) fun <EventT, TransformedEventT : Any> EventStream<EventT>.mapNotNull(
    transform: (EventT) -> TransformedEventT?,
): EventStream<TransformedEventT> = when (this) {
    NeverEventStream -> NeverEventStream

    is OperatedEventStream -> DerivedEventStream(
        vertex = EventStreamMapNotNullVertex(
            sourceEventStreamVertex = this.vertex,
            transform = transform,
        )
    )
}

context(pureContext: PureContext) fun <EventT, TransformedEventT> EventStream<EventT>.mapAt(
    transform: context(MomentContext) (EventT) -> TransformedEventT,
): EventStream<TransformedEventT> = when (this) {
    NeverEventStream -> NeverEventStream

    is OperatedEventStream -> DerivedEventStream(
        vertex = EventStreamMapVertex(
            sourceEventStreamVertex = this.vertex,
            transform = { preProcessingContext, event ->
                MomentContext(
                    preProcessingContext = preProcessingContext,
                ).run {
                    transform(event)
                }
            },
        )
    )
}

fun <EventT> EventStream<EventT>.filter(
    predicate: (EventT) -> Boolean,
): EventStream<EventT> = when (this) {
    NeverEventStream -> NeverEventStream

    is OperatedEventStream -> DerivedEventStream(
        vertex = EventStreamFilterVertex(
            sourceEventStreamVertex = this.vertex,
            predicate = predicate,
        )
    )
}

context(momentContext: MomentContext) fun <EventT> EventStream<EventT>.single(): EventStream<EventT> = when (this) {
    NeverEventStream -> NeverEventStream

    is OperatedEventStream -> DerivedEventStream(
        vertex = EventStreamSingleVertex.construct(
            preProcessingContext = momentContext.preProcessingContext,
            sourceEventStreamVertex = this.vertex,
        ),
    )
}

context(momentContext: MomentContext) fun <EventT> EventStream<EventT>.take(
    count: Int,
): EventStream<EventT> = when {
    count < 0 -> throw IllegalArgumentException("Count must be non-negative")

    count == 0 -> NeverEventStream

    count == 1 -> single()

    else -> TODO()
}

context(momentContext: MomentContext) fun <ValueT> EventStream<ValueT>.hold(
    initialValue: ValueT,
): Cell<ValueT> = when (this) {
    NeverEventStream -> Cell.of(value = initialValue)

    is OperatedEventStream -> DerivedCell(
        HoldCellVertex.construct(
            preProcessingContext = momentContext.preProcessingContext,
            sourceEventStreamVertex = this.vertex,
            initialValue = initialValue,
        ),
    )
}

/**
 * Subscribe to this event stream.
 *
 * This method must be called from outside the reactive system.
 */
fun <EventT> EventStream<EventT>.subscribe(
    handle: (EventT) -> Unit,
): EventStream.Subscription? = when (this) {
    NeverEventStream -> null

    is OperatedEventStream -> {
        val subscriptionVertex = SubscriptionVertex(
            sourceEventStreamVertex = this.vertex,
            handle = handle,
        )

        this.vertex.addDependent(
            expansionContext = Transaction.ExpansionContext.External,
            vertex = subscriptionVertex,
        )

        object : EventStream.Subscription {
            override fun cancel() {
                this@subscribe.vertex.removeDependent(
                    shrinkageContext = Transaction.ShrinkageContext.External,
                    vertex = subscriptionVertex,
                )
            }
        }
    }
}
