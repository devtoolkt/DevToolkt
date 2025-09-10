package dev.toolkt.reactive.event_stream

import dev.toolkt.core.utils.iterable.copyAndClear
import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.test_utils.ReactiveTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertIs

@Suppress("ClassName")
class EventStream_take_tests {
    private fun setup(
        count: Int,
    ): Pair<EventStream<Int>, ReactiveTest<Int>> = ReactiveTest.setup {
        val sourceEventStream = formEventStream()

        sourceEventStream.take(count = count)
    }

    @Test
    fun test_negative() {
        assertIs<IllegalArgumentException>(
            assertFails {
                MomentContext.executeExternally {
                    NeverEventStream.take(count = -1)
                }
            },
        )
    }

    @Test
    fun test_count0() {
        val (takeEventStream, reactiveTest) = setup(
            count = 0,
        )

        val collectedEvents = mutableListOf<Int>()

        takeEventStream.subscribeCollecting(
            targetList = collectedEvents,
        )

        // Emit an event from the source stream
        reactiveTest.stimulate(10)

        // Emit another event from the source stream
        reactiveTest.stimulate(20)

        // Verify that no events were received
        assertEquals(
            expected = emptyList(),
            actual = collectedEvents,
        )
    }

    @Test
    fun test_count1_caught() {
        val (takeEventStream, reactiveTest) = setup(
            count = 1,
        )

        val collectedEvents = mutableListOf<Int>()

        takeEventStream.subscribeCollecting(
            targetList = collectedEvents,
        )

        // Emit a single event from the source stream
        reactiveTest.stimulate(10)

        // Verify that the event was received
        assertEquals(
            expected = listOf(10),
            actual = collectedEvents.copyAndClear(),
        )

        // Emit an extraneous event from the source stream
        reactiveTest.stimulate(20)

        // Verify that the extraneous event was not received
        assertEquals(
            expected = emptyList(),
            actual = collectedEvents,
        )
    }

    @Test
    fun test_count1_missed() {
        val (takeEventStream, reactiveTest) = setup(
            count = 1,
        )

        // Emit a single event from the source stream
        reactiveTest.stimulate(10)

        val collectedEvents = mutableListOf<Int>()

        takeEventStream.subscribeCollecting(
            targetList = collectedEvents,
        )

        // Emit an extraneous event from the source stream
        reactiveTest.stimulate(20)

        // Verify that no events were received
        assertEquals(
            expected = emptyList(),
            actual = collectedEvents,
        )
    }

    @Test
    fun test_countFew_caughtAll() {
        val (takeEventStream, reactiveTest) = setup(
            count = 3,
        )

        val collectedEvents = mutableListOf<Int>()

        takeEventStream.subscribeCollecting(
            targetList = collectedEvents,
        )

        // Emit some events from the source stream

        reactiveTest.stimulate(10)

        assertEquals(
            expected = listOf(10),
            actual = collectedEvents.copyAndClear(),
        )

        reactiveTest.stimulate(20)

        assertEquals(
            expected = listOf(20),
            actual = collectedEvents.copyAndClear(),
        )

        reactiveTest.stimulate(30)

        assertEquals(
            expected = listOf(30),
            actual = collectedEvents.copyAndClear(),
        )

        // Emit an extraneous event from the source stream
        reactiveTest.stimulate(20)

        // Verify that the extraneous event was not received
        assertEquals(
            expected = emptyList(),
            actual = collectedEvents,
        )
    }

    @Test
    fun test_countFew_caughtSome() {
        val (takeEventStream, reactiveTest) = setup(
            count = 3,
        )

        val collectedEvents = mutableListOf<Int>()

        // Emit some events from the source stream

        reactiveTest.stimulate(10)

        // Subscribe after the first event was emitted
        takeEventStream.subscribeCollecting(
            targetList = collectedEvents,
        )

        // Emit some more events from the source stream

        reactiveTest.stimulate(20)

        assertEquals(
            expected = listOf(20),
            actual = collectedEvents.copyAndClear(),
        )

        reactiveTest.stimulate(30)

        assertEquals(
            expected = listOf(30),
            actual = collectedEvents.copyAndClear(),
        )

        // Emit an extraneous event from the source stream
        reactiveTest.stimulate(20)

        // Verify that the extraneous event was not received
        assertEquals(
            expected = emptyList(),
            actual = collectedEvents,
        )
    }

    @Test
    fun test_countFew_missedAll() {
        val (takeEventStream, reactiveTest) = setup(
            count = 2,
        )

        // Emit some events from the source stream

        reactiveTest.stimulate(10)

        reactiveTest.stimulate(20)

        val collectedEvents = mutableListOf<Int>()

        // Subscribe after the events were emitted
        takeEventStream.subscribeCollecting(
            targetList = collectedEvents,
        )

        // Emit an extraneous event from the source stream
        reactiveTest.stimulate(30)

        // Verify that no events were received
        assertEquals(
            expected = emptyList(),
            actual = collectedEvents,
        )
    }

    @Test
    fun test_countFew_cancelled() {
        val (takeEventStream, reactiveTest) = setup(
            count = 4,
        )

        val collectedEvents = mutableListOf<Int>()

        val subscription = takeEventStream.subscribeCollecting(
            targetList = collectedEvents,
        )

        // Emit some events from the source stream

        reactiveTest.stimulate(10)

        assertEquals(
            expected = listOf(10),
            actual = collectedEvents.copyAndClear(),
        )

        reactiveTest.stimulate(20)

        assertEquals(
            expected = listOf(20),
            actual = collectedEvents.copyAndClear(),
        )

        // Cancel the subscription
        subscription.cancel()

        reactiveTest.stimulate(30)

        // Verify that no more events were received
        assertEquals(
            expected = emptyList(),
            actual = collectedEvents,
        )
    }

    @Test
    fun test_countFew_resubscribed() {
        val (takeEventStream, reactiveTest) = setup(
            count = 5,
        )

        val collectedEvents = mutableListOf<Int>()

        val subscription = takeEventStream.subscribeCollecting(
            targetList = collectedEvents,
        )

        // Emit some events from the source stream

        reactiveTest.stimulate(10)

        assertEquals(
            expected = listOf(10),
            actual = collectedEvents.copyAndClear(),
        )

        reactiveTest.stimulate(20)

        assertEquals(
            expected = listOf(20),
            actual = collectedEvents.copyAndClear(),
        )

        // Cancel the subscription
        subscription.cancel()

        // Emit some events

        reactiveTest.stimulate(30)

        reactiveTest.stimulate(40)

        // Re-subscribe to the event stream

        takeEventStream.subscribeCollecting(
            targetList = collectedEvents,
        )

        // Emit some more events

        reactiveTest.stimulate(50)

        // Verify that no more events were received
        assertEquals(
            expected = listOf(50),
            actual = collectedEvents,
        )

        // Emit an extraneous event from the source stream
        reactiveTest.stimulate(60)

        // Verify that the extraneous event was not received
        assertEquals(
            expected = emptyList(),
            actual = collectedEvents,
        )
    }
}
