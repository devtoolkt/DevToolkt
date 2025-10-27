package dev.toolkt.reactive.cell.switch.updating.currentInnerUpdates

import dev.toolkt.core.utils.iterable.mapOfNotNull
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.test_utils.Tick
import dev.toolkt.reactive.cell.test_utils.TickAlike
import dev.toolkt.reactive.cell.test_utils.buildStatelessCellTestScenario
import dev.toolkt.reactive.cell.test_utils.defineDynamicCell
import kotlin.test.Test

/**
 * The current inner cell updates (possibly: freezing). The outer cell is dynamic (possibly: frozen), not updating simultaneously.
 *
 * The result cell should update with the updated value of the current inner cell.
 * The result cell should freeze iff the outer cell is frozen and the current inner cell updates freezing.
 */
@Suppress("ClassName")
class outerDynamic_tests {
    private data class Input<ValueT>(
        val outerCell: Cell<Cell<ValueT>>,
    )

    private enum class TimelineTick : TickAlike {
        Initial, OuterCellUpdate, PostOuterCellUpdate, CurrentInnerCellEarlierUpdate, PreCurrentInnerCellUpdate, CurrentInnerCellUpdate;

        override val asTick: Tick
            get() = Tick(t = this.ordinal)
    }

    // TODO: Add a case when the outer cell freezes simultaneously?
    private enum class OuterCellFreezeVariant {
        /**
         * The outer cell is dynamic at the moment of the current inner cell update.
         */
        None,

        /**
         * The outer cell froze during it last update.
         */
        FrozeAtSwitch,

        /**
         * The outer cell froze after the update, but before the current inner cell update.
         */
        FrozeAfterSwitch,
    }

    private fun buildScenario(
        outerCellFreezeVariant: OuterCellFreezeVariant,
        shouldCurrentInnerCellUpdateEarlier: Boolean,
        shouldCurrentInnerCellFreezeSimultaneously: Boolean,
    ) = buildStatelessCellTestScenario(
        configure = {
            val earlierInnerCell = defineDynamicCell(
                initialValue = 10,
                updatedValueByTick = emptyMap(),
                freezeTick = null,
            )

            val currentInnerCell = defineDynamicCell(
                initialValue = 20,
                updatedValueByTick = mapOfNotNull(
                    (TimelineTick.CurrentInnerCellEarlierUpdate to 21).takeIf { shouldCurrentInnerCellUpdateEarlier },
                    TimelineTick.CurrentInnerCellUpdate to 22,
                ),
                freezeTick = when {
                    shouldCurrentInnerCellFreezeSimultaneously -> TimelineTick.CurrentInnerCellUpdate
                    else -> null
                },
            )

            val outerCell = defineDynamicCell(
                initialValue = earlierInnerCell,
                updatedValueByTick = mapOf(
                    TimelineTick.OuterCellUpdate to currentInnerCell,
                ),
                freezeTick = when (outerCellFreezeVariant) {
                    OuterCellFreezeVariant.None -> null
                    OuterCellFreezeVariant.FrozeAtSwitch -> TimelineTick.OuterCellUpdate
                    OuterCellFreezeVariant.FrozeAfterSwitch -> TimelineTick.PostOuterCellUpdate
                },
            )

            Input(
                outerCell = outerCell,
            )
        },
        instantiate = {
            Cell.switch(outerCell)
        },
        verificationTick = TimelineTick.CurrentInnerCellUpdate,
        expectedUpdatedValue = 22,
        shouldExpectFreeze = false,
    )

    private val baseScenario = buildScenario(
        outerCellFreezeVariant = OuterCellFreezeVariant.None,
        shouldCurrentInnerCellUpdateEarlier = false,
        shouldCurrentInnerCellFreezeSimultaneously = false,
    )

    @Test
    fun test_baseScenario_observedAtTick_currentInnerCellUpdate() {
        baseScenario.testObserved(
            observationTick = TimelineTick.CurrentInnerCellUpdate,
        )
    }

    @Test
    fun test_baseScenario_observedAtTick_preCurrentInnerCellUpdate() {
        baseScenario.testObserved(
            observationTick = TimelineTick.PreCurrentInnerCellUpdate,
        )
    }

    @Test
    fun test_baseScenario_observedAtTick_outerCellUpdate() {
        baseScenario.testObserved(
            observationTick = TimelineTick.OuterCellUpdate,
        )
    }

    @Test
    fun test_baseScenario_observedAtTick_initial() {
        baseScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val subsequentUpdateScenario = buildScenario(
        outerCellFreezeVariant = OuterCellFreezeVariant.None,
        shouldCurrentInnerCellUpdateEarlier = true,
        shouldCurrentInnerCellFreezeSimultaneously = false,
    )

    @Test
    fun test_subsequentUpdateScenario_observedAtTick_currentInnerCellEarlierUpdate() {
        subsequentUpdateScenario.testObserved(
            observationTick = TimelineTick.CurrentInnerCellEarlierUpdate,
        )
    }

    @Test
    fun test_subsequentUpdateScenario_observedAtTick_postOuterCellUpdate() {
        subsequentUpdateScenario.testObserved(
            observationTick = TimelineTick.PostOuterCellUpdate,
        )
    }

    @Test
    fun test_subsequentUpdateScenario_observedAtTick_outerCellUpdate() {
        subsequentUpdateScenario.testObserved(
            observationTick = TimelineTick.OuterCellUpdate,
        )
    }

    @Test
    fun test_subsequentUpdateScenario_observedAtTick_initial() {
        baseScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val outerCellFrozeAtSwitchScenario = buildScenario(
        outerCellFreezeVariant = OuterCellFreezeVariant.FrozeAtSwitch,
        shouldCurrentInnerCellUpdateEarlier = false,
        shouldCurrentInnerCellFreezeSimultaneously = false,
    )

    @Test
    fun test_outerCellFrozeAtSwitchScenario_observedAtTick_currentInnerCellUpdate() {
        outerCellFrozeAtSwitchScenario.testObserved(
            observationTick = TimelineTick.CurrentInnerCellUpdate,
        )
    }

    @Test
    fun test_outerCellFrozeAtSwitchScenario_observedAtTick_preCurrentInnerCellUpdate() {
        outerCellFrozeAtSwitchScenario.testObserved(
            observationTick = TimelineTick.PreCurrentInnerCellUpdate,
        )
    }

    @Test
    fun test_outerCellFrozeAtSwitchScenario_observedAtTick_outerCellUpdate() {
        outerCellFrozeAtSwitchScenario.testObserved(
            observationTick = TimelineTick.OuterCellUpdate,
        )
    }

    @Test
    fun test_outerCellFrozeAtSwitchScenario_observedAtTick_initial() {
        outerCellFrozeAtSwitchScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val outerCellFrozeAfterSwitchScenario = buildScenario(
        outerCellFreezeVariant = OuterCellFreezeVariant.FrozeAfterSwitch,
        shouldCurrentInnerCellUpdateEarlier = false,
        shouldCurrentInnerCellFreezeSimultaneously = false,
    )

    @Test
    fun test_outerCellFrozeAfterSwitchScenario_observedAtTick_currentInnerCellUpdate() {
        outerCellFrozeAfterSwitchScenario.testObserved(
            observationTick = TimelineTick.CurrentInnerCellUpdate,
        )
    }

    @Test
    fun test_outerCellFrozeAfterSwitchScenario_observedAtTick_preCurrentInnerCellUpdate() {
        outerCellFrozeAfterSwitchScenario.testObserved(
            observationTick = TimelineTick.PreCurrentInnerCellUpdate,
        )
    }

    @Test
    fun test_outerCellFrozeAfterSwitchScenario_observedAtTick_outerCellUpdate() {
        outerCellFrozeAfterSwitchScenario.testObserved(
            observationTick = TimelineTick.OuterCellUpdate,
        )
    }

    @Test
    fun test_outerCellFrozeAfterSwitchScenario_observedAtTick_initial() {
        outerCellFrozeAfterSwitchScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val outerCellFrozeAtSwitchCurrentInnerCellFreezesSimultaneouslyScenario = buildScenario(
        outerCellFreezeVariant = OuterCellFreezeVariant.FrozeAtSwitch,
        shouldCurrentInnerCellUpdateEarlier = false,
        shouldCurrentInnerCellFreezeSimultaneously = true,
    )

    @Test
    fun test_outerCellFrozeAtSwitchCurrentInnerCellFreezesSimultaneouslyScenario_observedAtTick_currentInnerCellUpdate() {
        outerCellFrozeAtSwitchCurrentInnerCellFreezesSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.CurrentInnerCellUpdate,
        )
    }

    @Test
    fun test_outerCellFrozeAtSwitchCurrentInnerCellFreezesSimultaneouslyScenario_observedAtTick_preCurrentInnerCellUpdate() {
        outerCellFrozeAtSwitchCurrentInnerCellFreezesSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.PreCurrentInnerCellUpdate,
        )
    }

    @Test
    fun test_outerCellFrozeAtSwitchCurrentInnerCellFreezesSimultaneouslyScenario_observedAtTick_outerCellUpdate() {
        outerCellFrozeAtSwitchCurrentInnerCellFreezesSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.OuterCellUpdate,
        )
    }

    @Test
    fun test_outerCellFrozeAtSwitchCurrentInnerCellFreezesSimultaneouslyScenario_observedAtTick_initial() {
        outerCellFrozeAtSwitchCurrentInnerCellFreezesSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val outerCellFrozeAfterSwitchCurrentInnerCellFreezesSimultaneouslyScenario = buildScenario(
        outerCellFreezeVariant = OuterCellFreezeVariant.FrozeAfterSwitch,
        shouldCurrentInnerCellUpdateEarlier = false,
        shouldCurrentInnerCellFreezeSimultaneously = true,
    )

    @Test
    fun test_outerCellFrozeAfterSwitchCurrentInnerCellFreezesSimultaneouslyScenario_observedAtTick_currentInnerCellUpdate() {
        outerCellFrozeAfterSwitchCurrentInnerCellFreezesSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.CurrentInnerCellUpdate,
        )
    }

    @Test
    fun test_outerCellFrozeAfterSwitchCurrentInnerCellFreezesSimultaneouslyScenario_observedAtTick_preCurrentInnerCellUpdate() {
        outerCellFrozeAfterSwitchCurrentInnerCellFreezesSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.PreCurrentInnerCellUpdate,
        )
    }

    @Test
    fun test_outerCellFrozeAfterSwitchCurrentInnerCellFreezesSimultaneouslyScenario_observedAtTick_outerCellUpdate() {
        outerCellFrozeAfterSwitchCurrentInnerCellFreezesSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.OuterCellUpdate,
        )
    }

    @Test
    fun test_outerCellFrozeAfterSwitchCurrentInnerCellFreezesSimultaneouslyScenario_observedAtTick_initial() {
        outerCellFrozeAfterSwitchCurrentInnerCellFreezesSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }
}
