package dev.toolkt.reactive.cell.test_utils.timeline

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.test_utils.timeline.CellTimelineVerifier.Active.ObservationSpan

abstract class CellTimelineTestScenario {
    fun testPassively() {
        testVerified(
            timelineVerifier = CellTimelineVerifier.Passive,
        )
    }

    fun testActivelyFull() {
        testActively(
            observationSpan = ObservationSpan.Full,
        )
    }

    fun testActively(
        observationSpan: ObservationSpan,
    ) {
        testVerified(
            timelineVerifier = CellTimelineVerifier.Active(
                observationSpan = observationSpan,
            ),
        )
    }

    abstract fun testVerified(
        timelineVerifier: CellTimelineVerifier,
    )
}

fun <InputT, ValueT : Any> buildStatelessCellTimelineTestScenario(
    defineInput: context (InputDefinitionContext) () -> InputT,
    instantiateStatelessSubjectCell: InputT.() -> Cell<ValueT>,
    expectedSubjectCellTimeline: ExpectedCellTimeline<ValueT>,
): CellTimelineTestScenario = object : CellTimelineTestScenario() {
    override fun testVerified(
        timelineVerifier: CellTimelineVerifier,
    ) {
        val ticker = TimelineTicker()

        val (configuredInput, lastInputTick) = InputDefinitionContext.build(
            defineInput = defineInput,
            ticker = ticker,
        )

        expectedSubjectCellTimeline.validate(
            minTick = RawTick.First,
            maxTick = lastInputTick,
        )

        val subjectCell = configuredInput.instantiateStatelessSubjectCell()

        timelineVerifier.verifyStatelessCell(
            ticker = ticker,
            statelessSubjectCell = subjectCell,
            lastVerifiedTick = lastInputTick,
            expectedSubjectCellTimeline = expectedSubjectCellTimeline,
        )
    }
}

fun <InputT, ValueT : Any> buildStatefulCellTimelineTestScenario(
    defineInput: context (InputDefinitionContext) () -> InputT,
    spawnNamedTick: BaseNamedTick,
    spawnStatefulSubjectCell: context(MomentContext) InputT.() -> Cell<ValueT>,
    expectedSubjectCellTimeline: ExpectedCellTimeline.Dynamic<ValueT>,
): CellTimelineTestScenario = object : CellTimelineTestScenario() {
    override fun testVerified(
        timelineVerifier: CellTimelineVerifier,
    ) {
        val spawnTick = spawnNamedTick.ordinalTick
        val firstExpectedUpdateTick = expectedSubjectCellTimeline.firstExpectedUpdateTick
        val expectedFreezeTick = expectedSubjectCellTimeline.expectedFreezeTick

        if (firstExpectedUpdateTick != null) {
            require(!firstExpectedUpdateTick.isEarlierThan(spawnTick)) {
                "The first expected update tick (t=${firstExpectedUpdateTick.t}) cannot be earlier than the spawn tick (t=${spawnTick.t})."
            }
        }

        if (expectedFreezeTick != null) {
            require(!expectedFreezeTick.isEarlierThan(spawnTick)) {
                "The expected freeze tick (t=${expectedFreezeTick.t}) cannot be earlier than the spawn tick (t=${spawnTick.t})."
            }
        }

        val ticker = TimelineTicker()

        val (configuredInput, lastInputTick) = InputDefinitionContext.build(
            defineInput = defineInput,
            ticker = ticker,
        )

        expectedSubjectCellTimeline.validate(
            minTick = spawnTick,
            maxTick = lastInputTick,
        )

        timelineVerifier.verifyStatefulCell(
            ticker = ticker,
            spawnTick = spawnTick,
            spawnStatefulSubjectCell = { configuredInput.spawnStatefulSubjectCell() },
            lastVerifiedTick = lastInputTick,
            expectedSubjectCellTimeline = expectedSubjectCellTimeline,
        )
    }
}
