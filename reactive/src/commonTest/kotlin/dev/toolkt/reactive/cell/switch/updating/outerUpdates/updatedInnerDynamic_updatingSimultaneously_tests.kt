package dev.toolkt.reactive.cell.switch.updating.outerUpdates

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.test_utils.Tick
import dev.toolkt.reactive.cell.test_utils.TickAlike
import dev.toolkt.reactive.cell.test_utils.buildStatelessCellTestScenario
import dev.toolkt.reactive.cell.test_utils.defineDynamicCell
import kotlin.test.Test

/**
 * The outer cell updates (possibly: freezing). The updated inner cell is dynamic (possibly: freezing), updating simultaneously.
 *
 * The result cell should update with the updated value of the updated inner cell.
 * The result cell should freeze iff both the outer cell and the updated inner are updating freezing.
 */
@Suppress("ClassName")
class updatedInnerDynamic_updatingSimultaneously_tests {
    private data class Input<ValueT>(
        val outerCell: Cell<Cell<ValueT>>,
    )

    private enum class TimelineTick : TickAlike {
        Initial, PreOuterCellUpdate, OuterCellUpdate;

        override val asTick: Tick
            get() = Tick(t = this.ordinal)
    }

    private fun buildScenario(
        shouldOuterFreezeSimultaneously: Boolean,
        shouldUpdatedInnerFreezeSimultaneously: Boolean,
    ) = buildStatelessCellTestScenario(
        configure = {
            val earlierInnerCell = defineDynamicCell(
                initialValue = 10,
                updatedValueByTick = emptyMap(),
                freezeTick = null,
            )

            val updatedInnerCell = defineDynamicCell(
                initialValue = 20,
                updatedValueByTick = mapOf(
                    TimelineTick.OuterCellUpdate to 21,
                ),
                freezeTick = TimelineTick.OuterCellUpdate.takeIf { shouldUpdatedInnerFreezeSimultaneously },
            )

            val outerCell = defineDynamicCell(
                initialValue = earlierInnerCell,
                updatedValueByTick = mapOf(
                    TimelineTick.OuterCellUpdate to updatedInnerCell,
                ),
                freezeTick = TimelineTick.OuterCellUpdate.takeIf { shouldOuterFreezeSimultaneously },
            )

            Input(
                outerCell = outerCell,
            )
        },
        instantiate = {
            Cell.switch(outerCell)
        },
        verificationTick = TimelineTick.OuterCellUpdate,
        expectedUpdatedValue = 21,
        shouldExpectFreeze = false,
    )

    private val baseScenario = buildScenario(
        shouldOuterFreezeSimultaneously = false,
        shouldUpdatedInnerFreezeSimultaneously = false,
    )

    @Test
    fun test_base_observedAtTick_outerCellUpdate() {
        baseScenario.testObserved(
            observationTick = TimelineTick.OuterCellUpdate,
        )
    }

    @Test
    fun test_base_observedAtTick_preOuterCellUpdate() {
        baseScenario.testObserved(
            observationTick = TimelineTick.PreOuterCellUpdate,
        )
    }

    @Test
    fun test_base_observedAtTick_initial() {
        baseScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val outerFreezesSimultaneouslyScenario = buildScenario(
        shouldOuterFreezeSimultaneously = true,
        shouldUpdatedInnerFreezeSimultaneously = false,
    )

    @Test
    fun test_outerFreezesSimultaneously_observedAtTick_outerCellUpdate() {
        outerFreezesSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.OuterCellUpdate,
        )
    }

    @Test
    fun test_outerFreezesSimultaneously_observedAtTick_preOuterCellUpdate() {
        outerFreezesSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.PreOuterCellUpdate,
        )
    }

    @Test
    fun test_outerFreezesSimultaneously_observedAtTick_initial() {
        outerFreezesSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val updatedInnerFreezesSimultaneouslyScenario = buildScenario(
        shouldOuterFreezeSimultaneously = false,
        shouldUpdatedInnerFreezeSimultaneously = true,
    )

    @Test
    fun test_updatedInnerFreezesSimultaneously_observedAtTick_outerCellUpdate() {
        updatedInnerFreezesSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.OuterCellUpdate,
        )
    }

    @Test
    fun test_updatedInnerFreezesSimultaneously_observedAtTick_preOuterCellUpdate() {
        updatedInnerFreezesSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.PreOuterCellUpdate,
        )
    }

    @Test
    fun test_updatedInnerFreezesSimultaneously_observedAtTick_initial() {
        updatedInnerFreezesSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val bothFreezeSimultaneouslyScenario = buildScenario(
        shouldOuterFreezeSimultaneously = true,
        shouldUpdatedInnerFreezeSimultaneously = true,
    )

    @Test
    fun test_bothFreezeSimultaneously_observedAtTick_outerCellUpdate() {
        bothFreezeSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.OuterCellUpdate,
        )
    }

    @Test
    fun test_bothFreezeSimultaneously_observedAtTick_preOuterCellUpdate() {
        bothFreezeSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.PreOuterCellUpdate,
        )
    }

    @Test
    fun test_bothFreezeSimultaneously_observedAtTick_initial() {
        bothFreezeSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }
}
