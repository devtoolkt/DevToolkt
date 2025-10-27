package dev.toolkt.reactive.cell.map2.updating

import dev.toolkt.reactive.cell.map2.Map2GenericScenario
import dev.toolkt.reactive.cell.map2.Map2GenericScenario.SourceCellVariant
import dev.toolkt.reactive.cell.map2.Map2GenericScenario.TimelineTick
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore // FIXME
@Suppress("ClassName")
class otherSourceUpdateAfterSourceFreeze_tests {
    private val otherSourcesUpdateAfterSource1FreezesScenario = Map2GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.FrozeAtEarlierUpdate,
        sourceCell2Variant = SourceCellVariant.UpdatingNow.FreezingNow,
        shouldExpectFreeze = false,
    )

    @Test
    fun test_otherSourcesUpdateAfterSource1FreezesScenario_observedAtTick_finalSourceUpdateTick() {
        otherSourcesUpdateAfterSource1FreezesScenario.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_otherSourcesUpdateAfterSource1FreezesScenario_observedAtTick_preFinalSourceUpdateTick() {
        otherSourcesUpdateAfterSource1FreezesScenario.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_otherSourcesUpdateAfterSource1FreezesScenario_observedAtTick_initial() {
        otherSourcesUpdateAfterSource1FreezesScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val otherSourcesUpdateAfterSource2FreezesScenario = Map2GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.UpdatingNow.NotFreezingNow,
        sourceCell2Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.FrozeAfterEarlierUpdate,
        shouldExpectFreeze = false,
    )

    @Test
    fun test_otherSourcesUpdateAfterSource2FreezesScenario_observedAtTick_finalSourceUpdateTick() {
        otherSourcesUpdateAfterSource2FreezesScenario.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_otherSourcesUpdateAfterSource2FreezesScenario_observedAtTick_preFinalSourceUpdateTick() {
        otherSourcesUpdateAfterSource2FreezesScenario.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_otherSourcesUpdateAfterSource2FreezesScenario_observedAtTick_initial() {
        otherSourcesUpdateAfterSource2FreezesScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }
}
