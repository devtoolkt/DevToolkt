package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.sample
import dev.toolkt.reactive.cell.test_utils.sampleExternally
import dev.toolkt.reactive.test_utils.ReactiveTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@Suppress("ClassName")
class EventStream_hold_simultaneous_tests {
    private data class Stimulation(
        val trigger: Int? = null,
        val sourceEvent: Int? = null,
    )

    private data class Snapshot(
        val holdCell: Cell<Int>,
        val sampledValue: Int,
    )

    private fun setup(): Pair<Cell<Snapshot?>, ReactiveTest<Stimulation>> = ReactiveTest.setup {
        val triggerEventStream = extractEventStream(
            selector = Stimulation::trigger,
        )

        val sourceEventStream = extractEventStream(
            selector = Stimulation::sourceEvent,
        )

        val snapshotCell: Cell<Snapshot?> = triggerEventStream.mapAt { trigger ->
            val holdCell = sourceEventStream.hold(trigger)

            Snapshot(
                holdCell = holdCell,
                sampledValue = holdCell.sample(),
            )
        }.hold(
            initialValue = null,
        )

        snapshotCell
    }

    @Test
    fun test() {
        val (snapshotCell, reactiveTest) = setup()

        assertNull(
            snapshotCell.sampleExternally(),
        )

        reactiveTest.stimulate(
            Stimulation(
                trigger = 10,
                sourceEvent = 11,
            ),
        )

        val snapshot = assertNotNull(
            snapshotCell.sampleExternally(),
        )

        assertEquals(
            expected = 10,
            actual = snapshot.sampledValue,
        )

        assertEquals(
            expected = 11,
            actual = snapshot.holdCell.sampleExternally(),
        )
    }
}
