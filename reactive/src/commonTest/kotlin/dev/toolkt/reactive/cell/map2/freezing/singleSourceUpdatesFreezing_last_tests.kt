package dev.toolkt.reactive.cell.map2.freezing

import dev.toolkt.reactive.cell.map2.Map2GenericScenario
import dev.toolkt.reactive.cell.map2.Map2GenericScenario.SourceCellVariant
import dev.toolkt.reactive.cell.map2.Map2GenericScenario.TimelineTick
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
    private val justSource1UpdatesFreezingLastScenario = Map2GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.UpdatingNow.FreezingNow,
        sourceCell2Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.FrozeAtEarlierUpdate,
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

    private val justSource2UpdatesFreezingLastScenario = Map2GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.FrozeAfterEarlierUpdate,
        sourceCell2Variant = SourceCellVariant.UpdatingNow.FreezingNow,
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
}
