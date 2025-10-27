package dev.toolkt.reactive.cell.map2.notFreezing

import dev.toolkt.reactive.cell.map2.Map2GenericScenario
import dev.toolkt.reactive.cell.map2.Map2GenericScenario.SourceCellVariant
import dev.toolkt.reactive.cell.map2.Map2GenericScenario.TimelineTick
import kotlin.test.Ignore
import kotlin.test.Test

/**
 * A single source cells freezes, leaving some of the other source cells warm. No source cell updates.
 *
 * The result cell should not update.
 *
 * The result cell should not freeze.
 */
@Ignore // FIXME
@Suppress("ClassName")
class singleSourceFreezes_notLast_tests {
    private val justSource1FreezesNotLastScenario = Map2GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.NotUpdatingNow.WarmNow.FreezingNow,
        sourceCell2Variant = SourceCellVariant.NotUpdatingNow.WarmNow.NotFreezingNow,
        shouldExpectFreeze = false,
    )

    @Test
    fun test_justSource1FreezesNotLastScenario_observedAtTick_finalSourceUpdateTick() {
        justSource1FreezesNotLastScenario.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_justSource1FreezesNotLastScenario_observedAtTick_preFinalSourceUpdateTick() {
        justSource1FreezesNotLastScenario.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_justSource1FreezesNotLastScenario_observedAtTick_initial() {
        justSource1FreezesNotLastScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val justSource2FreezesNotLastScenario = Map2GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.NotUpdatingNow.WarmNow.NotFreezingNow,
        sourceCell2Variant = SourceCellVariant.NotUpdatingNow.WarmNow.FreezingNow,
        shouldExpectFreeze = false,
    )

    @Test
    fun test_justSource2FreezesNotLastScenario_observedAtTick_finalSourceUpdateTick() {
        justSource2FreezesNotLastScenario.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_justSource2FreezesNotLastScenario_observedAtTick_preFinalSourceUpdateTick() {
        justSource2FreezesNotLastScenario.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_justSource2FreezesNotLastScenario_observedAtTick_initial() {
        justSource2FreezesNotLastScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }
}
