package dev.toolkt.reactive.cell.switch.freezing.outerUpdatesFreezing

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.test_utils.Tick
import dev.toolkt.reactive.cell.test_utils.TickAlike
import dev.toolkt.reactive.cell.test_utils.buildStatelessCellTestScenario
import dev.toolkt.reactive.cell.test_utils.defineDynamicCell
import kotlin.test.Ignore
import kotlin.test.Test

/**
 * The outer cell updates freezing and the updated inner cell is frozen / freezing.
 *
 * The result cell should freeze.
 */
@Ignore // TODO: Implement freezing
@Suppress("ClassName")
class updatedInnerDynamic_frozenOrFreezingNow_tests {
    private data class Input<ValueT>(
        val outerCell: Cell<Cell<ValueT>>,
    )

    private enum class TimelineTick : TickAlike {
        Initial, PreOuterCellFreezingUpdate, OuterCellFreezingUpdate;

        override val asTick: Tick
            get() = Tick(t = this.ordinal)
    }

    private enum class UpdatedInnerCellFreezeVariant {
        /**
         * The updated inner cell froze before the outer cell update.
         */
        FrozeBeforeSwitch,

        /**
         * The updated inner cell freezes simultaneously with the outer cell freezing update.
         */
        FreezesSimultaneously,
    }

    private fun buildScenario(
        updatedInnerCellFreezeVariant: UpdatedInnerCellFreezeVariant,
    ) = buildStatelessCellTestScenario(
        configure = {
            val earlierInnerCell = defineDynamicCell(
                initialValue = 10,
                updatedValueByTick = emptyMap(),
                freezeTick = null,
            )

            val updatedInnerCell = defineDynamicCell(
                initialValue = 20,
                updatedValueByTick = emptyMap(),
                freezeTick = when (updatedInnerCellFreezeVariant) {
                    UpdatedInnerCellFreezeVariant.FrozeBeforeSwitch -> TimelineTick.Initial
                    UpdatedInnerCellFreezeVariant.FreezesSimultaneously -> TimelineTick.OuterCellFreezingUpdate
                },
            )

            val outerCell = defineDynamicCell(
                initialValue = earlierInnerCell,
                updatedValueByTick = mapOf(
                    TimelineTick.OuterCellFreezingUpdate to updatedInnerCell,
                ),
                freezeTick = TimelineTick.OuterCellFreezingUpdate,
            )

            Input(
                outerCell = outerCell,
            )
        },
        instantiate = {
            Cell.switch(outerCell)
        },
        verificationTick = TimelineTick.OuterCellFreezingUpdate,
        expectedUpdatedValue = 20,
        shouldExpectFreeze = true,
    )

    private val updatedInnerCellFrozeBeforeSwitchScenario = buildScenario(
        updatedInnerCellFreezeVariant = UpdatedInnerCellFreezeVariant.FrozeBeforeSwitch,
    )

    @Test
    fun test_updatedInnerCellFrozeBeforeSwitchScenario_observedAtTick_outerCellFreezingUpdate() {
        updatedInnerCellFrozeBeforeSwitchScenario.testObserved(
            observationTick = TimelineTick.OuterCellFreezingUpdate,
        )
    }

    @Test
    fun test_updatedInnerCellFrozeBeforeSwitchScenario_observedAtTick_preOuterCellFreezingUpdate() {
        updatedInnerCellFrozeBeforeSwitchScenario.testObserved(
            observationTick = TimelineTick.PreOuterCellFreezingUpdate,
        )
    }

    @Test
    fun test_updatedInnerCellFrozeBeforeSwitchScenario_observedAtTick_initial() {
        updatedInnerCellFrozeBeforeSwitchScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val updatedInnerCellFreezesSimultaneouslyScenario = buildScenario(
        updatedInnerCellFreezeVariant = UpdatedInnerCellFreezeVariant.FreezesSimultaneously,
    )

    @Test
    fun test_updatedInnerCellFreezesSimultaneouslyScenario_observedAtTick_outerCellFreezingUpdate() {
        updatedInnerCellFreezesSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.OuterCellFreezingUpdate,
        )
    }

    @Test
    fun test_updatedInnerCellFreezesSimultaneouslyScenario_observedAtTick_preOuterCellFreezingUpdate() {
        updatedInnerCellFreezesSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.PreOuterCellFreezingUpdate,
        )
    }

    @Test
    fun test_updatedInnerCellFreezesSimultaneouslyScenario_observedAtTick_initial() {
        updatedInnerCellFreezesSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }
}
