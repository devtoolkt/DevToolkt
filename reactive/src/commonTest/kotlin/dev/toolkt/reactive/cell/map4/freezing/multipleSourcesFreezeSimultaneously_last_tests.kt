package dev.toolkt.reactive.cell.map4.freezing

import dev.toolkt.reactive.cell.map4.Map4GenericScenario
import dev.toolkt.reactive.cell.map4.Map4GenericScenario.SourceCellVariant
import dev.toolkt.reactive.cell.map4.Map4GenericScenario.TimelineTick
import kotlin.test.Ignore
import kotlin.test.Test

/**
 * Multiple source cells freeze, leaving all source cells frozen. None of them updates.
 *
 * The result cell should not update.
 *
 * The result cell should freeze.
 */
@Ignore // FIXME
@Suppress("ClassName")
class multipleSourcesFreezeSimultaneously_last_tests {
    private val twoSourcesFreezeSimultaneouslyLastScenario1 = Map4GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.NotUpdatingNow.WarmNow.FreezingNow,
        sourceCell2Variant = SourceCellVariant.NotUpdatingNow.WarmNow.FreezingNow,
        sourceCell3Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.Const,
        sourceCell4Variant = SourceCellVariant.NotUpdatingNow.WarmNow.NotFreezingNow,
        shouldExpectFreeze = false,
    )

    @Test
    fun test_twoSourcesFreezeSimultaneouslyLastScenario1_observedAtTick_finalSourceUpdateTick() {
        twoSourcesFreezeSimultaneouslyLastScenario1.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_twoSourcesFreezeSimultaneouslyLastScenario1_observedAtTick_preFinalSourceUpdateTick() {
        twoSourcesFreezeSimultaneouslyLastScenario1.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_twoSourcesFreezeSimultaneouslyLastScenario1_observedAtTick_initial() {
        twoSourcesFreezeSimultaneouslyLastScenario1.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val twoSourcesFreezeSimultaneouslyLastScenario2 = Map4GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.NotUpdatingNow.WarmNow.FreezingNow,
        sourceCell2Variant = SourceCellVariant.NotUpdatingNow.WarmNow.NotFreezingNow,
        sourceCell3Variant = SourceCellVariant.NotUpdatingNow.WarmNow.FreezingNow,
        sourceCell4Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.Const,
        shouldExpectFreeze = false,
    )

    @Test
    fun test_twoSourcesFreezeSimultaneouslyLastScenario2_observedAtTick_finalSourceUpdateTick() {
        twoSourcesFreezeSimultaneouslyLastScenario2.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_twoSourcesFreezeSimultaneouslyLastScenario2_observedAtTick_preFinalSourceUpdateTick() {
        twoSourcesFreezeSimultaneouslyLastScenario2.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_twoSourcesFreezeSimultaneouslyLastScenario2_observedAtTick_initial() {
        twoSourcesFreezeSimultaneouslyLastScenario2.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val twoSourcesFreezeSimultaneouslyLastScenario3 = Map4GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.NotUpdatingNow.WarmNow.NotFreezingNow,
        sourceCell2Variant = SourceCellVariant.NotUpdatingNow.WarmNow.FreezingNow,
        sourceCell3Variant = SourceCellVariant.NotUpdatingNow.WarmNow.FreezingNow,
        sourceCell4Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.FrozeAtEarlierUpdate,
        shouldExpectFreeze = false,
    )

    @Test
    fun test_twoSourcesFreezeSimultaneouslyLastScenario3_observedAtTick_finalSourceUpdateTick() {
        twoSourcesFreezeSimultaneouslyLastScenario3.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_twoSourcesFreezeSimultaneouslyLastScenario3_observedAtTick_preFinalSourceUpdateTick() {
        twoSourcesFreezeSimultaneouslyLastScenario3.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_twoSourcesFreezeSimultaneouslyLastScenario3_observedAtTick_initial() {
        twoSourcesFreezeSimultaneouslyLastScenario3.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val threeSourcesFreezeSimultaneouslyLastScenario1 = Map4GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.NotUpdatingNow.WarmNow.FreezingNow,
        sourceCell2Variant = SourceCellVariant.NotUpdatingNow.WarmNow.NotFreezingNow,
        sourceCell3Variant = SourceCellVariant.NotUpdatingNow.WarmNow.FreezingNow,
        sourceCell4Variant = SourceCellVariant.NotUpdatingNow.WarmNow.FreezingNow,
        shouldExpectFreeze = false,
    )

    @Test
    fun test_threeSourcesFreezeSimultaneouslyLastScenario1_observedAtTick_finalSourceUpdateTick() {
        threeSourcesFreezeSimultaneouslyLastScenario1.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_threeSourcesFreezeSimultaneouslyLastScenario1_observedAtTick_preFinalSourceUpdateTick() {
        threeSourcesFreezeSimultaneouslyLastScenario1.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_threeSourcesFreezeSimultaneouslyLastScenario1_observedAtTick_initial() {
        threeSourcesFreezeSimultaneouslyLastScenario1.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val threeSourcesFreezeSimultaneouslyLastScenario2 = Map4GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.NotUpdatingNow.WarmNow.FreezingNow,
        sourceCell2Variant = SourceCellVariant.NotUpdatingNow.WarmNow.FreezingNow,
        sourceCell3Variant = SourceCellVariant.NotUpdatingNow.WarmNow.FreezingNow,
        sourceCell4Variant = SourceCellVariant.NotUpdatingNow.WarmNow.NotFreezingNow,
        shouldExpectFreeze = false,
    )

    @Test
    fun test_threeSourcesFreezeSimultaneouslyLastScenario2_observedAtTick_finalSourceUpdateTick() {
        threeSourcesFreezeSimultaneouslyLastScenario2.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_threeSourcesFreezeSimultaneouslyLastScenario2_observedAtTick_preFinalSourceUpdateTick() {
        threeSourcesFreezeSimultaneouslyLastScenario2.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_threeSourcesFreezeSimultaneouslyLastScenario2_observedAtTick_initial() {
        threeSourcesFreezeSimultaneouslyLastScenario2.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }
}
