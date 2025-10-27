package dev.toolkt.reactive.cell.switch.notFreezing.outerFreezes

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.test_utils.Tick
import dev.toolkt.reactive.cell.test_utils.TickAlike
import dev.toolkt.reactive.cell.test_utils.buildStatelessCellTestScenario
import dev.toolkt.reactive.cell.test_utils.defineDynamicCell
import kotlin.test.Test

/**
 * Outer cell freezes with a warm inner cell.
 *
 * The result cell should not freeze.
 */
@Suppress("ClassName")
class currentInnerDynamic_warmNow_tests {
    private data class Input<ValueT>(
        val outerCell: Cell<Cell<ValueT>>,
    )

    private enum class TimelineTick : TickAlike {
        Initial, OuterCellUpdate, PreOuterCellFreeze, OuterCellFreeze;

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
                freezeTick = null,
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
        shouldExpectFreeze = false,
    )

    @Test
    fun test_baseScenario_observedAtTick_outerCellFreeze() {
        baseScenario.testObserved(
            observationTick = TimelineTick.OuterCellFreeze,
        )
    }

    @Test
    fun test_baseScenario_observedAtTick_preOuterCellFreeze() {
        baseScenario.testObserved(
            observationTick = TimelineTick.PreOuterCellFreeze,
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
