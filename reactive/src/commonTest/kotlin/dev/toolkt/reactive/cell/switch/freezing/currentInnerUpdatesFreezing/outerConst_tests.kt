package dev.toolkt.reactive.cell.switch.freezing.currentInnerUpdatesFreezing

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.test_utils.Tick
import dev.toolkt.reactive.cell.test_utils.TickAlike
import dev.toolkt.reactive.cell.test_utils.buildStatelessCellTestScenario
import dev.toolkt.reactive.cell.test_utils.defineConstCell
import dev.toolkt.reactive.cell.test_utils.defineDynamicCell
import kotlin.test.Ignore
import kotlin.test.Test

/**
 * The current inner cell updates freezing while the outer cell is constant.
 *
 * The result cell should update.
 *
 * The result cell should freeze.
 */
@Ignore // TODO: Implement freezing
@Suppress("ClassName")
class outerConst_tests {
    private data class Input<ValueT>(
        val outerCell: Cell<Cell<ValueT>>,
    )

    private enum class TimelineTick : TickAlike {
        Initial, CurrentInnerCellFreeze;

        override val asTick: Tick
            get() = Tick(t = this.ordinal)
    }

    private val baseScenario = buildStatelessCellTestScenario(
        configure = {
            val currentInnerCell = defineDynamicCell(
                initialValue = 20,
                updatedValueByTick = mapOf(
                    TimelineTick.CurrentInnerCellFreeze to 21,
                ),
                freezeTick = TimelineTick.CurrentInnerCellFreeze,
            )

            val outerCell = defineConstCell(
                constValue = currentInnerCell,
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

    @Test
    fun test_baseScenario_observedAtTick_currentInnerCellFreeze() {
        baseScenario.testObserved(
            observationTick = TimelineTick.CurrentInnerCellFreeze,
        )
    }

    @Test
    fun test_baseScenario_observedAtTick_initial() {
        baseScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }
}
