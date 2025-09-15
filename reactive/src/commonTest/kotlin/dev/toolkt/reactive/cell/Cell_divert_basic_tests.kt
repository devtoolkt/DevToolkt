package dev.toolkt.reactive.cell

import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.subscribeCollecting
import dev.toolkt.reactive.test_utils.ReactiveTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * ```
 * I_1  I_2  I_3
 *  \    •    •
 *   \   •   •
 *    \  •  •
 *     \ • •
 *      \••
 *       ▼
 * O╶╶╶▶ D
 * ```
 */
@Ignore // TODO: Implement this logic
@Suppress("ClassName")
class Cell_divert_basic_tests {
    private enum class DivertCaseId {
        Case1, Case2, Case3,
    }

    private data class Stimulation(
        val newDivertCaseId: DivertCaseId? = null,
        val innerEvent1: Int? = null,
        val innerEvent2: Int? = null,
        val innerEvent3: Int? = null,
    )

    private fun setup(
        initialDivertCaseId: DivertCaseId,
    ): Pair<EventStream<Int>, ReactiveTest<Stimulation>> = ReactiveTest.setup {
        // (I_1)
        val innerEventStream1 = extractEventStream(
            selector = Stimulation::innerEvent1,
        )

        // (I_2)
        val innerEventStream2 = extractEventStream(
            selector = Stimulation::innerEvent1,
        )

        // (I_3)
        val innerEventStream3 = extractEventStream(
            selector = Stimulation::innerEvent1,
        )

        val divertCaseId = extractCell(
            initialValue = initialDivertCaseId,
            selector = Stimulation::newDivertCaseId,
        )

        // (O)
        val outerCell = divertCaseId.map { caseIdNow ->
            when (caseIdNow) {
                DivertCaseId.Case1 -> innerEventStream1
                DivertCaseId.Case2 -> innerEventStream2
                DivertCaseId.Case3 -> innerEventStream3
            }
        }

        // (D)
        Cell.divert(
            outerCell = outerCell,
        )
    }

    private fun test_eventPropagation(
        initialDivertCaseId: DivertCaseId,
        stimulation: Stimulation,
        expectedValue: Int?,
    ) {
        val (divertEventStream, reactiveSystem) = setup(
            initialDivertCaseId = initialDivertCaseId,
        )

        val collectedEvents = mutableListOf<Int>()

        divertEventStream.subscribeCollecting(
            targetList = collectedEvents,
        )

        reactiveSystem.stimulate(stimulation)

        assertEquals(
            expected = listOfNotNull(expectedValue),
            actual = collectedEvents,
        )
    }

    @Test
    fun test_eventPropagation_outerUpdate() {
        test_eventPropagation(
            initialDivertCaseId = DivertCaseId.Case1,
            stimulation = Stimulation(
                newDivertCaseId = DivertCaseId.Case3,
            ),
            expectedValue = null,
        )
    }

    @Test
    fun test_eventPropagation_innerUpdate() {
        test_eventPropagation(
            initialDivertCaseId = DivertCaseId.Case2,
            stimulation = Stimulation(
                innerEvent2 = 22,
            ),
            expectedValue = 22,
        )
    }

    @Test
    fun test_eventPropagation_simultaneousUpdate() {
        test_eventPropagation(
            initialDivertCaseId = DivertCaseId.Case3,
            stimulation = Stimulation(
                newDivertCaseId = DivertCaseId.Case1,
                innerEvent1 = 11,
                innerEvent3 = 31,
            ),
            expectedValue = 31,
        )
    }

    @Test
    fun test_eventPropagation_afterCancel() {
        val (divertEventStream, reactiveSystem) = setup(
            initialDivertCaseId = DivertCaseId.Case1,
        )

        val collectedEvents = mutableListOf<Int>()

        val subscription = assertNotNull(
            divertEventStream.subscribeCollecting(
                targetList = collectedEvents,
            ),
        )

        reactiveSystem.stimulate(
            Stimulation(
                newDivertCaseId = DivertCaseId.Case2,
                innerEvent1 = 11,
                innerEvent2 = 21,
                innerEvent3 = 31,
            ),
        )

        collectedEvents.clear()

        subscription.cancel()

        reactiveSystem.stimulate(
            Stimulation(
                newDivertCaseId = DivertCaseId.Case3,
                innerEvent1 = 12,
                innerEvent2 = 22,
                innerEvent3 = 32,
            ),
        )

        assertEquals(
            expected = emptyList(),
            actual = collectedEvents,
        )
    }
}
