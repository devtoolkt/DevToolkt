package dev.toolkt.reactive.event_stream

import dev.toolkt.core.utils.iterable.copyAndClear
import dev.toolkt.reactive.test_utils.ReactiveTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Suppress("ClassName")
class EventStream_map_tests {
    private fun setup(): Pair<EventStream<String>, ReactiveTest<Int>> =
        ReactiveTest.setup {
            val sourceEventStream = formEventStream()

            val mapStream = sourceEventStream.map {
                it.toString()
            }

            mapStream
        }

    @Test
    fun test_eventPropagation() {
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
            expected = listOf("20"),
            actual = collectedEvents.copyAndClear(),
        )
    }

    @Test
    fun test_eventPropagation_cancelled() {
        val (mapEventStream, reactiveTest) = setup()

        val collectedEvents = mutableListOf<String>()

        val subscription = assertNotNull(
            mapEventStream.subscribeCollecting(
                targetList = collectedEvents,
            ),
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
