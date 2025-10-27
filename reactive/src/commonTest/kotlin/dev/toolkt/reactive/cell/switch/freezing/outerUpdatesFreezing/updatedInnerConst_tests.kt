package dev.toolkt.reactive.cell.switch.freezing.outerUpdatesFreezing

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.test_utils.Tick
import dev.toolkt.reactive.cell.test_utils.TickAlike
import dev.toolkt.reactive.cell.test_utils.buildStatelessCellTestScenario
import dev.toolkt.reactive.cell.test_utils.defineConstCell
import dev.toolkt.reactive.cell.test_utils.defineDynamicCell
import kotlin.test.Ignore
import kotlin.test.Test

/**
 * The outer cell updates freezing and the updated inner cell is constant.
 *
 * The result cell should freeze.
 */
@Ignore // TODO: Implement freezing
@Suppress("ClassName")
class updatedInnerConst_tests {
    private data class Input<ValueT>(
        val outerCell: Cell<Cell<ValueT>>,
    )

    private enum class TimelineTick : TickAlike {
        Initial, PreOuterCellFreezingUpdate, OuterCellFreezingUpdate;

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

            val updatedInnerCell = defineConstCell(
                constValue = 20,
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

    @Test
    fun test_baseScenario_observedAtTick_outerCellFreezingUpdate() {
        baseScenario.testObserved(
            observationTick = TimelineTick.OuterCellFreezingUpdate,
        )
    }

    @Test
    fun test_baseScenario_observedAtTick_preOuterCellFreezingUpdate() {
        baseScenario.testObserved(
            observationTick = TimelineTick.PreOuterCellFreezingUpdate,
        )
    }

    @Test
    fun test_baseScenario_observedAtTick_initial() {
        baseScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }
}
