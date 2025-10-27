package dev.toolkt.reactive.cell.map4.updating

import dev.toolkt.reactive.cell.map4.Map4GenericScenario
import dev.toolkt.reactive.cell.map4.Map4GenericScenario.SourceCellVariant
import dev.toolkt.reactive.cell.map4.Map4GenericScenario.TimelineTick
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore // FIXME
@Suppress("ClassName")
class multipleSourcesUpdateSimultaneously_tests {
    private val twoSourcesUpdateSimultaneouslyScenario1 = Map4GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.FrozeAtEarlierUpdate,
        sourceCell2Variant = SourceCellVariant.UpdatingNow.FreezingNow,
        sourceCell3Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.Const,
        sourceCell4Variant = SourceCellVariant.UpdatingNow.NotFreezingNow,
        shouldExpectFreeze = false,
    )

    @Test
    fun test_twoSourcesUpdateSimultaneouslyScenario1_observedAtTick_finalSourceUpdateTick() {
        twoSourcesUpdateSimultaneouslyScenario1.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_twoSourcesUpdateSimultaneouslyScenario1_observedAtTick_preFinalSourceUpdateTick() {
        twoSourcesUpdateSimultaneouslyScenario1.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_twoSourcesUpdateSimultaneouslyScenario1_observedAtTick_initial() {
        twoSourcesUpdateSimultaneouslyScenario1.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val twoSourcesUpdateSimultaneouslyScenario2 = Map4GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.UpdatingNow.NotFreezingNow,
        sourceCell2Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.FrozeAtEarlierUpdate,
        sourceCell3Variant = SourceCellVariant.UpdatingNow.FreezingNow,
        sourceCell4Variant = SourceCellVariant.NotUpdatingNow.WarmNow.NotFreezingNow,
        shouldExpectFreeze = false,
    )

    @Test
    fun test_twoSourcesUpdateSimultaneouslyScenario2_observedAtTick_finalSourceUpdateTick() {
        twoSourcesUpdateSimultaneouslyScenario2.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_twoSourcesUpdateSimultaneouslyScenario2_observedAtTick_preFinalSourceUpdateTick() {
        twoSourcesUpdateSimultaneouslyScenario2.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_twoSourcesUpdateSimultaneouslyScenario2_observedAtTick_initial() {
        twoSourcesUpdateSimultaneouslyScenario2.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val twoSourcesUpdateSimultaneouslyScenario3 = Map4GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.NotUpdatingNow.WarmNow.NotFreezingNow,
        sourceCell2Variant = SourceCellVariant.UpdatingNow.NotFreezingNow,
        sourceCell3Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.FrozeAtEarlierUpdate,
        sourceCell4Variant = SourceCellVariant.UpdatingNow.FreezingNow,
        shouldExpectFreeze = false,
    )

    @Test
    fun test_twoSourcesUpdateSimultaneouslyScenario3_observedAtTick_finalSourceUpdateTick() {
        twoSourcesUpdateSimultaneouslyScenario3.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_twoSourcesUpdateSimultaneouslyScenario3_observedAtTick_preFinalSourceUpdateTick() {
        twoSourcesUpdateSimultaneouslyScenario3.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_twoSourcesUpdateSimultaneouslyScenario3_observedAtTick_initial() {
        twoSourcesUpdateSimultaneouslyScenario3.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val threeSourcesUpdateSimultaneouslyScenario1 = Map4GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.FrozeAtEarlierUpdate,
        sourceCell2Variant = SourceCellVariant.UpdatingNow.FreezingNow,
        sourceCell3Variant = SourceCellVariant.UpdatingNow.NotFreezingNow,
        sourceCell4Variant = SourceCellVariant.UpdatingNow.NotFreezingNow,
        shouldExpectFreeze = false,
    )

    @Test
    fun test_threeSourcesUpdateSimultaneouslyScenario1_observedAtTick_finalSourceUpdateTick() {
        threeSourcesUpdateSimultaneouslyScenario1.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_threeSourcesUpdateSimultaneouslyScenario1_observedAtTick_preFinalSourceUpdateTick() {
        threeSourcesUpdateSimultaneouslyScenario1.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_threeSourcesUpdateSimultaneouslyScenario1_observedAtTick_initial() {
        threeSourcesUpdateSimultaneouslyScenario1.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val threeSourcesUpdateSimultaneouslyScenario2 = Map4GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.UpdatingNow.NotFreezingNow,
        sourceCell2Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.FrozeAtEarlierUpdate,
        sourceCell3Variant = SourceCellVariant.UpdatingNow.FreezingNow,
        sourceCell4Variant = SourceCellVariant.UpdatingNow.NotFreezingNow,
        shouldExpectFreeze = false,
    )

    @Test
    fun test_threeSourcesUpdateSimultaneouslyScenario2_observedAtTick_finalSourceUpdateTick() {
        threeSourcesUpdateSimultaneouslyScenario2.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_threeSourcesUpdateSimultaneouslyScenario2_observedAtTick_preFinalSourceUpdateTick() {
        threeSourcesUpdateSimultaneouslyScenario2.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_threeSourcesUpdateSimultaneouslyScenario2_observedAtTick_initial() {
        threeSourcesUpdateSimultaneouslyScenario2.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val allSourcesUpdateSimultaneouslyScenario = Map4GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.UpdatingNow.FreezingNow,
        sourceCell2Variant = SourceCellVariant.UpdatingNow.NotFreezingNow,
        sourceCell3Variant = SourceCellVariant.UpdatingNow.FreezingNow,
        sourceCell4Variant = SourceCellVariant.UpdatingNow.NotFreezingNow,
        shouldExpectFreeze = false,
    )

    @Test
    fun test_allSourcesUpdateSimultaneouslyScenario_observedAtTick_finalSourceUpdateTick() {
        allSourcesUpdateSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_allSourcesUpdateSimultaneouslyScenario_observedAtTick_preFinalSourceUpdateTick() {
        allSourcesUpdateSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_allSourcesUpdateSimultaneouslyScenario_observedAtTick_initial() {
        allSourcesUpdateSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val allSourcesUpdateFreezingSimultaneouslyScenario = Map4GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.UpdatingNow.FreezingNow,
        sourceCell2Variant = SourceCellVariant.UpdatingNow.FreezingNow,
        sourceCell3Variant = SourceCellVariant.UpdatingNow.FreezingNow,
        sourceCell4Variant = SourceCellVariant.UpdatingNow.FreezingNow,
        shouldExpectFreeze = true,
    )

    @Test
    fun test_allSourcesUpdateFreezingSimultaneouslyScenario_observedAtTick_finalSourceUpdateTick() {
        allSourcesUpdateFreezingSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_allSourcesUpdateFreezingSimultaneouslyScenario_observedAtTick_preFinalSourceUpdateTick() {
        allSourcesUpdateFreezingSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_allSourcesUpdateFreezingSimultaneouslyScenario_observedAtTick_initial() {
        allSourcesUpdateFreezingSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }
}
