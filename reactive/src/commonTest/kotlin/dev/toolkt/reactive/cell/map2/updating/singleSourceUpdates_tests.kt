package dev.toolkt.reactive.cell.map2.updating

import dev.toolkt.reactive.cell.map2.Map2GenericScenario
import dev.toolkt.reactive.cell.map2.Map2GenericScenario.SourceCellVariant
import dev.toolkt.reactive.cell.map2.Map2GenericScenario.TimelineTick
import kotlin.test.Ignore
import kotlin.test.Test

/**
 * A single source cell updates. Optionally, that source cell might freeze, but not in a way meeting the freezing
 * condition (*).
 *
 * The result cell should update. The updated value should be the result of the applying the transformation function with
 * the new values of the respective source cells.
 *
 * The result cell should not freeze.
 */
@Ignore // FIXME
@Suppress("ClassName")
class singleSourceUpdates_tests {
    private val justSource1UpdatesScenario = Map2GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.UpdatingNow.NotFreezingNow,
        sourceCell2Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.FrozeAtEarlierUpdate,
        shouldExpectFreeze = false,
    )

    @Test
    fun test_justSource1UpdatesScenario_observedAtTick_finalSourceUpdateTick() {
        justSource1UpdatesScenario.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_justSource1UpdatesScenario_observedAtTick_preFinalSourceUpdateTick() {
        justSource1UpdatesScenario.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_justSource1UpdatesScenario_observedAtTick_initial() {
        justSource1UpdatesScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val justSource1UpdatesFreezingScenario = Map2GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.UpdatingNow.FreezingNow,
        sourceCell2Variant = SourceCellVariant.NotUpdatingNow.WarmNow.NotFreezingNow,
        shouldExpectFreeze = false,
    )

    @Test
    fun test_justSource1UpdatesFreezingScenario_observedAtTick_finalSourceUpdateTick() {
        justSource1UpdatesFreezingScenario.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_justSource1UpdatesFreezingScenario_observedAtTick_preFinalSourceUpdateTick() {
        justSource1UpdatesFreezingScenario.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_justSource1UpdatesFreezingScenario_observedAtTick_initial() {
        justSource1UpdatesFreezingScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val justSource2UpdatesScenario = Map2GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.FrozeAfterEarlierUpdate,
        sourceCell2Variant = SourceCellVariant.UpdatingNow.NotFreezingNow,
        shouldExpectFreeze = false,
    )

    @Test
    fun test_justSource2UpdatesScenario_observedAtTick_finalSourceUpdateTick() {
        justSource2UpdatesScenario.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_justSource2UpdatesScenario_observedAtTick_preFinalSourceUpdateTick() {
        justSource2UpdatesScenario.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_justSource2UpdatesScenario_observedAtTick_initial() {
        justSource2UpdatesScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val justSource2UpdatesFreezingScenario = Map2GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.NotUpdatingNow.WarmNow.NotFreezingNow,
        sourceCell2Variant = SourceCellVariant.UpdatingNow.FreezingNow,
        shouldExpectFreeze = false,
    )

    @Test
    fun test_justSource2UpdatesFreezingScenario_observedAtTick_finalSourceUpdateTick() {
        justSource2UpdatesFreezingScenario.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_justSource2UpdatesFreezingScenario_observedAtTick_preFinalSourceUpdateTick() {
        justSource2UpdatesFreezingScenario.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_justSource2UpdatesFreezingScenario_observedAtTick_initial() {
        justSource2UpdatesFreezingScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }
}
