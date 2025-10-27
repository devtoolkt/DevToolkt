package dev.toolkt.reactive.cell.map2.updating

import dev.toolkt.reactive.cell.map2.Map2GenericScenario
import dev.toolkt.reactive.cell.map2.Map2GenericScenario.SourceCellVariant
import dev.toolkt.reactive.cell.map2.Map2GenericScenario.TimelineTick
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore // FIXME
@Suppress("ClassName")
class bothSourcesUpdateSimultaneously_tests {
    private val bothSourcesUpdateSimultaneouslyScenario1 = Map2GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.FrozeAtEarlierUpdate,
        sourceCell2Variant = SourceCellVariant.UpdatingNow.FreezingNow,
        shouldExpectFreeze = false,
    )

    @Test
    fun test_twoSourcesUpdateSimultaneouslyScenario1_observedAtTick_finalSourceUpdateTick() {
        bothSourcesUpdateSimultaneouslyScenario1.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_twoSourcesUpdateSimultaneouslyScenario1_observedAtTick_preFinalSourceUpdateTick() {
        bothSourcesUpdateSimultaneouslyScenario1.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_twoSourcesUpdateSimultaneouslyScenario1_observedAtTick_initial() {
        bothSourcesUpdateSimultaneouslyScenario1.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }
}
