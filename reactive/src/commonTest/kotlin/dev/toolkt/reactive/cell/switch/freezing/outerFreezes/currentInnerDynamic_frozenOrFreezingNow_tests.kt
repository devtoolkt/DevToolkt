package dev.toolkt.reactive.cell.switch.freezing.outerFreezes

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.test_utils.Tick
import dev.toolkt.reactive.cell.test_utils.TickAlike
import dev.toolkt.reactive.cell.test_utils.buildStatelessCellTestScenario
import dev.toolkt.reactive.cell.test_utils.defineDynamicCell
import kotlin.test.Ignore
import kotlin.test.Test

/**
 * Outer cell freezes (without updating) while the current cell is frozen / freezing.
 *
 * The result cell should freeze.
 */
@Ignore // TODO: Implement freezing
@Suppress("ClassName")
class currentInnerDynamic_frozenOrFreezingNow_tests {
    private data class Input<ValueT>(
        val outerCell: Cell<Cell<ValueT>>,
    )

    private enum class CurrentInnerCellFreezeVariant {
        /**
         * The current inner cell froze before the outer cell update.
         */
        FrozeBeforeSwitch,

        /**
         * The current inner cell froze simultaneously with the outer cell update.
         */
        FrozeAtSwitch,

        /**
         * The current inner cell froze after the outer cell update.
         */
        FrozeAfterSwitch,

        /**
         * The current inner cell freezes simultaneously with the outer cell freezing.
         */
        FreezesSimultaneously,
    }

    private enum class TimelineTick : TickAlike {
        Initial, OuterCellUpdate, PostOuterCellUpdate, PreOuterCellFreeze, OuterCellFreeze;

        override val asTick: Tick
            get() = Tick(t = this.ordinal)
    }

    private fun buildScenario(
        currentInnerCellFreezeVariant: CurrentInnerCellFreezeVariant,
    ) = buildStatelessCellTestScenario(
        configure = {
            val earlierInnerCell = defineDynamicCell(
                initialValue = 10,
                updatedValueByTick = emptyMap(),
                freezeTick = null,
            )

            val currentInnerCell = defineDynamicCell(
                initialValue = 20,
                updatedValueByTick = emptyMap(),
                freezeTick = when (currentInnerCellFreezeVariant) {
                    CurrentInnerCellFreezeVariant.FrozeBeforeSwitch -> TimelineTick.Initial
                    CurrentInnerCellFreezeVariant.FrozeAtSwitch -> TimelineTick.OuterCellUpdate
                    CurrentInnerCellFreezeVariant.FrozeAfterSwitch -> TimelineTick.PostOuterCellUpdate
                    CurrentInnerCellFreezeVariant.FreezesSimultaneously -> TimelineTick.OuterCellFreeze
                },
            )

            val outerCell = defineDynamicCell(
                initialValue = earlierInnerCell,
                updatedValueByTick = mapOf(
                    TimelineTick.OuterCellUpdate to currentInnerCell,
                ),
                freezeTick = TimelineTick.OuterCellFreeze,
            )

            Input(
                outerCell = outerCell,
            )
        },
        instantiate = {
            Cell.switch(outerCell)
        },
        verificationTick = TimelineTick.OuterCellFreeze,
        expectedUpdatedValue = null,
        shouldExpectFreeze = true,
    )

    private val currentInnerCellFrozeBeforeSwitchScenario = buildScenario(
        currentInnerCellFreezeVariant = CurrentInnerCellFreezeVariant.FrozeBeforeSwitch,
    )

    @Test
    fun test_currentInnerCellFrozeBeforeSwitchScenario_observedAtTick_outerCellFreeze() {
        currentInnerCellFrozeBeforeSwitchScenario.testObserved(
            observationTick = TimelineTick.OuterCellFreeze,
        )
    }

    @Test
    fun test_currentInnerCellFrozeBeforeSwitchScenario_observedAtTick_preOuterCellFreeze() {
        currentInnerCellFrozeBeforeSwitchScenario.testObserved(
            observationTick = TimelineTick.PreOuterCellFreeze,
        )
    }

    @Test
    fun test_currentInnerCellFrozeBeforeSwitchScenario_observedAtTick_outerCellUpdate() {
        currentInnerCellFrozeBeforeSwitchScenario.testObserved(
            observationTick = TimelineTick.OuterCellUpdate,
        )
    }

    @Test
    fun test_currentInnerCellFrozeBeforeSwitchScenario_observedAtTick_initial() {
        currentInnerCellFrozeBeforeSwitchScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val currentInnerCellFrozeAtSwitchScenario = buildScenario(
        currentInnerCellFreezeVariant = CurrentInnerCellFreezeVariant.FrozeAtSwitch,
    )

    @Test
    fun test_currentInnerCellFrozeAtSwitchScenario_observedAtTick_outerCellFreeze() {
        currentInnerCellFrozeAtSwitchScenario.testObserved(
            observationTick = TimelineTick.OuterCellFreeze,
        )
    }

    @Test
    fun test_currentInnerCellFrozeAtSwitchScenario_observedAtTick_preOuterCellFreeze() {
        currentInnerCellFrozeAtSwitchScenario.testObserved(
            observationTick = TimelineTick.PreOuterCellFreeze,
        )
    }

    @Test
    fun test_currentInnerCellFrozeAtSwitchScenario_observedAtTick_outerCellUpdate() {
        currentInnerCellFrozeAtSwitchScenario.testObserved(
            observationTick = TimelineTick.OuterCellUpdate,
        )
    }

    @Test
    fun test_currentInnerCellFrozeAtSwitchScenario_observedAtTick_initial() {
        currentInnerCellFrozeAtSwitchScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val currentInnerCellFrozeAfterSwitchScenario = buildScenario(
        currentInnerCellFreezeVariant = CurrentInnerCellFreezeVariant.FrozeAfterSwitch,
    )

    @Test
    fun test_currentInnerCellFrozeAfterSwitchScenario_observedAtTick_outerCellFreeze() {
        currentInnerCellFrozeAfterSwitchScenario.testObserved(
            observationTick = TimelineTick.OuterCellFreeze,
        )
    }

    @Test
    fun test_currentInnerCellFrozeAfterSwitchScenario_observedAtTick_preOuterCellFreeze() {
        currentInnerCellFrozeAfterSwitchScenario.testObserved(
            observationTick = TimelineTick.PreOuterCellFreeze,
        )
    }

    @Test
    fun test_currentInnerCellFrozeAfterSwitchScenario_observedAtTick_postOuterCellUpdate() {
        currentInnerCellFrozeAfterSwitchScenario.testObserved(
            observationTick = TimelineTick.PostOuterCellUpdate,
        )
    }

    @Test
    fun test_currentInnerCellFrozeAfterSwitchScenario_observedAtTick_outerCellUpdate() {
        currentInnerCellFrozeAfterSwitchScenario.testObserved(
            observationTick = TimelineTick.OuterCellUpdate,
        )
    }

    @Test
    fun test_currentInnerCellFrozeAfterSwitchScenario_observedAtTick_initial() {
        currentInnerCellFrozeAfterSwitchScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val currentInnerCellFreezesSimultaneouslyScenario = buildScenario(
        currentInnerCellFreezeVariant = CurrentInnerCellFreezeVariant.FrozeAfterSwitch,
    )

    @Test
    fun test_currentInnerCellFreezesSimultaneouslyScenario_observedAtTick_outerCellFreeze() {
        currentInnerCellFreezesSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.OuterCellFreeze,
        )
    }

    @Test
    fun test_currentInnerCellFreezesSimultaneouslyScenario_observedAtTick_preOuterCellFreeze() {
        currentInnerCellFreezesSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.PreOuterCellFreeze,
        )
    }

    @Test
    fun test_currentInnerCellFreezesSimultaneouslyScenario_observedAtTick_outerCellUpdate() {
        currentInnerCellFreezesSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.OuterCellUpdate,
        )
    }

    @Test
    fun test_currentInnerCellFreezesSimultaneouslyScenario_observedAtTick_initial() {
        currentInnerCellFreezesSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }
}
