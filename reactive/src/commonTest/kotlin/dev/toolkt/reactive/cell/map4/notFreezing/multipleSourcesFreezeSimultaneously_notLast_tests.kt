package dev.toolkt.reactive.cell.map4.notFreezing

import dev.toolkt.reactive.cell.map4.Map4GenericScenario
import dev.toolkt.reactive.cell.map4.Map4GenericScenario.SourceCellVariant
import dev.toolkt.reactive.cell.map4.Map4GenericScenario.TimelineTick
import kotlin.test.Ignore
import kotlin.test.Test

/**
 * Multiple source cells freeze, leaving some of the other source cells warm. No source cell updates.
 *
 * The result cell should not update.
 *
 * The result cell should not freeze.
 */
@Ignore // FIXME
@Suppress("ClassName")
class multipleSourcesFreezeSimultaneously_notLast_tests {
    private val twoSourcesFreezeSimultaneouslyScenario1 = Map4GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.NotUpdatingNow.WarmNow.FreezingNow,
        sourceCell2Variant = SourceCellVariant.NotUpdatingNow.WarmNow.FreezingNow,
        sourceCell3Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.Const,
        sourceCell4Variant = SourceCellVariant.NotUpdatingNow.WarmNow.NotFreezingNow,
        shouldExpectFreeze = false,
    )

    @Test
    fun test_twoSourcesFreezeSimultaneouslyScenario1_observedAtTick_finalSourceUpdateTick() {
        twoSourcesFreezeSimultaneouslyScenario1.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_twoSourcesFreezeSimultaneouslyScenario1_observedAtTick_preFinalSourceUpdateTick() {
        twoSourcesFreezeSimultaneouslyScenario1.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_twoSourcesFreezeSimultaneouslyScenario1_observedAtTick_initial() {
        twoSourcesFreezeSimultaneouslyScenario1.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val twoSourcesFreezeSimultaneouslyScenario2 = Map4GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.NotUpdatingNow.WarmNow.FreezingNow,
        sourceCell2Variant = SourceCellVariant.NotUpdatingNow.WarmNow.NotFreezingNow,
        sourceCell3Variant = SourceCellVariant.NotUpdatingNow.WarmNow.FreezingNow,
        sourceCell4Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.Const,
        shouldExpectFreeze = false,
    )

    @Test
    fun test_twoSourcesFreezeSimultaneouslyScenario2_observedAtTick_finalSourceUpdateTick() {
        twoSourcesFreezeSimultaneouslyScenario2.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_twoSourcesFreezeSimultaneouslyScenario2_observedAtTick_preFinalSourceUpdateTick() {
        twoSourcesFreezeSimultaneouslyScenario2.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_twoSourcesFreezeSimultaneouslyScenario2_observedAtTick_initial() {
        twoSourcesFreezeSimultaneouslyScenario2.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val twoSourcesFreezeSimultaneouslyScenario3 = Map4GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.NotUpdatingNow.WarmNow.NotFreezingNow,
        sourceCell2Variant = SourceCellVariant.NotUpdatingNow.WarmNow.FreezingNow,
        sourceCell3Variant = SourceCellVariant.NotUpdatingNow.WarmNow.FreezingNow,
        sourceCell4Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.FrozeAtEarlierUpdate,
        shouldExpectFreeze = false,
    )

    @Test
    fun test_twoSourcesFreezeSimultaneouslyScenario3_observedAtTick_finalSourceUpdateTick() {
        twoSourcesFreezeSimultaneouslyScenario3.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_twoSourcesFreezeSimultaneouslyScenario3_observedAtTick_preFinalSourceUpdateTick() {
        twoSourcesFreezeSimultaneouslyScenario3.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_twoSourcesFreezeSimultaneouslyScenario3_observedAtTick_initial() {
        twoSourcesFreezeSimultaneouslyScenario3.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val threeSourcesFreezeSimultaneouslyScenario1 = Map4GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.NotUpdatingNow.WarmNow.FreezingNow,
        sourceCell2Variant = SourceCellVariant.NotUpdatingNow.WarmNow.NotFreezingNow,
        sourceCell3Variant = SourceCellVariant.NotUpdatingNow.WarmNow.FreezingNow,
        sourceCell4Variant = SourceCellVariant.NotUpdatingNow.WarmNow.FreezingNow,
        shouldExpectFreeze = false,
    )

    @Test
    fun test_threeSourcesFreezeSimultaneouslyScenario1_observedAtTick_finalSourceUpdateTick() {
        threeSourcesFreezeSimultaneouslyScenario1.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_threeSourcesFreezeSimultaneouslyScenario1_observedAtTick_preFinalSourceUpdateTick() {
        threeSourcesFreezeSimultaneouslyScenario1.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_threeSourcesFreezeSimultaneouslyScenario1_observedAtTick_initial() {
        threeSourcesFreezeSimultaneouslyScenario1.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val threeSourcesFreezeSimultaneouslyScenario2 = Map4GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.NotUpdatingNow.WarmNow.FreezingNow,
        sourceCell2Variant = SourceCellVariant.NotUpdatingNow.WarmNow.FreezingNow,
        sourceCell3Variant = SourceCellVariant.NotUpdatingNow.WarmNow.FreezingNow,
        sourceCell4Variant = SourceCellVariant.NotUpdatingNow.WarmNow.NotFreezingNow,
        shouldExpectFreeze = false,
    )

    @Test
    fun test_threeSourcesFreezeSimultaneouslyScenario2_observedAtTick_finalSourceUpdateTick() {
        threeSourcesFreezeSimultaneouslyScenario2.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_threeSourcesFreezeSimultaneouslyScenario2_observedAtTick_preFinalSourceUpdateTick() {
        threeSourcesFreezeSimultaneouslyScenario2.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_threeSourcesFreezeSimultaneouslyScenario2_observedAtTick_initial() {
        threeSourcesFreezeSimultaneouslyScenario2.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }
}
