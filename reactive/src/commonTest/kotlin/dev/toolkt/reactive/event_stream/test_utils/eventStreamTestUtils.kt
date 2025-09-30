package dev.toolkt.reactive.event_stream.test_utils

import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.subscribe
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

// TODO: Rework like CellVerificationStrategy?
object EventStreamSubscriptionUtils {
    interface Asserter<EventT> {
        fun assertNoOccurrence()

        fun assertOccurredEventEquals(
            expectedOccurredEvent: EventT,
        )

        fun cancel()
    }

    fun <EventT> subscribeForTesting(
        eventStream: EventStream<EventT>,
    ): Asserter<EventT> {
        val receivedOccurredEvents = mutableListOf<EventT>()

        val subscription = assertNotNull(
            eventStream.subscribe { occurredEvent ->
                receivedOccurredEvents.add(occurredEvent)
            },
        )

        return object : Asserter<EventT> {
            override fun assertNoOccurrence() {
                assertEquals(
                    expected = emptyList(),
                    actual = receivedOccurredEvents,
                )
            }

            override fun assertOccurredEventEquals(
                expectedOccurredEvent: EventT,
            ) {
                assertEquals(
                    expected = listOf(expectedOccurredEvent),
                    actual = receivedOccurredEvents,
                )

                receivedOccurredEvents.clear()
            }

            override fun cancel() {
                subscription.cancel()
            }
        }
    }
}
