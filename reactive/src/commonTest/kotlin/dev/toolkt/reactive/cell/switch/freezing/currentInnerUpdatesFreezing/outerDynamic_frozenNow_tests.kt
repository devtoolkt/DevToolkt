package dev.toolkt.reactive.cell.switch.freezing.currentInnerUpdatesFreezing

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.test_utils.Tick
import dev.toolkt.reactive.cell.test_utils.TickAlike
import dev.toolkt.reactive.cell.test_utils.buildStatelessCellTestScenario
import dev.toolkt.reactive.cell.test_utils.defineDynamicCell
import kotlin.test.Ignore
import kotlin.test.Test

/**
 * The current inner cell updates freezing while the outer cell is frozen.
 *
 * The result cell should update.
 *
 * The result cell should freeze.
 */
@Ignore // TODO: Implement freezing
@Suppress("ClassName")
class outerDynamic_frozenNow_tests {
    private data class Input<ValueT>(
        val outerCell: Cell<Cell<ValueT>>,
    )

    private enum class TimelineTick : TickAlike {
        Initial, OuterCellUpdate, PostOuterCellUpdate, PreCurrentInnerCellFreeze, CurrentInnerCellFreeze;

        override val asTick: Tick
            get() = Tick(t = this.ordinal)
    }

    private enum class OuterCellFreezeVariant {
        /**
         * The outer cell freezes simultaneously with the update to the current inner cell.
         */
        FrozeAtSwitch,

        /**
         * The outer cell froze after the update, but before the current inner cell freeze.
         */
        FrozeAfterSwitch,
    }

    private fun buildScenario(
        outerCellFreezeVariant: OuterCellFreezeVariant,
    ) = buildStatelessCellTestScenario(
        configure = {
            val earlierInnerCell = defineDynamicCell(
                initialValue = 10,
                updatedValueByTick = emptyMap(),
                freezeTick = null,
            )

            val currentInnerCell = defineDynamicCell(
                initialValue = 20,
                updatedValueByTick = mapOf(
                    TimelineTick.CurrentInnerCellFreeze to 21,
                ),
                freezeTick = TimelineTick.CurrentInnerCellFreeze,
            )

            val outerCell = defineDynamicCell(
                initialValue = earlierInnerCell,
                updatedValueByTick = mapOf(
                    TimelineTick.OuterCellUpdate to currentInnerCell,
                ),
                freezeTick = when (outerCellFreezeVariant) {
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
        verificationTick = TimelineTick.CurrentInnerCellFreeze,
        expectedUpdatedValue = 21,
        shouldExpectFreeze = true,
    )

    private val outerCellFrozeAtSwitchScenario = buildScenario(
        outerCellFreezeVariant = OuterCellFreezeVariant.FrozeAtSwitch,
    )

    @Test
    fun test_outerCellFrozeAtSwitchScenario_observedAtTick_currentInnerCellFreeze() {
        outerCellFrozeAtSwitchScenario.testObserved(
            observationTick = TimelineTick.CurrentInnerCellFreeze,
        )
    }

    @Test
    fun test_outerCellFrozeAtSwitchScenario_observedAtTick_preCurrentInnerCellFreeze() {
        outerCellFrozeAtSwitchScenario.testObserved(
            observationTick = TimelineTick.PreCurrentInnerCellFreeze,
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
    )

    @Test
    fun test_outerCellFrozeAfterSwitchScenario_observedAtTick_currentInnerCellFreeze() {
        outerCellFrozeAfterSwitchScenario.testObserved(
            observationTick = TimelineTick.CurrentInnerCellFreeze,
        )
    }

    @Test
    fun test_outerCellFrozeAfterSwitchScenario_observedAtTick_preCurrentInnerCellFreeze() {
        outerCellFrozeAfterSwitchScenario.testObserved(
            observationTick = TimelineTick.PreCurrentInnerCellFreeze,
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
}
