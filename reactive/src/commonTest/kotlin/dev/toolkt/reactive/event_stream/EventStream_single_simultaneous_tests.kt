package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.test_utils.sampleExternally
import dev.toolkt.reactive.test_utils.ReactiveTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@Suppress("ClassName")
class EventStream_single_simultaneous_tests {
    private data class Stimulation(
        val trigger: Unit? = null,
        val sourceEvent: Int? = null,
    )

    private data class Snapshot(
        val singleEventStream: EventStream<Int>,
        val memoryCell: Cell<Int>,
    )

    private fun setup(): Pair<Cell<Snapshot?>, ReactiveTest<Stimulation>> = ReactiveTest.setup {
        val triggerEventStream = extractEventStream(
            selector = Stimulation::trigger,
        )

        val sourceEventStream = extractEventStream(
            selector = Stimulation::sourceEvent,
        )

        val snapshotCell: Cell<Snapshot?> = triggerEventStream.mapAt {
            val singleEventStream = sourceEventStream.single()

            Snapshot(
                singleEventStream = singleEventStream,
                memoryCell = singleEventStream.hold(0),
            )
        }.hold(
            initialValue = null,
        )

        snapshotCell
    }

    @Test
    fun test_eventPropagation() {
        val (snapshotCell, reactiveTest) = setup()

        assertNull(
            snapshotCell.sampleExternally(),
        )

        reactiveTest.stimulate(
            Stimulation(
                trigger = Unit,
                sourceEvent = 10,
            ),
        )

        val snapshot = assertNotNull(
            snapshotCell.sampleExternally(),
        )

        assertEquals(
            expected = 10,
            actual = snapshot.memoryCell.sampleExternally(),
        )

        val collectedEvents = mutableListOf<Int>()

        snapshot.singleEventStream.subscribeCollecting(
            targetList = collectedEvents,
        )

        reactiveTest.stimulate(
            Stimulation(
                sourceEvent = 11,
            ),
        )

        assertEquals(
            expected = emptyList(),
            actual = collectedEvents,
        )
    }
}
