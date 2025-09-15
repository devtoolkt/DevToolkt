package dev.toolkt.reactive.cell

import dev.toolkt.core.utils.iterable.copyAndClear
import dev.toolkt.reactive.cell.test_utils.sampleExternally
import dev.toolkt.reactive.event_stream.hold
import dev.toolkt.reactive.event_stream.mapAt
import dev.toolkt.reactive.event_stream.subscribeCollecting
import dev.toolkt.reactive.test_utils.ReactiveTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * ```
 *  I        J_i
 *   \       •
 *    \     •
 *     \   •
 *      \ •
 *       ▼
 * O╶╶╶▶ S
 * ```
 */
@Ignore // TODO: Implement this logic
@Suppress("ClassName")
class Cell_switch_toJunior_tests {
    private data class Stimulation(
        val newInitialInnerValue: Int? = null,
        val newInnerValue: Int? = null,
    )

    private fun setup(
        initialValue: Int,
    ): Pair<Cell<Int>, ReactiveTest<Stimulation>> = ReactiveTest.setup {
        // (I)
        val initialCell = Cell.of(initialValue)

        val newInitialInnerValues = extractEventStream(
            selector = Stimulation::newInitialInnerValue,
        )

        val newInnerValues = extractEventStream(
            selector = Stimulation::newInnerValue,
        )

        val newJuniorCells = newInitialInnerValues.mapAt { newInitialInnerValue ->
            // Spawn a "junior" cell (a "just-born" cell which couldn't have been considered when planning a propagation
            // graph for a given transaction, yet its new value might be needed in that transaction).
            // (J_i)
            newInnerValues.hold(initialValue = newInitialInnerValue)
        }

        // (O)
        val outerCell = newJuniorCells.hold(initialValue = initialCell)

        // (S)
        Cell.switch(
            outerCell = outerCell,
        )
    }

    @Test
    fun test_outerUpdate() {
        val (switchCell, reactiveTest) = setup(
            initialValue = 0,
        )

        val collectedEvents = mutableListOf<Int>()

        switchCell.newValues.subscribeCollecting(
            targetList = collectedEvents,
        )

        reactiveTest.stimulate(
            Stimulation(
                newInitialInnerValue = 10,
            ),
        )

        assertEquals(
            expected = 10,
            actual = switchCell.sampleExternally(),
        )

        assertEquals(
            expected = listOf(10),
            actual = collectedEvents.copyAndClear(),
        )
    }

    @Test
    fun test_laterJuniorUpdate() {
        val (switchCell, reactiveTest) = setup(
            initialValue = 1,
        )

        val collectedEvents = mutableListOf<Int>()

        switchCell.newValues.subscribeCollecting(
            targetList = collectedEvents,
        )

        reactiveTest.stimulate(
            Stimulation(
                newInitialInnerValue = 10,
            ),
        )

        collectedEvents.clear()

        reactiveTest.stimulate(
            Stimulation(
                newInnerValue = 20,
            ),
        )

        assertEquals(
            expected = 20,
            actual = switchCell.sampleExternally(),
        )

        assertEquals(
            expected = listOf(20),
            actual = collectedEvents,
        )
    }

    @Test
    fun test_simultaneousJuniorUpdate() {
        val (switchCell, reactiveTest) = setup(
            initialValue = 1,
        )

        val collectedEvents = mutableListOf<Int>()

        switchCell.newValues.subscribeCollecting(
            targetList = collectedEvents,
        )

        reactiveTest.stimulate(
            Stimulation(
                newInitialInnerValue = 10,
                newInnerValue = 11,
            ),
        )

        assertEquals(
            expected = 11,
            actual = switchCell.sampleExternally(),
        )

        assertEquals(
            expected = listOf(11),
            actual = collectedEvents,
        )
    }
}
