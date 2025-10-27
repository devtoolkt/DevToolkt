package dev.toolkt.reactive.cell.switch.updating.outerUpdates

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.test_utils.Tick
import dev.toolkt.reactive.cell.test_utils.TickAlike
import dev.toolkt.reactive.cell.test_utils.buildStatelessCellTestScenario
import dev.toolkt.reactive.cell.test_utils.defineDynamicCell
import kotlin.test.Ignore
import kotlin.test.Test

/**
 * The outer cell updates (possibly: freezing). The updated inner cell is dynamic (possibly: frozen / freezing), not
 * updating simultaneously.
 *
 * The result cell should update with the old value (possibly: frozen value) of the updated inner cell.
 *
 * The result cell should not freeze.
 */
@Suppress("ClassName")
class updatedInnerDynamic_notUpdatingSimultaneously_tests {
    private data class Input<ValueT>(
        val outerCell: Cell<Cell<ValueT>>,
    )

    private enum class TimelineTick : TickAlike {
        Initial, OuterCellUpdate;

        override val asTick: Tick
            get() = Tick(t = this.ordinal)
    }

    private enum class UpdatedInnerCellFreezeVariant {
        /**
         * The updated inner cell is dynamic at the moment of the outer cell update.
         */
        None,

        /**
         * The updated inner cell freezes simultaneously with the outer cell update.
         */
        FreezesSimultaneously,

        /**
         * The updated inner cell froze earlier.
         */
        FrozeEarlier,
    }

    private fun buildScenario(
        shouldOuterCellFreezeSimultaneously: Boolean,
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
                    UpdatedInnerCellFreezeVariant.None -> null
                    UpdatedInnerCellFreezeVariant.FreezesSimultaneously -> TimelineTick.OuterCellUpdate
                    UpdatedInnerCellFreezeVariant.FrozeEarlier -> TimelineTick.Initial
                },
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
        updatedInnerCellFreezeVariant = UpdatedInnerCellFreezeVariant.None,
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
        updatedInnerCellFreezeVariant = UpdatedInnerCellFreezeVariant.None,
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

    private val updatedInnerCellFreezesSimultaneouslyScenario = buildScenario(
        shouldOuterCellFreezeSimultaneously = false,
        updatedInnerCellFreezeVariant = UpdatedInnerCellFreezeVariant.FreezesSimultaneously,
    )

    @Test
    fun test_updatedInnerCellFreezesSimultaneouslyScenario_observedAtTick_outerCellUpdate() {
        updatedInnerCellFreezesSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.OuterCellUpdate,
        )
    }

    @Test
    fun test_updatedInnerCellFreezesSimultaneouslyScenario_observedAtTick_initial() {
        updatedInnerCellFreezesSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val updatedInnerCellFrozeEarlierScenario = buildScenario(
        shouldOuterCellFreezeSimultaneously = false,
        updatedInnerCellFreezeVariant = UpdatedInnerCellFreezeVariant.FrozeEarlier,
    )

    @Test
    fun test_updatedInnerCellFrozeEarlierScenario_observedAtTick_outerCellUpdate() {
        updatedInnerCellFrozeEarlierScenario.testObserved(
            observationTick = TimelineTick.OuterCellUpdate,
        )
    }

    @Ignore // FIXME: "Vertex (...) is not a dependent of (...)" (issues with single())
    @Test
    fun test_updatedInnerCellFrozeEarlierScenario_observedAtTick_initial() {
        updatedInnerCellFrozeEarlierScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val bothFreezeSimultaneouslyScenario = buildScenario(
        shouldOuterCellFreezeSimultaneously = true,
        updatedInnerCellFreezeVariant = UpdatedInnerCellFreezeVariant.FreezesSimultaneously,
    )

    @Test
    fun test_bothFreezeSimultaneouslyScenario_observedAtTick_outerCellUpdate() {
        bothFreezeSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.OuterCellUpdate,
        )
    }

    @Test
    fun test_bothFreezeSimultaneouslyScenario_observedAtTick_initial() {
        bothFreezeSimultaneouslyScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }

    private val outerCellFreezeSimultaneouslyUpdatedInnerCellFrozeEarlierScenario = buildScenario(
        shouldOuterCellFreezeSimultaneously = true,
        updatedInnerCellFreezeVariant = UpdatedInnerCellFreezeVariant.FrozeEarlier,
    )

    @Test
    fun test_outerCellFreezeSimultaneouslyUpdatedInnerCellFrozeEarlierScenario_observedAtTick_outerCellUpdate() {
        outerCellFreezeSimultaneouslyUpdatedInnerCellFrozeEarlierScenario.testObserved(
            observationTick = TimelineTick.OuterCellUpdate,
        )
    }

    @Ignore // FIXME: "Vertex (...) is not a dependent of (...)" (issues with single())
    @Test
    fun test_outerCellFreezeSimultaneouslyUpdatedInnerCellFrozeEarlierScenario_observedAtTick_initial() {
        outerCellFreezeSimultaneouslyUpdatedInnerCellFrozeEarlierScenario.testObserved(
            observationTick = TimelineTick.Initial,
        )
    }
}
