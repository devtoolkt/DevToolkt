package dev.toolkt.reactive.cell

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.test_utils.sampleExternally
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.hold
import dev.toolkt.reactive.event_stream.map
import dev.toolkt.reactive.event_stream.subscribeCollecting
import dev.toolkt.reactive.test_utils.ReactiveTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("ClassName")
class Cell_switch_activating_tests {
    private fun setup(
        buildInnerCell: context(MomentContext) (tickStream: EventStream<Unit>) -> Cell<String>,
    ): Pair<Cell<String>, ReactiveTest<Unit>> = ReactiveTest.setup {
        val tickStream = formEventStream()

        val innerCell = buildInnerCell(tickStream)

        val outerCell = tickStream.map { innerCell }.hold(
            initialValue = Cell.of(""),
        )

        Cell.switch(
            outerCell = outerCell,
        )
    }

    @Test
    fun test_holdInnerCell() {
        val (switchCell, reactiveTest) = setup { tickStream ->
            tickStream.map { "y" }.hold(initialValue = "x")
        }

        val collectedEvents = mutableListOf<String>()

        switchCell.newValues.subscribeCollecting(
            targetList = collectedEvents,
        )

        reactiveTest.stimulate(Unit)

        assertEquals(
            expected = "y",
            actual = switchCell.sampleExternally(),
        )

        assertEquals(
            expected = listOf("y"),
            actual = collectedEvents,
        )
    }

    @Test
    fun test_mapInnerCell() {
        val (switchCell, reactiveTest) = setup { tickStream ->
            tickStream.map { 1 }.hold(initialValue = 0).map {
                it.toString()
            }
        }

        val collectedEvents = mutableListOf<String>()

        switchCell.newValues.subscribeCollecting(
            targetList = collectedEvents,
        )

        reactiveTest.stimulate(Unit)

        assertEquals(
            expected = "1",
            actual = switchCell.sampleExternally(),
        )

        assertEquals(
            expected = listOf("1"),
            actual = collectedEvents,
        )
    }

    @Ignore // FIXME: Fix this case
    @Test
    fun test_switchInnerCell_nonSimultaneous() {
        val (switchCell, reactiveTest) = setup { tickStream ->
            val nestedInnerCell = MutableCell(initialValue = ";")

            val nestedOuterCell = tickStream.map {
                nestedInnerCell
            }.hold(
                initialValue = Cell.of(",")
            )

            Cell.switch(nestedOuterCell)
        }

        val collectedEvents = mutableListOf<String>()

        switchCell.newValues.subscribeCollecting(
            targetList = collectedEvents,
        )

        reactiveTest.stimulate(Unit)

        assertEquals(
            expected = ";",
            actual = switchCell.sampleExternally(),
        )

        assertEquals(
            expected = listOf(";"),
            actual = collectedEvents,
        )
    }

    @Test
    fun test_switchInnerCell_simultaneous() {
        val (switchCell, reactiveTest) = setup { tickStream ->
            val nestedInnerCell = tickStream.map { "w" }.hold(initialValue = "z")

            val nestedOuterCell = tickStream.map {
                nestedInnerCell
            }.hold(
                initialValue = Cell.of(".")
            )

            Cell.switch(nestedOuterCell)
        }

        val collectedEvents = mutableListOf<String>()

        switchCell.newValues.subscribeCollecting(
            targetList = collectedEvents,
        )

        reactiveTest.stimulate(Unit)

        assertEquals(
            expected = "w",
            actual = switchCell.sampleExternally(),
        )

        assertEquals(
            expected = listOf("w"),
            actual = collectedEvents,
        )
    }
}
