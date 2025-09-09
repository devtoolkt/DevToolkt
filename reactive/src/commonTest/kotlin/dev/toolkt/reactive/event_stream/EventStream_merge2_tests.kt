package dev.toolkt.reactive.event_stream

import dev.toolkt.core.utils.iterable.copyAndClear
import dev.toolkt.reactive.test_utils.ReactiveTest
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("ClassName")
class EventStream_merge2_tests {
    private data class StimulationEvent(
        val event1: Int? = null,
        val event2: Int? = null,
    )

    private fun setup(): Pair<EventStream<Int>, ReactiveTest<StimulationEvent>> = ReactiveTest.setup {
        val inputEventStream1 = extractEventStream(StimulationEvent::event1)
        val inputEventStream2 = extractEventStream(StimulationEvent::event2)

        EventStream.merge2(
            eventStream1 = inputEventStream1,
            eventStream2 = inputEventStream2,
        )
    }

    @Test
    fun testMerge2_propagation_nonSimultaneous1() {
        val (merge2EventStream, reactiveTest) = setup()

        val collectedEvents = mutableListOf<Int>()

        merge2EventStream.subscribeCollecting(
            targetList = collectedEvents,
        )

        reactiveTest.stimulate(
            StimulationEvent(
                event1 = 10,
            ),
        )

        assertEquals(
            expected = listOf(10),
            actual = collectedEvents.copyAndClear(),
        )

        reactiveTest.stimulate(
            StimulationEvent(
                event1 = 11,
            ),
        )

        assertEquals(
            expected = listOf(11),
            actual = collectedEvents.copyAndClear(),
        )
    }

    @Test
    fun testMerge2_propagation_nonSimultaneous2() {
        val (merge2EventStream, reactiveTest) = setup()

        val collectedEvents = mutableListOf<Int>()

        merge2EventStream.subscribeCollecting(
            targetList = collectedEvents,
        )

        reactiveTest.stimulate(
            StimulationEvent(
                event2 = 20,
            ),
        )

        assertEquals(
            expected = listOf(20),
            actual = collectedEvents.copyAndClear(),
        )

        reactiveTest.stimulate(
            StimulationEvent(
                event2 = 21,
            ),
        )

        assertEquals(
            expected = listOf(21),
            actual = collectedEvents.copyAndClear(),
        )
    }

    @Test
    fun testMerge2_propagation_simultaneous() {
        val (merge2EventStream, reactiveTest) = setup()

        val collectedEvents = mutableListOf<Int>()

        merge2EventStream.subscribeCollecting(
            targetList = collectedEvents,
        )

        // Emit events from both streams simultaneously
        reactiveTest.stimulate(
            StimulationEvent(
                event1 = 10,
                event2 = 20,
            ),
        )

        // Verify that the first stream takes precedence
        assertEquals(
            expected = listOf(10),
            actual = collectedEvents.copyAndClear(),
        )

        // Emit events from both streams simultaneously (again)
        reactiveTest.stimulate(
            StimulationEvent(
                event1 = 11,
                event2 = 21,
            ),
        )

        // Verify that the first stream takes precedence (again)
        assertEquals(
            expected = listOf(11),
            actual = collectedEvents.copyAndClear(),
        )
    }

    @Test
    fun testMerge2_propagation_cancelled() {
        val (mapEventStream, reactiveTest) = setup()

        val collectedEvents = mutableListOf<Int>()

        val subscription = mapEventStream.subscribeCollecting(
            targetList = collectedEvents,
        )

        reactiveTest.stimulate(
            StimulationEvent(
                event2 = 21,
            ),
        )

        reactiveTest.stimulate(
            StimulationEvent(
                event1 = 11,
                event2 = 21,
            ),
        )

        collectedEvents.clear()

        subscription.cancel()

        reactiveTest.stimulate(
            StimulationEvent(
                event1 = 21,
                event2 = 31,
            ),
        )

        assertEquals(
            expected = emptyList(),
            actual = collectedEvents,
        )
    }
}
