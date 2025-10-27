package dev.toolkt.reactive.cell.switch.updating.currentInnerUpdates

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.test_utils.Tick
import dev.toolkt.reactive.cell.test_utils.TickAlike
import dev.toolkt.reactive.cell.test_utils.buildStatelessCellTestScenario
import dev.toolkt.reactive.cell.test_utils.defineConstCell
import dev.toolkt.reactive.cell.test_utils.defineDynamicCell
import kotlin.test.Test

/**
 * The current inner cell updates (possibly: freezing). The outer cell is constant.
 *
 * The result cell should update with the updated value of the constant inner cell.
 * The result cell should freeze if the constant inner cell is updating freezing.
 */
@Suppress("ClassName")
class outerConst_tests {
    private data class Input<ValueT>(
        val outerCell: Cell<Cell<ValueT>>,
    )

    private enum class TimelineTick : TickAlike {
        Initial, CurrentInnerCellUpdate;

        override val asTick: Tick
            get() = Tick(t = this.ordinal)
    }

    private fun buildScenario(
        shouldCurrentInnerCellFreezeSimultaneously: Boolean,
    ) = buildStatelessCellTestScenario(
        configure = {
            val constInnerCell = defineDynamicCell(
                initialValue = 10,
                updatedValueByTick = mapOf(
                    TimelineTick.CurrentInnerCellUpdate to 11,
                ),
                freezeTick = when {
                    shouldCurrentInnerCellFreezeSimultaneously -> TimelineTick.CurrentInnerCellUpdate
                    else -> null
                },
            )

            val outerCell = defineConstCell(
                constValue = constInnerCell,
            )

            Input(
                outerCell = outerCell,
            )
        },
        instantiate = {
            Cell.switch(outerCell)
        },
        verificationTick = TimelineTick.CurrentInnerCellUpdate,
        expectedUpdatedValue = 11,
        shouldExpectFreeze = false,
    )

    private val baseScenario = buildScenario(
        shouldCurrentInnerCellFreezeSimultaneously = false,
    )

    @Test
    fun test_observedAtTick_currentInnerCellUpdate() {
        baseScenario.testObserved(
            observationTick = TimelineTick.CurrentInnerCellUpdate,
        )
    }

    @Test
    fun test_observedAtTick_initial() {
        baseScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val currentInnerCellFreezeSimultaneouslyScenario = buildScenario(
        shouldCurrentInnerCellFreezeSimultaneously = true,
    )

    @Test
    fun test_currentInnerCellFreezeSimultaneously_observedAtTick_currentInnerCellUpdate() {
        currentInnerCellFreezeSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.CurrentInnerCellUpdate,
        )
    }

    @Test
    fun test_currentInnerCellFreezeSimultaneously_observedAtTick_initial() {
        currentInnerCellFreezeSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }
}
