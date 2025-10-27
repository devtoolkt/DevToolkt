package dev.toolkt.reactive.cell.map4.freezing

import dev.toolkt.reactive.cell.map4.Map4GenericScenario
import dev.toolkt.reactive.cell.map4.Map4GenericScenario.SourceCellVariant
import dev.toolkt.reactive.cell.map4.Map4GenericScenario.TimelineTick
import kotlin.test.Ignore
import kotlin.test.Test

/**
 * The last warm source cells freezes (without updating).
 *
 * The result cell should not update.
 *
 * The result cell should freeze.
 */
@Ignore // FIXME
@Suppress("ClassName")
class singleSourceFreezes_last_tests {
    private val justSource1FreezesLastScenario = Map4GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.NotUpdatingNow.WarmNow.FreezingNow,
        sourceCell2Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.FrozeAtEarlierUpdate,
        sourceCell3Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.Const,
        sourceCell4Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.FrozeAfterEarlierUpdate,
        shouldExpectFreeze = true,
    )

    @Test
    fun test_justSource1FreezesLastScenario_observedAtTick_finalSourceUpdateTick() {
        justSource1FreezesLastScenario.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_justSource1FreezesLastScenario_observedAtTick_preFinalSourceUpdateTick() {
        justSource1FreezesLastScenario.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_justSource1FreezesLastScenario_observedAtTick_initial() {
        justSource1FreezesLastScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val justSource2FreezesLastScenario = Map4GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.FrozeAfterEarlierUpdate,
        sourceCell2Variant = SourceCellVariant.NotUpdatingNow.WarmNow.FreezingNow,
        sourceCell3Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.Const,
        sourceCell4Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.FrozeAfterEarlierUpdate,
        shouldExpectFreeze = true,
    )

    @Test
    fun test_justSource2FreezesLastScenario_observedAtTick_finalSourceUpdateTick() {
        justSource2FreezesLastScenario.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_justSource2FreezesLastScenario_observedAtTick_preFinalSourceUpdateTick() {
        justSource2FreezesLastScenario.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_justSource2FreezesLastScenario_observedAtTick_initial() {
        justSource2FreezesLastScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val justSource3FreezesLastScenario = Map4GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.FrozeAtEarlierUpdate,
        sourceCell2Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.Const,
        sourceCell3Variant = SourceCellVariant.NotUpdatingNow.WarmNow.FreezingNow,
        sourceCell4Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.FrozeAfterEarlierUpdate,
        shouldExpectFreeze = true,
    )

    @Test
    fun test_justSource3FreezesLastScenario_observedAtTick_finalSourceUpdateTick() {
        justSource3FreezesLastScenario.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_justSource3FreezesLastScenario_observedAtTick_preFinalSourceUpdateTick() {
        justSource3FreezesLastScenario.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_justSource3FreezesLastScenario_observedAtTick_initial() {
        justSource3FreezesLastScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val justSource4FreezesLastScenario = Map4GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.FrozeAtEarlierUpdate,
        sourceCell2Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.Const,
        sourceCell3Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.FrozeAfterEarlierUpdate,
        sourceCell4Variant = SourceCellVariant.NotUpdatingNow.WarmNow.FreezingNow,
        shouldExpectFreeze = true,
    )

    @Test
    fun test_justSource4FreezesLastScenario_observedAtTick_finalSourceUpdateTick() {
        justSource4FreezesLastScenario.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_justSource4FreezesLastScenario_observedAtTick_preFinalSourceUpdateTick() {
        justSource4FreezesLastScenario.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_justSource4FreezesLastScenario_observedAtTick_initial() {
        justSource4FreezesLastScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }
}
