package dev.toolkt.reactive.cell.map4.updating

import dev.toolkt.reactive.cell.map4.Map4GenericScenario
import dev.toolkt.reactive.cell.map4.Map4GenericScenario.SourceCellVariant
import dev.toolkt.reactive.cell.map4.Map4GenericScenario.TimelineTick
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
    private val justSource1UpdatesScenario = Map4GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.UpdatingNow.NotFreezingNow,
        sourceCell2Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.FrozeAtEarlierUpdate,
        sourceCell3Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.Const,
        sourceCell4Variant = SourceCellVariant.NotUpdatingNow.WarmNow.NotFreezingNow,
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

    private val justSource1UpdatesFreezingScenario = Map4GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.UpdatingNow.FreezingNow,
        sourceCell2Variant = SourceCellVariant.NotUpdatingNow.WarmNow.NotFreezingNow,
        sourceCell3Variant = SourceCellVariant.NotUpdatingNow.WarmNow.FreezingNow,        sourceCell4Variant = SourceCellVariant.NotUpdatingNow.WarmNow.NotFreezingNow,
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

    private val justSource2UpdatesScenario = Map4GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.FrozeAfterEarlierUpdate,
        sourceCell2Variant = SourceCellVariant.UpdatingNow.NotFreezingNow,
        sourceCell3Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.Const,
        sourceCell4Variant = SourceCellVariant.NotUpdatingNow.WarmNow.NotFreezingNow,
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

    private val justSource2UpdatesFreezingScenario = Map4GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.NotUpdatingNow.WarmNow.NotFreezingNow,
        sourceCell2Variant = SourceCellVariant.UpdatingNow.FreezingNow,
        sourceCell3Variant = SourceCellVariant.NotUpdatingNow.WarmNow.NotFreezingNow,
        sourceCell4Variant = SourceCellVariant.NotUpdatingNow.WarmNow.NotFreezingNow,
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

    private val justSource3UpdatesScenario = Map4GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.FrozeAfterEarlierUpdate,
        sourceCell2Variant = SourceCellVariant.NotUpdatingNow.FrozenNow.Const,
        sourceCell3Variant = SourceCellVariant.UpdatingNow.NotFreezingNow,
        sourceCell4Variant = SourceCellVariant.NotUpdatingNow.WarmNow.NotFreezingNow,
        shouldExpectFreeze = false,
    )

    @Test
    fun test_justSource3UpdatesScenario_observedAtTick_finalSourceUpdateTick() {
        justSource3UpdatesScenario.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_justSource3UpdatesScenario_observedAtTick_preFinalSourceUpdateTick() {
        justSource3UpdatesScenario.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_justSource3UpdatesScenario_observedAtTick_initial() {
        justSource3UpdatesScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val justSource3UpdatesFreezingScenario = Map4GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.NotUpdatingNow.WarmNow.NotFreezingNow,
        sourceCell2Variant = SourceCellVariant.NotUpdatingNow.WarmNow.NotFreezingNow,
        sourceCell3Variant = SourceCellVariant.UpdatingNow.FreezingNow,
        sourceCell4Variant = SourceCellVariant.NotUpdatingNow.WarmNow.NotFreezingNow,
        shouldExpectFreeze = false,
    )

    @Test
    fun test_justSource3UpdatesFreezingScenario_observedAtTick_finalSourceUpdateTick() {
        justSource3UpdatesFreezingScenario.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_justSource3UpdatesFreezingScenario_observedAtTick_preFinalSourceUpdateTick() {
        justSource3UpdatesFreezingScenario.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_justSource3UpdatesFreezingScenario_observedAtTick_initial() {
        justSource3UpdatesFreezingScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val justSource4UpdatesFreezingScenario = Map4GenericScenario.build(
        sourceCell1Variant = SourceCellVariant.NotUpdatingNow.WarmNow.NotFreezingNow,
        sourceCell2Variant = SourceCellVariant.NotUpdatingNow.WarmNow.NotFreezingNow,
        sourceCell3Variant = SourceCellVariant.NotUpdatingNow.WarmNow.NotFreezingNow,
        sourceCell4Variant = SourceCellVariant.UpdatingNow.FreezingNow,
        shouldExpectFreeze = false,
    )

    @Test
    fun test_justSource4UpdatesFreezingScenario_observedAtTick_finalSourceUpdateTick() {
        justSource4UpdatesFreezingScenario.testObserved(
            observationTick = TimelineTick.FinalSourceUpdateTick,
        )
    }

    @Test
    fun test_justSource4UpdatesFreezingScenario_observedAtTick_preFinalSourceUpdateTick() {
        justSource4UpdatesFreezingScenario.testObserved(
            observationTick = TimelineTick.PreFinalSourceUpdateTick,
        )
    }

    @Test
    fun test_justSource4UpdatesFreezingScenario_observedAtTick_initial() {
        justSource4UpdatesFreezingScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }
}
