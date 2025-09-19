package dev.toolkt.reactive.cell

import dev.toolkt.core.utils.iterable.copyAndClear
import dev.toolkt.reactive.cell.test_utils.sampleExternally
import dev.toolkt.reactive.event_stream.subscribeCollecting
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("ClassName")
class Cell_switch_const_tests {
    @Test
    fun test_constOuter() {
        val mutableCell = MutableCell(
            initialValue = 10,
        )

        val outerCell = Cell.of(
            mutableCell.map { it.toString() },
        )

        val switchCell = Cell.switch(outerCell)

        val collectedEvents = mutableListOf<String>()

        switchCell.newValues.subscribeCollecting(
            targetList = collectedEvents,
        )

        assertEquals(
            expected = "10",
            actual = switchCell.sampleExternally(),
        )

        mutableCell.set(11)

        assertEquals(
            expected = listOf("11"),
            actual = collectedEvents,
        )

        assertEquals(
            expected = "11",
            actual = switchCell.sampleExternally(),
        )
    }

    @Test
    fun test_constInner() {
        val mutableInnerCell1 = MutableCell(
            initialValue = 10,
        )

        val mutableInnerCell3 = MutableCell(
            initialValue = 30,
        )

        val mutableOuterCell = MutableCell<Cell<Int>>(
            initialValue = mutableInnerCell1,
        )

        val switchCell = Cell.switch(mutableOuterCell)

        val collectedEvents = mutableListOf<Int>()

        switchCell.newValues.subscribeCollecting(
            targetList = collectedEvents,
        )

        assertEquals(
            expected = 10,
            actual = switchCell.sampleExternally(),
        )

        mutableOuterCell.set(Cell.of(20))

        assertEquals(
            expected = listOf(20),
            actual = collectedEvents.copyAndClear(),
        )

        assertEquals(
            expected = 20,
            actual = switchCell.sampleExternally(),
        )

        mutableOuterCell.set(mutableInnerCell3)

        assertEquals(
            expected = listOf(30),
            actual = collectedEvents,
        )

        assertEquals(
            expected = 30,
            actual = switchCell.sampleExternally(),
        )
    }
}
