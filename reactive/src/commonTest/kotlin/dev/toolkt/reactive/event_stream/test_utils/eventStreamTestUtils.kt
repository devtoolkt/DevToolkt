package dev.toolkt.reactive.event_stream.test_utils

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.test_utils.Tick
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.mapNotNull
import dev.toolkt.reactive.event_stream.subscribe
import dev.toolkt.reactive.event_stream.take
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

interface EventStreamDynamicTestContext {
    val onTick: EventStream<Tick>
}

context(context: EventStreamDynamicTestContext) fun <EventT : Any> createEnergicEventStreamExternally(
    emittedEventByTick: Map<Tick, EventT>,
    terminationTick: Tick?,
): EventStream<EventT> {
    val onTickCropped = when (terminationTick) {
        null -> context.onTick

        else -> MomentContext.execute {
            context.onTick.take(terminationTick.t)
        }
    }

    return onTickCropped.mapNotNull { tick ->
        emittedEventByTick[tick]
    }
}

fun <EventT : Any> testEventStream_initiallyEnergic(
    setup: context(EventStreamDynamicTestContext) () -> EventStream<EventT>,
    expectedNotificationByTick: Map<Tick, EventStream.Notification<EventT>>,
) {
    val doTick = EmitterEventStream<Tick>()

    val subjectEventStream = with(
        object : EventStreamDynamicTestContext {
            override val onTick: EventStream<Tick> = doTick
        },
    ) {
        setup()
    }

    val receivedNotifications = mutableListOf<EventStream.Notification<EventT>>()

    // TODO: Use different verification strategies
    // TODO: Handle freezing

    assertNotNull(
        actual = subjectEventStream.subscribe(
            object : EventStream.Subscriber<EventT> {
                override fun handleNotification(
                    notification: EventStream.Notification<EventT>,
                ) {
                    receivedNotifications.add(notification)
                }
            }
        ),
        message = "Expected a non-null subscription for an energic event stream",
    )

    val lastTick = expectedNotificationByTick.keys.maxByOrNull { it.t } ?: return

    (1..lastTick.t).forEach { t ->
        val tick = Tick(t = t)

        receivedNotifications.clear()

        doTick.emit(tick)

        val expectedNotification = expectedNotificationByTick[tick]

        when {
            expectedNotification != null -> {
                assertEquals(
                    expected = 1,
                    actual = receivedNotifications.size,
                    message = "At t=${tick.t}, as single update expected, but received: $receivedNotifications",
                )
            }

            else -> {
                assertEquals(
                    expected = 0,
                    actual = receivedNotifications.size,
                    message = "At t=${tick.t}, no updates expected, but received: $receivedNotifications",
                )
            }
        }

        val receivedNotification = receivedNotifications.single()

        assertEquals(
            expected = expectedNotification,
            actual = receivedNotification,
            message = "At t=${tick.t}, expected $expectedNotification, but received: $receivedNotification",
        )
    }
}

interface EventStreamInertTestContext

fun <EventT : Any> testEventStream_immediatelyExhausted(
    setup: context(EventStreamInertTestContext) () -> EventStream<EventT>,
    expectedValue: EventT,
) {
    val subjectEventStream = with(
        object : EventStreamInertTestContext {},
    ) {
        setup()
    }

    assertNull(
        actual = subjectEventStream.subscribe {},
        message = "Expected a null subscription for an exhausted event stream",
    )
}
