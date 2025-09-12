package dev.toolkt.reactive.event_stream

import dev.toolkt.core.utils.iterable.copyAndClear
import dev.toolkt.reactive.test_utils.ReactiveTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

@Ignore // TODO: Implement this logic
@Suppress("ClassName")
class EventStream_merge2_tests {
    private data class StimulationEvent(
        val sourceEvent1: Int? = null,
        val sourceEvent2: Int? = null,
    )

    private fun setup(): Pair<EventStream<Int>, ReactiveTest<StimulationEvent>> = ReactiveTest.setup {
        val sourceEventStream1 = extractEventStream(StimulationEvent::sourceEvent1)
        val sourceEventStream2 = extractEventStream(StimulationEvent::sourceEvent2)

        EventStream.merge2(
            eventStream1 = sourceEventStream1,
            eventStream2 = sourceEventStream2,
        )
    }

    @Test
    fun test_eventPropagation_source1Occurrence() {
        val (merge2EventStream, reactiveTest) = setup()

        val collectedEvents = mutableListOf<Int>()

        merge2EventStream.subscribeCollecting(
            targetList = collectedEvents,
        )

        reactiveTest.stimulate(
            StimulationEvent(
                sourceEvent1 = 10,
            ),
        )

        assertEquals(
            expected = listOf(10),
            actual = collectedEvents.copyAndClear(),
        )

        reactiveTest.stimulate(
            StimulationEvent(
                sourceEvent1 = 11,
            ),
        )

        assertEquals(
            expected = listOf(11),
            actual = collectedEvents.copyAndClear(),
        )
    }

    @Test
    fun test_eventPropagation_source2Occurrence() {
        val (merge2EventStream, reactiveTest) = setup()

        val collectedEvents = mutableListOf<Int>()

        merge2EventStream.subscribeCollecting(
            targetList = collectedEvents,
        )

        reactiveTest.stimulate(
            StimulationEvent(
                sourceEvent2 = 20,
            ),
        )

        assertEquals(
            expected = listOf(20),
            actual = collectedEvents.copyAndClear(),
        )

        reactiveTest.stimulate(
            StimulationEvent(
                sourceEvent2 = 21,
            ),
        )

        assertEquals(
            expected = listOf(21),
            actual = collectedEvents.copyAndClear(),
        )
    }

    @Test
    fun test_eventPropagation_simultaneousOccurrence() {
        val (merge2EventStream, reactiveTest) = setup()

        val collectedEvents = mutableListOf<Int>()

        merge2EventStream.subscribeCollecting(
            targetList = collectedEvents,
        )

        // Emit events from both streams simultaneously
        reactiveTest.stimulate(
            StimulationEvent(
                sourceEvent1 = 10,
                sourceEvent2 = 20,
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
                sourceEvent1 = 11,
                sourceEvent2 = 21,
            ),
        )

        // Verify that the first stream takes precedence (again)
        assertEquals(
            expected = listOf(11),
            actual = collectedEvents.copyAndClear(),
        )
    }

    @Test
    fun test_eventPropagation_cancelled() {
        val (mapEventStream, reactiveTest) = setup()

        val collectedEvents = mutableListOf<Int>()

        val subscription = mapEventStream.subscribeCollecting(
            targetList = collectedEvents,
        )

        reactiveTest.stimulate(
            StimulationEvent(
                sourceEvent2 = 21,
            ),
        )

        reactiveTest.stimulate(
            StimulationEvent(
                sourceEvent1 = 11,
                sourceEvent2 = 21,
            ),
        )

        collectedEvents.clear()

        subscription.cancel()

        reactiveTest.stimulate(
            StimulationEvent(
                sourceEvent1 = 21,
                sourceEvent2 = 31,
            ),
        )

        assertEquals(
            expected = emptyList(),
            actual = collectedEvents,
        )
    }
}
