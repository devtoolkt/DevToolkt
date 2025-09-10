package dev.toolkt.reactive.event_stream

import dev.toolkt.core.utils.iterable.copyAndClear
import dev.toolkt.reactive.test_utils.ReactiveTest
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("ClassName")
class EventStream_single_tests {
    private fun setup(): Pair<EventStream<Int>, ReactiveTest<Int>> =
        ReactiveTest.setup {
            val sourceEventStream = formEventStream()

            sourceEventStream.single()
        }

    @Test
    fun test_eventPropagation_caught() {
        val (singleEventStream, reactiveTest) = setup()

        val collectedEvents = mutableListOf<Int>()

        singleEventStream.subscribeCollecting(
            targetList = collectedEvents,
        )

        // Emit the single event from the source stream
        reactiveTest.stimulate(10)

        // Verify that the single event was received
        assertEquals(
            expected = listOf(10),
            actual = collectedEvents.copyAndClear(),
        )

        // Emit an extraneous event from the source stream
        reactiveTest.stimulate(20)

        // Verify that the extraneous event was not propagated
        assertEquals(
            expected = emptyList(),
            actual = collectedEvents,
        )
    }

    @Test
    fun test_eventPropagation_missed() {
        val (singleEventStream, reactiveTest) = setup()

        val collectedEvents = mutableListOf<Int>()

        // Emit the single event from the source stream
        reactiveTest.stimulate(10)

        // Subscribe after the single event was emitted
        singleEventStream.subscribeCollecting(
            targetList = collectedEvents,
        )

        // Emit an extraneous event from the source stream
        reactiveTest.stimulate(20)

        // Verify that no events were propagated
        assertEquals(
            expected = emptyList(),
            actual = collectedEvents,
        )
    }

    @Test
    fun test_eventPropagation_cancelled() {
        val (singleEventStream, reactiveTest) = setup()

        val collectedEvents = mutableListOf<Int>()

        val subscription = singleEventStream.subscribeCollecting(
            targetList = collectedEvents,
        )

        // Cancel the subscription before any events are emitted
        subscription.cancel()

        // Emit the single event from the source stream
        reactiveTest.stimulate(10)

        // Emit an extraneous event from the source stream
        reactiveTest.stimulate(20)

        // Verify that no events were received
        assertEquals(
            expected = emptyList(),
            actual = collectedEvents,
        )
    }

    @Test
    fun test_eventPropagation_resubscribed() {
        val (singleEventStream, reactiveTest) = setup()

        val collectedEvents = mutableListOf<Int>()

        val subscription = singleEventStream.subscribeCollecting(
            targetList = collectedEvents,
        )

        // Cancel the subscription before any events are emitted
        subscription.cancel()

        // Re-subscribe to the event stream
        singleEventStream.subscribeCollecting(
            targetList = collectedEvents,
        )

        // Emit the single event from the source stream
        reactiveTest.stimulate(10)

        // Verify that the single event was received
        assertEquals(
            expected = listOf(10),
            actual = collectedEvents.copyAndClear(),
        )

        // Emit an extraneous event from the source stream
        reactiveTest.stimulate(20)

        // Verify that the extraneous event was not propagated
        assertEquals(
            expected = emptyList(),
            actual = collectedEvents,
        )
    }
}
