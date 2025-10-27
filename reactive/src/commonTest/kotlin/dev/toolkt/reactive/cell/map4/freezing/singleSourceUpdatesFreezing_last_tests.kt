package dev.toolkt.reactive.cell.map4.freezing

import dev.toolkt.reactive.cell.map4.Map4GenericScenario
import dev.toolkt.reactive.cell.map4.Map4GenericScenario.SourceCellVariant
import dev.toolkt.reactive.cell.map4.Map4GenericScenario.TimelineTick
import kotlin.test.Ignore
import kotlin.test.Test

/**
 * The last warm source cells updates freezing.
 *
 * The result cell should update.
 *
 * The result cell should freeze.
 */
@Ignore // FIXME
@Suppress("ClassName")
class singleSourceUpdatesFreezing_last_tests {
    private val justSource1UpdatesFreezingLastScenario = Map4GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.UpdatingNow.FreezingNow,
        sourceCell2Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.FrozeAtEarlierUpdate,
        sourceCell3Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.Const,
        sourceCell4Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.FrozeAfterEarlierUpdate,
        shouldExpectFreeze = true,
    )

    @Test
    fun test_justSource1UpdatesFreezingLastScenario_observedAtTick_finalSourceUpdateTick() {
        justSource1UpdatesFreezingLastScenario.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_justSource1UpdatesFreezingLastScenario_observedAtTick_preFinalSourceUpdateTick() {
        justSource1UpdatesFreezingLastScenario.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_justSource1UpdatesFreezingLastScenario_observedAtTick_initial() {
        justSource1UpdatesFreezingLastScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val justSource2UpdatesFreezingLastScenario = Map4GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.FrozeAfterEarlierUpdate,
        sourceCell2Variant = SourceCellVariant.UpdatingNow.FreezingNow,
        sourceCell3Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.Const,
        sourceCell4Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.FrozeAfterEarlierUpdate,
        shouldExpectFreeze = true,
    )

    @Test
    fun test_justSource2UpdatesFreezingLastScenario_observedAtTick_finalSourceUpdateTick() {
        justSource2UpdatesFreezingLastScenario.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_justSource2UpdatesFreezingLastScenario_observedAtTick_preFinalSourceUpdateTick() {
        justSource2UpdatesFreezingLastScenario.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_justSource2UpdatesFreezingLastScenario_observedAtTick_initial() {
        justSource2UpdatesFreezingLastScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val justSource3UpdatesFreezingLastScenario = Map4GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.FrozeAtEarlierUpdate,
        sourceCell2Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.Const,
        sourceCell3Variant = SourceCellVariant.UpdatingNow.FreezingNow,
        sourceCell4Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.FrozeAfterEarlierUpdate,
        shouldExpectFreeze = true,
    )

    @Test
    fun test_justSource3UpdatesFreezingLastScenario_observedAtTick_finalSourceUpdateTick() {
        justSource3UpdatesFreezingLastScenario.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_justSource3UpdatesFreezingLastScenario_observedAtTick_preFinalSourceUpdateTick() {
        justSource3UpdatesFreezingLastScenario.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_justSource3UpdatesFreezingLastScenario_observedAtTick_initial() {
        justSource3UpdatesFreezingLastScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val justSource4UpdatesFreezingLastScenario = Map4GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.FrozeAtEarlierUpdate,
        sourceCell2Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.Const,
        sourceCell3Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.FrozeAfterEarlierUpdate,
        sourceCell4Variant = SourceCellVariant.UpdatingNow.FreezingNow,
        shouldExpectFreeze = true,
    )

    @Test
    fun test_justSource4UpdatesFreezingLastScenario_observedAtTick_finalSourceUpdateTick() {
        justSource4UpdatesFreezingLastScenario.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_justSource4UpdatesFreezingLastScenario_observedAtTick_preFinalSourceUpdateTick() {
        justSource4UpdatesFreezingLastScenario.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_justSource4UpdatesFreezingLastScenario_observedAtTick_initial() {
        justSource4UpdatesFreezingLastScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }
}
