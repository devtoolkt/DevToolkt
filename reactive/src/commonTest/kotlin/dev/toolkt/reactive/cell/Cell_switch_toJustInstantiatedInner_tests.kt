package dev.toolkt.reactive.cell

import dev.toolkt.core.utils.iterable.copyAndClear
import dev.toolkt.reactive.cell.test_utils.sampleExternally
import dev.toolkt.reactive.event_stream.subscribeCollecting
import dev.toolkt.reactive.test_utils.ReactiveTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * ```
 *  S_1   S_2
 *   ╷     ╷
 *   ╷     ╷
 *   ▼     ▼
 *  I_1   I_2
 *   \     •
 *    \   •
 *     \ •
 *      ▼
 * O╶╶▶ S
 * ```
 */
@Suppress("ClassName")
class Cell_switch_toJustInstantiatedInner_tests {
    private sealed interface SwitchCaseId {
        val id: Int

        data object Case1 : SwitchCaseId {
            override val id: Int = 1
        }

        data object Case2 : SwitchCaseId {
            override val id: Int = 2
        }
    }

    private data class Stimulation(
        val newSwitchCaseId: SwitchCaseId? = null,
        val newSourceValue1: Int? = null,
        val newSourceValue2: Int? = null,
    )

    private fun setup(
        initialSourceValue1: Int,
        initialSourceValue2: Int,
        initialSwitchCaseId: SwitchCaseId,
    ): Pair<Cell<String>, ReactiveTest<Stimulation>> = ReactiveTest.setup {
        // (S_1)
        val sourceCell1 = extractCell(
            initialValue = initialSourceValue1,
            selector = Stimulation::newSourceValue1,
        )

        // (S_2)
        val sourceCell2 = extractCell(
            initialValue = initialSourceValue2,
            selector = Stimulation::newSourceValue2,
        )

        val switchCaseId = extractCell(
            initialValue = initialSwitchCaseId,
            selector = Stimulation::newSwitchCaseId,
        )

        // (O)
        val outerCell = switchCaseId.map { caseIdNow ->
            // Instantiate a cell that couldn't have been considered when planning a propagation graph for a given
            // transaction, yet its new value might be needed immediately.
            when (caseIdNow) {
                // (I_1)
                SwitchCaseId.Case1 -> sourceCell1.map {
                    it.toString()
                }

                // (I_2)
                SwitchCaseId.Case2 -> sourceCell2.map {
                    it.toString()
                }
            }
        }

        // (S)
        Cell.switch(
            outerCell = outerCell,
        )
    }

    @Test
    fun test_outerUpdate() {
        val (switchCell, reactiveTest) = setup(
            initialSourceValue1 = 10,
            initialSourceValue2 = 20,
            initialSwitchCaseId = SwitchCaseId.Case1,
        )

        val collectedEvents = mutableListOf<String>()

        switchCell.newValues.subscribeCollecting(
            targetList = collectedEvents,
        )

        reactiveTest.stimulate(
            Stimulation(
                newSwitchCaseId = SwitchCaseId.Case2,
            ),
        )

        assertEquals(
            expected = "20",
            actual = switchCell.sampleExternally(),
        )

        assertEquals(
            expected = listOf("20"),
            actual = collectedEvents.copyAndClear(),
        )
    }

    @Test
    fun test_simultaneousUpdate() {
        val (switchCell, reactiveTest) = setup(
            initialSourceValue1 = 11,
            initialSourceValue2 = 21,
            initialSwitchCaseId = SwitchCaseId.Case1,
        )

        val collectedEvents = mutableListOf<String>()

        switchCell.newValues.subscribeCollecting(
            targetList = collectedEvents,
        )

        reactiveTest.stimulate(
            Stimulation(
                newSwitchCaseId = SwitchCaseId.Case2,
                newSourceValue2 = 22,
            ),
        )

        assertEquals(
            expected = "22",
            actual = switchCell.sampleExternally(),
        )

        assertEquals(
            expected = listOf("22"),
            actual = collectedEvents.copyAndClear(),
        )
    }
}
