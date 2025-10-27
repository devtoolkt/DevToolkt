package dev.toolkt.reactive.cell.map4.updating

import dev.toolkt.reactive.cell.map4.Map4GenericScenario
import dev.toolkt.reactive.cell.map4.Map4GenericScenario.SourceCellVariant
import dev.toolkt.reactive.cell.map4.Map4GenericScenario.TimelineTick
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore // FIXME
@Suppress("ClassName")
class otherSourcesUpdateAfterSourceFreeze_tests {
    private val otherSourcesUpdateAfterSource1FreezesScenario = Map4GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.FrozeAtEarlierUpdate,
        sourceCell2Variant = SourceCellVariant.UpdatingNow.FreezingNow,
        sourceCell3Variant = SourceCellVariant.NotUpdatingNow.WarmNow.NotFreezingNow,
        sourceCell4Variant = SourceCellVariant.UpdatingNow.NotFreezingNow,
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

    private val otherSourcesUpdateAfterSource2FreezesScenario = Map4GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.UpdatingNow.NotFreezingNow,
        sourceCell2Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.FrozeAfterEarlierUpdate,
        sourceCell3Variant = SourceCellVariant.NotUpdatingNow.WarmNow.NotFreezingNow,
        sourceCell4Variant = SourceCellVariant.UpdatingNow.FreezingNow,
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

    private val otherSourcesUpdateAfterSource3FreezesScenario = Map4GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.UpdatingNow.NotFreezingNow,
        sourceCell2Variant = SourceCellVariant.NotUpdatingNow.WarmNow.NotFreezingNow,
        sourceCell3Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.FrozeAfterEarlierUpdate,
        sourceCell4Variant = SourceCellVariant.UpdatingNow.FreezingNow,
        shouldExpectFreeze = false,
    )

    @Test
    fun test_otherSourcesUpdateAfterSource3FreezesScenario_observedAtTick_finalSourceUpdateTick() {
        otherSourcesUpdateAfterSource3FreezesScenario.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_otherSourcesUpdateAfterSource3FreezesScenario_observedAtTick_preFinalSourceUpdateTick() {
        otherSourcesUpdateAfterSource3FreezesScenario.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_otherSourcesUpdateAfterSource3FreezesScenario_observedAtTick_initial() {
        otherSourcesUpdateAfterSource3FreezesScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val otherSourcesUpdateAfterSource4FreezesScenario = Map4GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.UpdatingNow.FreezingNow,
        sourceCell2Variant = SourceCellVariant.NotUpdatingNow.WarmNow.NotFreezingNow,
        sourceCell3Variant = SourceCellVariant.NotUpdatingNow.WarmNow.NotFreezingNow,
        sourceCell4Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.FrozeAtEarlierUpdate,
        shouldExpectFreeze = false
    )

    @Test
    fun test_otherSourcesUpdateAfterSource4FreezesScenario_observedAtTick_finalSourceUpdateTick() {
        otherSourcesUpdateAfterSource4FreezesScenario.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_otherSourcesUpdateAfterSource4FreezesScenario_observedAtTick_preFinalSourceUpdateTick() {
        otherSourcesUpdateAfterSource4FreezesScenario.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_otherSourcesUpdateAfterSource4FreezesScenario_observedAtTick_initial() {
        otherSourcesUpdateAfterSource4FreezesScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }
}
