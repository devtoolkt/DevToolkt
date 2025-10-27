package dev.toolkt.reactive.cell.switch.freezing.outerFreezes

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.test_utils.Tick
import dev.toolkt.reactive.cell.test_utils.TickAlike
import dev.toolkt.reactive.cell.test_utils.buildStatelessCellTestScenario
import dev.toolkt.reactive.cell.test_utils.defineConstCell
import dev.toolkt.reactive.cell.test_utils.defineDynamicCell
import kotlin.test.Ignore
import kotlin.test.Test

/**
 * Outer cell freezes with a constant current inner cell.
 *
 * The result cell should freeze.
 */
@Ignore // TODO: Implement freezing
@Suppress("ClassName")
class currentInnerConst_tests {
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

            val currentInnerCell = defineConstCell(
                constValue = 20,
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
