package dev.toolkt.reactive.event_stream.test_utils

import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.subscribe
import kotlin.test.assertEquals

object EventStreamSubscriptionUtils {
    interface Asserter<EventT> {
        fun assertOccurredEventEquals(
            expectedOccurredEvent: EventT,
        )
    }

    fun <EventT> subscribeForTesting(
        eventStream: EventStream<EventT>,
    ): Asserter<EventT> {

        val receivedOccurredEvents = mutableListOf<EventT>()

        eventStream.subscribe { occurredEvent ->
            receivedOccurredEvents.add(occurredEvent)
        }

        return object : Asserter<EventT> {
            override fun assertOccurredEventEquals(
                expectedOccurredEvent: EventT,
            ) {
                assertEquals(
                    expected = listOf(expectedOccurredEvent),
                    actual = receivedOccurredEvents,
                )

                receivedOccurredEvents.clear()
            }
        }
    }
}
