package dev.toolkt.reactive.cell.map4.freezing

import dev.toolkt.reactive.cell.map4.Map4GenericScenario
import dev.toolkt.reactive.cell.map4.Map4GenericScenario.SourceCellVariant
import dev.toolkt.reactive.cell.map4.Map4GenericScenario.TimelineTick
import kotlin.test.Ignore
import kotlin.test.Test

/**
 * All source cells update freezing.
 *
 * The result cell should update.
 *
 * The result cell should freeze.
 */
@Ignore // FIXME
@Suppress("ClassName")
class allSourcesUpdateFreezingSimultaneously_tests {
    private val allSourcesUpdateFreezingSimultaneouslyScenario = Map4GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.UpdatingNow.FreezingNow,
        sourceCell2Variant = SourceCellVariant.UpdatingNow.FreezingNow,
        sourceCell3Variant = SourceCellVariant.UpdatingNow.FreezingNow,
        sourceCell4Variant = SourceCellVariant.UpdatingNow.FreezingNow,
        shouldExpectFreeze = true,
    )

    @Test
    fun test_threeSourcesFreezeSimultaneouslyLastScenario2_observedAtTick_finalSourceUpdateTick() {
        allSourcesUpdateFreezingSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_threeSourcesFreezeSimultaneouslyLastScenario2_observedAtTick_preFinalSourceUpdateTick() {
        allSourcesUpdateFreezingSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_threeSourcesFreezeSimultaneouslyLastScenario2_observedAtTick_initial() {
        allSourcesUpdateFreezingSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }
}
