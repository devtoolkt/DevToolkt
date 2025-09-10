package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.test_utils.ReactiveTest
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("ClassName")
class EventStream_filter_tests {
    private fun setup(): Pair<EventStream<Int>, ReactiveTest<Int>> = ReactiveTest.setup {
        val sourceEventStream = formEventStream()

        val mapStream = sourceEventStream.filter {
            it % 2 == 0
        }

        mapStream
    }

    @Test
    fun test_eventPropagation_passed() {
        val (mapEventStream, reactiveTest) = setup()

        val collectedEvents = mutableListOf<Int>()

        mapEventStream.subscribeCollecting(
            targetList = collectedEvents,
        )

        reactiveTest.stimulate(2)

        assertEquals(
            expected = listOf(2),
            actual = collectedEvents,
        )

        reactiveTest.stimulate(6)

        assertEquals(
            expected = listOf(6),
            actual = collectedEvents,
        )
    }

    @Test
    fun test_eventPropagation_blocked() {
        val (mapEventStream, reactiveTest) = setup()

        val collectedEvents = mutableListOf<Int>()

        mapEventStream.subscribeCollecting(
            targetList = collectedEvents,
        )

        reactiveTest.stimulate(3)

        assertEquals(
            expected = emptyList(),
            actual = collectedEvents,
        )

        reactiveTest.stimulate(7)

        assertEquals(
            expected = emptyList(),
            actual = collectedEvents,
        )
    }

    @Test
    fun test_eventPropagation_cancelled() {
        val (mapEventStream, reactiveTest) = setup()

        val collectedEvents = mutableListOf<Int>()

        val subscription = mapEventStream.subscribeCollecting(
            targetList = collectedEvents,
        )

        reactiveTest.stimulate(2)

        collectedEvents.clear()

        subscription.cancel()

        reactiveTest.stimulate(4)

        assertEquals(
            expected = emptyList(),
            actual = collectedEvents,
        )
    }
}
