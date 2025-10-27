package dev.toolkt.reactive.cell.switch.updating.outerUpdates

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.test_utils.Tick
import dev.toolkt.reactive.cell.test_utils.TickAlike
import dev.toolkt.reactive.cell.test_utils.buildStatelessCellTestScenario
import dev.toolkt.reactive.cell.test_utils.defineConstCell
import dev.toolkt.reactive.cell.test_utils.defineDynamicCell
import kotlin.test.Test

/**
 * The outer cell updates (possibly: freezing). The updated inner cell is constant.
 *
 * The result cell should update with the constant value of the updated inner cell.
 *
 * The result cell should not freeze.
 */
@Suppress("ClassName")
class updatedInnerConst_tests {
    private data class Input<ValueT>(
        val outerCell: Cell<Cell<ValueT>>,
    )

    private enum class TimelineTick : TickAlike {
        Initial, OuterCellUpdate;

        override val asTick: Tick
            get() = Tick(t = this.ordinal)
    }

    private fun buildScenario(
        shouldOuterCellFreezeSimultaneously: Boolean,
    ) = buildStatelessCellTestScenario(
        configure = {
            val earlierInnerCell = defineDynamicCell(
                initialValue = 10,
                updatedValueByTick = emptyMap(),
                freezeTick = null,
            )

            val updatedInnerCell = defineConstCell(
                constValue = 20,
            )

            val outerCell = defineDynamicCell(
                initialValue = earlierInnerCell,
                updatedValueByTick = mapOf(
                    TimelineTick.OuterCellUpdate to updatedInnerCell,
                ),
                freezeTick = when {
                    shouldOuterCellFreezeSimultaneously -> TimelineTick.OuterCellUpdate
                    else -> null
                },
            )

            Input(
                outerCell = outerCell,
            )
        },
        instantiate = {
            Cell.switch(outerCell)
        },
        verificationTick = TimelineTick.OuterCellUpdate,
        expectedUpdatedValue = 20,
        shouldExpectFreeze = false,
    )

    private val baseScenario = buildScenario(
        shouldOuterCellFreezeSimultaneously = false,
    )

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

    private val outerCellFreezeSimultaneouslyScenario = buildScenario(
        shouldOuterCellFreezeSimultaneously = true,
    )

    @Test
    fun test_outerCellFreezeSimultaneouslyScenario_observedAtTick_outerCellUpdate() {
        outerCellFreezeSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.OuterCellUpdate,
        )
    }

    @Test
    fun test_outerCellFreezeSimultaneouslyScenario_observedAtTick_initial() {
        outerCellFreezeSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }
}
