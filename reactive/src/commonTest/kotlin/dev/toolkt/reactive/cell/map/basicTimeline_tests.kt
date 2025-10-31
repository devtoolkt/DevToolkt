package dev.toolkt.reactive.cell.map

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.map
import dev.toolkt.reactive.cell.test_utils.timeline.BaseNamedTick
import dev.toolkt.reactive.cell.test_utils.timeline.CellTimelineVerifier.Active.ObservationSpan
import dev.toolkt.reactive.cell.test_utils.timeline.ExpectedCellTimeline
import dev.toolkt.reactive.cell.test_utils.timeline.buildStatelessCellTimelineTestScenario
import dev.toolkt.reactive.cell.test_utils.timeline.defineInputCell
import kotlin.test.Test

@Suppress("ClassName")
class basicTimeline_tests {
    private data class Input<ValueT>(
        val sourceCell: Cell<ValueT>,
    )

    private enum class NamedTick : BaseNamedTick {
        PreUpdatePause, SourceUpdate1, SourceUpdate2, SourceUpdate3, PreFreezePause, SourceFreeze, PostFreezePause;
    }

    private val testScenario = buildStatelessCellTimelineTestScenario(
        defineInput = {
            val sourceEventStream = defineInputCell(
                initialValue = 10,
                updatedValueByNamedTick = mapOf(
                    NamedTick.SourceUpdate1 to 11,
                    NamedTick.SourceUpdate2 to 12,
                    NamedTick.SourceUpdate3 to 13,
                ),
                freezeNamedTick = NamedTick.SourceFreeze,
            )

            Input(
                sourceCell = sourceEventStream,
            )
        },
        instantiateStatelessSubjectCell = {
            sourceCell.map { it.toString() }
        },
        expectedSubjectCellTimeline = ExpectedCellTimeline.Dynamic(
            expectedInitialValue = "10",
            expectedUpdatedValueByNamedTick = mapOf(
                NamedTick.SourceUpdate1 to "11",
                NamedTick.SourceUpdate2 to "12",
                NamedTick.SourceUpdate3 to "13",
            ),
            // FIXME: Implement termination/freezing
            // expectedFreezeNamedTick = NamedTick.SourceFreeze,
            expectedFreezeNamedTick = null,
        ),
    )

    @Test
    fun testPassively() {
        testScenario.testPassively()
    }

    @Test
    fun testActivelyFull() {
        testScenario.testActivelyFull()
    }

    @Test
    fun testActivelyFromPreUpdatePause() {
        testScenario.testActively(
            observationSpan = ObservationSpan(
                firstObservedNamedTick = NamedTick.PreUpdatePause,
            ),
        )
    }

    @Test
    fun testActivelyFromPreFreezePause() {
        testScenario.testActively(
            observationSpan = ObservationSpan(
                firstObservedNamedTick = NamedTick.PreFreezePause,
            ),
        )
    }

    @Test
    fun testActivelyFromSourceFreeze() {
        testScenario.testActively(
            observationSpan = ObservationSpan(
                firstObservedNamedTick = NamedTick.SourceFreeze,
            ),
        )
    }

    @Test
    fun testActivelyFromPostFreezePause() {
        testScenario.testActively(
            observationSpan = ObservationSpan(
                firstObservedNamedTick = NamedTick.PostFreezePause,
            ),
        )
    }
}
