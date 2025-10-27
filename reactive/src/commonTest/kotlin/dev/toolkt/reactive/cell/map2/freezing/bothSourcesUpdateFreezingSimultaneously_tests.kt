package dev.toolkt.reactive.cell.map2.freezing

import dev.toolkt.reactive.cell.map2.Map2GenericScenario
import dev.toolkt.reactive.cell.map2.Map2GenericScenario.SourceCellVariant
import dev.toolkt.reactive.cell.map2.Map2GenericScenario.TimelineTick
import kotlin.test.Ignore
import kotlin.test.Test

/**
 * Both source cells update freezing.
 *
 * The result cell should update.
 *
 * The result cell should freeze.
 */
@Ignore // FIXME
@Suppress("ClassName")
class bothSourcesUpdateFreezingSimultaneously_tests {
    private val bothSourcesUpdateFreezingSimultaneouslyScenario = Map2GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.UpdatingNow.FreezingNow,
        sourceCell2Variant = SourceCellVariant.UpdatingNow.FreezingNow,
        shouldExpectFreeze = true,
    )

    @Test
    fun test_threeSourcesFreezeSimultaneouslyLastScenario2_observedAtTick_finalSourceUpdateTick() {
        bothSourcesUpdateFreezingSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_threeSourcesFreezeSimultaneouslyLastScenario2_observedAtTick_preFinalSourceUpdateTick() {
        bothSourcesUpdateFreezingSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_threeSourcesFreezeSimultaneouslyLastScenario2_observedAtTick_initial() {
        bothSourcesUpdateFreezingSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }
}
