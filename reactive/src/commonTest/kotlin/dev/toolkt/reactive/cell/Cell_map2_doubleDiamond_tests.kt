package dev.toolkt.reactive.cell

import dev.toolkt.core.utils.iterable.copyAndClear
import dev.toolkt.reactive.cell.test_utils.sampleExternally
import dev.toolkt.reactive.event_stream.subscribeCollecting
import dev.toolkt.reactive.test_utils.ReactiveTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * ```
 *      L       R
 *     / \     / \
 *    /   \   /   \
 *   ▼     ▼ ▼     ▼
 *  O_l     C     O_r
 *   \     / \     /
 *    \   /   \   /
 *     ▼ ▼     ▼ ▼
 *     F_l     F_r
 * ```
 */
@Ignore // TODO: Implement this logic
@Suppress("ClassName")
class Cell_map2_doubleDiamond_tests {
    private data class Stimulation(
        val newSourceValueLeft: Int? = null,
        val newSourceValueRight: Char? = null,
    )

    private data class System(
        val funnelCellLeft: Cell<String>,
        val funnelCellRight: Cell<String>,
    )

    private fun setup(
        initialSourceValueLeft: Int,
        initialSourceValueRight: Char,
    ): Pair<System, ReactiveTest<Stimulation>> = ReactiveTest.setup {
        // (L)
        val sourceCellLeft = extractCell(
            initialValue = initialSourceValueLeft,
            selector = Stimulation::newSourceValueLeft,
        )

        // (R)
        val sourceCellRight = extractCell(
            initialValue = initialSourceValueRight,
            selector = Stimulation::newSourceValueRight,
        )

        // (O_l)
        val intermediateCellOuterLeft = sourceCellLeft.map {
            (it + 1).toString()
        }

        // (C)
        val intermediateCellCentral = Cell.map2(
            cell1 = sourceCellLeft,
            cell2 = sourceCellRight,
        ) { value1, value2 ->
            "$value1:$value2"
        }

        // (O_r)
        val intermediateCellOuterRight = sourceCellRight.map {
            "$it${it.uppercase()}"
        }

        // (F_l)
        val funnelCellLeft = Cell.map2(
            cell1 = intermediateCellOuterLeft,
            cell2 = intermediateCellCentral,
        ) { outerLeftValue, centralValue ->
            "$outerLeftValue>$centralValue"
        }

        // (F_r)
        val funnelCellRight = Cell.map2(
            cell1 = intermediateCellCentral,
            cell2 = intermediateCellOuterRight,
        ) { value1, value2 ->
            "$value1<$value2"
        }

        System(
            funnelCellLeft = funnelCellLeft,
            funnelCellRight = funnelCellRight,
        )
    }

    @Test
    fun test_initial() {
        val (system, _) = setup(
            initialSourceValueLeft = 8,
            initialSourceValueRight = 'x',
        )

        assertEquals(
            expected = "9:8>x",
            actual = system.funnelCellLeft.sampleExternally(),
        )

        assertEquals(
            expected = "8:x<xX",
            actual = system.funnelCellRight.sampleExternally(),
        )
    }

    @Test
    fun test_sourceLeftUpdate() {
        val (system, reactiveTest) = setup(
            initialSourceValueLeft = 0,
            initialSourceValueRight = 'A',
        )

        val collectedEventsLeft = mutableListOf<String>()

        val funnelCellLeft = system.funnelCellLeft
        val funnelCellRight = system.funnelCellRight

        funnelCellLeft.newValues.subscribeCollecting(
            targetList = collectedEventsLeft,
        )

        val collectedEventsRight = mutableListOf<String>()

        funnelCellRight.newValues.subscribeCollecting(
            targetList = collectedEventsRight,
        )

        reactiveTest.stimulate(
            Stimulation(
                newSourceValueLeft = 2,
            ),
        )

        assertEquals(
            expected = "3:2>a",
            actual = funnelCellLeft.sampleExternally(),
        )

        assertEquals(
            expected = listOf("3:2>a"),
            actual = collectedEventsLeft.copyAndClear(),
        )

        assertEquals(
            expected = "2:a<aA",
            actual = funnelCellRight.sampleExternally(),
        )

        assertEquals(
            expected = listOf("2:a<aA"),
            actual = collectedEventsRight.copyAndClear(),
        )
    }

    @Test
    fun test_sourceRightUpdate() {
        val (system, reactiveTest) = setup(
            initialSourceValueLeft = 0,
            initialSourceValueRight = 'A',
        )

        val funnelCellLeft = system.funnelCellLeft
        val funnelCellRight = system.funnelCellRight

        val collectedEventsLeft = mutableListOf<String>()

        funnelCellLeft.newValues.subscribeCollecting(
            targetList = collectedEventsLeft,
        )

        val collectedEventsRight = mutableListOf<String>()

        funnelCellRight.newValues.subscribeCollecting(
            targetList = collectedEventsRight,
        )

        reactiveTest.stimulate(
            Stimulation(
                newSourceValueRight = 'Z',
            ),
        )

        assertEquals(
            expected = "1:0>z",
            actual = funnelCellLeft.sampleExternally(),
        )

        assertEquals(
            expected = listOf("1:0>z"),
            actual = collectedEventsLeft.copyAndClear(),
        )

        assertEquals(
            expected = "0:z<zZ",
            actual = funnelCellRight.sampleExternally(),
        )

        assertEquals(
            expected = listOf("0:z<zZ"),
            actual = collectedEventsRight.copyAndClear(),
        )
    }

    @Test
    fun test_simultaneousSourceUpdate() {
        val (system, reactiveTest) = setup(
            initialSourceValueLeft = 0,
            initialSourceValueRight = 'A',
        )

        val funnelCellLeft = system.funnelCellLeft
        val funnelCellRight = system.funnelCellRight

        val collectedEventsLeft = mutableListOf<String>()

        funnelCellLeft.newValues.subscribeCollecting(
            targetList = collectedEventsLeft,
        )

        val collectedEventsRight = mutableListOf<String>()

        funnelCellRight.newValues.subscribeCollecting(
            targetList = collectedEventsRight,
        )

        reactiveTest.stimulate(
            Stimulation(
                newSourceValueLeft = 3,
                newSourceValueRight = 'M',
            ),
        )

        assertEquals(
            expected = "4:3>m",
            actual = funnelCellLeft.sampleExternally(),
        )

        assertEquals(
            expected = listOf("4:3>m"),
            actual = collectedEventsLeft.copyAndClear(),
        )

        assertEquals(
            expected = "3:m<mM",
            actual = funnelCellRight.sampleExternally(),
        )

        assertEquals(
            expected = listOf("3:m<mM"),
            actual = collectedEventsRight.copyAndClear(),
        )
    }
}
