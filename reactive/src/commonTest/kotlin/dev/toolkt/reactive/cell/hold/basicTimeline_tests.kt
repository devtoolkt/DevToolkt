package dev.toolkt.reactive.cell.hold

import dev.toolkt.reactive.cell.test_utils.timeline.BaseNamedTick
import dev.toolkt.reactive.cell.test_utils.timeline.CellTimelineVerifier.Active.ObservationSpan
import dev.toolkt.reactive.cell.test_utils.timeline.ExpectedCellTimeline
import dev.toolkt.reactive.cell.test_utils.timeline.buildStatefulCellTimelineTestScenario
import dev.toolkt.reactive.cell.test_utils.timeline.defineInputEventStream
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.hold
import kotlin.test.Test

@Suppress("ClassName")
class basicTimeline_tests {
    private data class Input<ValueT>(
        val sourceEventStream: EventStream<ValueT>,
    )

    private enum class NamedTick : BaseNamedTick {
        Spawn, PreEmissionPause, SourceEmission1, SourceEmission2, SourceEmission3, PreTerminationPause, SourceTermination, PostTerminationPause;
    }

    private val testScenario = buildStatefulCellTimelineTestScenario(
        defineInput = {
            val sourceEventStream = defineInputEventStream(
                emittedEventByNamedTick = mapOf(
                    NamedTick.SourceEmission1 to 11,
                    NamedTick.SourceEmission2 to 12,
                    NamedTick.SourceEmission3 to 13,
                ),
                terminationNamedTick = NamedTick.SourceTermination,
            )

            Input(
                sourceEventStream = sourceEventStream,
            )
        },
        spawnNamedTick = NamedTick.Spawn,
        spawnStatefulSubjectCell = {
            sourceEventStream.hold(initialValue = 10)
        },
        expectedSubjectCellTimeline = ExpectedCellTimeline.Dynamic(
            expectedInitialValue = 10,
            expectedUpdatedValueByNamedTick = mapOf(
                NamedTick.SourceEmission1 to 11,
                NamedTick.SourceEmission2 to 12,
                NamedTick.SourceEmission3 to 13,
            ),
            // FIXME: Implement termination/freezing
            // expectedFreezeNamedTick = NamedTick.SourceTermination,
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
    fun testActivelyFromPreEmissionPause() {
        testScenario.testActively(
            observationSpan = ObservationSpan(
                firstObservedNamedTick = NamedTick.PreEmissionPause,
            ),
        )
    }

    @Test
    fun testActivelyFromPreTerminationPause() {
        testScenario.testActively(
            observationSpan = ObservationSpan(
                firstObservedNamedTick = NamedTick.PreTerminationPause,
            ),
        )
    }

    @Test
    fun testActivelyFromSourceTermination() {
        testScenario.testActively(
            observationSpan = ObservationSpan(
                firstObservedNamedTick = NamedTick.SourceTermination,
            ),
        )
    }

    @Test
    fun testActivelyFromPostTerminationPause() {
        testScenario.testActively(
            observationSpan = ObservationSpan(
                firstObservedNamedTick = NamedTick.PostTerminationPause,
            ),
        )
    }
}
