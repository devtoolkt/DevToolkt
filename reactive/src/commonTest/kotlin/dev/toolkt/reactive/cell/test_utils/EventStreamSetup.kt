package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.NeverEventStream
import dev.toolkt.reactive.event_stream.map
import dev.toolkt.reactive.event_stream.subscribeCollecting
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

interface EventStreamSetup<out EventT> {
    interface EventStreamProvider<out EventT> {
        companion object {
            fun <EventT> pure(
                eventStream: EventStream<EventT>,
            ): EventStreamProvider<EventT> = object : EventStreamProvider<EventT> {
                override fun provide(): EventStream<EventT> = eventStream
            }
        }

        fun provide(): EventStream<EventT>
    }

    data object NeverEventStreamProvider : EventStreamProvider<Nothing> {
        override fun provide(): EventStream<Nothing> = NeverEventStream
    }

    class ReplacingEventStreamSetup<out EventT> private constructor(
        private val replacementEvent: EventT,
    ) : EventStreamSetup<EventT> {
        companion object {
            fun <EventT : Any> configure(
                replacementEvent: EventT,
            ): ReplacingEventStreamSetup<EventT> = ReplacingEventStreamSetup(
                replacementEvent = replacementEvent,
            )
        }

        context(momentContext: MomentContext) override fun setup(
            preparationTickStream: EventStream<Unit>,
            propagationTickStream: EventStream<Unit>,
        ): EventStreamProvider<EventT> = object : EventStreamProvider<EventT> {
            override fun provide(): EventStream<EventT> = propagationTickStream.map { replacementEvent }
        }
    }

    class MapToStringEventStreamSetup<out EventT> private constructor(
        private val sourceSetup: EventStreamSetup<EventT>,
    ) : EventStreamSetup<String> {
        companion object {
            fun <EventT : Any> configure(
                sourceSetup: EventStreamSetup<EventT>,
            ): MapToStringEventStreamSetup<EventT> = MapToStringEventStreamSetup(
                sourceSetup = sourceSetup,
            )
        }

        context(momentContext: MomentContext) override fun setup(
            preparationTickStream: EventStream<Unit>,
            propagationTickStream: EventStream<Unit>,
        ): EventStreamProvider<String> {
            val sourceEventStreamProvider = sourceSetup.setup(
                preparationTickStream,
                propagationTickStream,
            )

            return object : EventStreamProvider<String> {
                override fun provide(): EventStream<String> = sourceEventStreamProvider.provide().map { it.toString() }
            }
        }
    }

    class Merge2EventStreamSetup<EventT> private constructor(
        private val source1Setup: EventStreamSetup<EventT>,
        private val source2Setup: EventStreamSetup<EventT>,
    ) : EventStreamSetup<EventT> {
        companion object {
            fun <EventT> configure(
                source1Setup: EventStreamSetup<EventT>,
                source2Setup: EventStreamSetup<EventT>,
            ): Merge2EventStreamSetup<EventT> = Merge2EventStreamSetup(
                source1Setup = source1Setup,
                source2Setup = source2Setup,
            )
        }

        context(momentContext: MomentContext) override fun setup(
            preparationTickStream: EventStream<Unit>,
            propagationTickStream: EventStream<Unit>,
        ): EventStreamProvider<EventT> {
            val sourceEventStream1Provider = source1Setup.setup(
                preparationTickStream = preparationTickStream,
                propagationTickStream = propagationTickStream,
            )

            val sourceEventStream2Provider = source2Setup.setup(
                preparationTickStream = preparationTickStream,
                propagationTickStream = propagationTickStream,
            )

            return object : EventStreamProvider<EventT> {
                override fun provide(): EventStream<EventT> = EventStream.merge2(
                    sourceEventStream1Provider.provide(),
                    sourceEventStream2Provider.provide(),
                )
            }
        }
    }

    context(momentContext: MomentContext) fun setup(
        preparationTickStream: EventStream<Unit>,
        propagationTickStream: EventStream<Unit>,
    ): EventStreamProvider<EventT>
}

fun <EventT : Any> EventStreamSetup<EventT>.testOccurrencePropagation(
    expectedOccurredEvent: EventT?,
) {
    val preparationTickStream = EmitterEventStream<Unit>()

    val propagationTickStream = EmitterEventStream<Unit>()

    val subjectEventStream = MomentContext.execute {
        setup(
            preparationTickStream = preparationTickStream,
            propagationTickStream = propagationTickStream,
        )
    }.provide()

    preparationTickStream.emit(Unit)

    val collectedOccurredEvents = mutableListOf<EventT?>()

    // FIXME: Shouldn't we subscribe before preparation?
    subjectEventStream.subscribeCollecting(
        targetList = collectedOccurredEvents,
    )

    propagationTickStream.emit(Unit)

    val expectedOccurredEvents: List<EventT?> = listOfNotNull(expectedOccurredEvent)

    assertEquals(
        expected = expectedOccurredEvents,
        actual = collectedOccurredEvents,
    )
}

fun <EventT> EventStreamSetup<EventT>.testOccurrencePropagationDeactivated() {
    val preparationTickStream = EmitterEventStream<Unit>()

    val propagationTickStream = EmitterEventStream<Unit>()

    val subjectEventStream = MomentContext.execute {
        setup(
            preparationTickStream = preparationTickStream,
            propagationTickStream = propagationTickStream,
        )
    }.provide()

    preparationTickStream.emit(Unit)

    val collectedEvents = mutableListOf<EventT>()

    val subscription = assertNotNull(
        subjectEventStream.subscribeCollecting(
            targetList = collectedEvents,
        ),
    )

    subscription.cancel()

    propagationTickStream.emit(Unit)

    assertEquals(
        expected = emptyList(),
        actual = collectedEvents,
    )
}
