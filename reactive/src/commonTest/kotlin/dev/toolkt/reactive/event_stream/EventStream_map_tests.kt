package dev.toolkt.reactive.event_stream

import dev.toolkt.core.utils.iterable.copyAndClear
import dev.toolkt.reactive.test_utils.ReactiveTest
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("ClassName")
class EventStream_map_tests {
    private fun setup(): Pair<EventStream<String>, ReactiveTest<Int>> =
        ReactiveTest.setupWithSingleInputEventStream { inputEventStream ->
            val mapStream = inputEventStream.map {
                it.toString()
            }

            mapStream
        }

    @Test
    fun testMap_propagation() {
        val (mapEventStream, reactiveTest) = setup()

        val collectedEvents = mutableListOf<String>()

        mapEventStream.subscribeCollecting(
            targetList = collectedEvents,
        )

        reactiveTest.stimulate(10)

        assertEquals(
            expected = listOf("10"),
            actual = collectedEvents.copyAndClear(),
        )

        reactiveTest.stimulate(20)

        assertEquals(
            expected = listOf("30"),
            actual = collectedEvents.copyAndClear(),
        )
    }

    @Test
    fun testMap_propagation_cancelled() {
        val (mapEventStream, reactiveTest) = setup()

        val collectedEvents = mutableListOf<String>()

        val subscription = mapEventStream.subscribeCollecting(
            targetList = collectedEvents,
        )

        reactiveTest.stimulate(10)

        collectedEvents.clear()

        subscription.cancel()

        reactiveTest.stimulate(20)

        assertEquals(
            expected = emptyList(),
            actual = collectedEvents,
        )
    }
}
