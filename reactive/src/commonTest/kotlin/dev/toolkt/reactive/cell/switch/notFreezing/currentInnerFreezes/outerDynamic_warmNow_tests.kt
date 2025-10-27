package dev.toolkt.reactive.cell.switch.notFreezing.currentInnerFreezes

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.test_utils.Tick
import dev.toolkt.reactive.cell.test_utils.TickAlike
import dev.toolkt.reactive.cell.test_utils.buildStatelessCellTestScenario
import dev.toolkt.reactive.cell.test_utils.defineDynamicCell
import kotlin.test.Test

/**
 * The current inner cell freezes while the outer cell is warm.
 *
 * The result cell should not freeze.
 */
@Suppress("ClassName")
class outerDynamic_warmNow_tests {
    private data class Input<ValueT>(
        val outerCell: Cell<Cell<ValueT>>,
    )

    private enum class TimelineTick : TickAlike {
        Initial, OuterCellUpdate, PreCurrentInnerCellFreeze, CurrentInnerCellFreeze;

        override val asTick: Tick
            get() = Tick(t = this.ordinal)
    }

    private val baseScenario = buildStatelessCellTestScenario(
        configure = {
            val earlierInnerCell = defineDynamicCell(
                initialValue = 10,
                updatedValueByTick = emptyMap(),
                freezeTick = null,
            )

            val currentInnerCell = defineDynamicCell(
                initialValue = 20,
                updatedValueByTick = emptyMap(),
                freezeTick = TimelineTick.CurrentInnerCellFreeze,
            )

            val outerCell = defineDynamicCell(
                initialValue = earlierInnerCell,
                updatedValueByTick = mapOf(
                    TimelineTick.OuterCellUpdate to currentInnerCell,
                ),
                freezeTick = null,
            )

            Input(
                outerCell = outerCell,
            )
        },
        instantiate = {
            Cell.switch(outerCell)
        },
        verificationTick = TimelineTick.CurrentInnerCellFreeze,
        expectedUpdatedValue = null,
        shouldExpectFreeze = false,
    )

    @Test
    fun test_baseScenario_observedAtTick_currentInnerCellFreeze() {
        baseScenario.testObserved(
            observationTick = TimelineTick.CurrentInnerCellFreeze,
        )
    }

    @Test
    fun test_baseScenario_observedAtTick_preCurrentInnerCellFreeze() {
        baseScenario.testObserved(
            observationTick = TimelineTick.PreCurrentInnerCellFreeze,
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
}
