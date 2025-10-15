package dev.toolkt.reactive.cell

import dev.toolkt.core.utils.iterable.mapOfNotNull
import dev.toolkt.reactive.cell.test_utils.GivenCellTimeline
import dev.toolkt.reactive.cell.test_utils.InertCellFactory
import dev.toolkt.reactive.cell.test_utils.Tick
import dev.toolkt.reactive.cell.test_utils.createDynamicCellExternally
import dev.toolkt.reactive.cell.test_utils.testCell_immediatelyInert
import dev.toolkt.reactive.cell.test_utils.testCell_initiallyDynamic
import kotlin.test.Ignore
import kotlin.test.Test

/**
 * Outer cell: immediately inert
 */
@Suppress("ClassName")
class Cell_switch_state_outerInert_tests {
    /**
     * Inner cell: immediately inert
     */
    @Ignore // FIXME: Expected a null subscription for an inert cell
    @Test
    fun test_state_innerInert() {
        fun test(
            outerCellFactory: InertCellFactory,
            innerCellFactory: InertCellFactory,
        ) = testCell_immediatelyInert(
            setup = {
                val innerCell = innerCellFactory.createInertExternally(
                    inertValue = 10,
                )

                val outerCell = outerCellFactory.createInertExternally(
                    inertValue = innerCell,
                )

                Cell.switch(outerCell)
            },
            expectedValue = 10,
        )

        InertCellFactory.values.forEach { outerCellFactory ->
            InertCellFactory.values.forEach { innerCellFactory ->
                test(
                    outerCellFactory = outerCellFactory,
                    innerCellFactory = innerCellFactory,
                )
            }
        }
    }

    /**
     * Inner cell: initially dynamic
     */
    @Test
    fun test_state_innerDynamic() {
        fun test(
            outerCellFactory: InertCellFactory,
        ) {
            testCell_initiallyDynamic(
                setup = {
                    val innerCell = createDynamicCellExternally(
                        givenInitialValue = 10,
                        givenUpdateByTick = emptyMap(),
                        freezeTick = null,
                    )

                    val outerCell = outerCellFactory.createInertExternally(
                        inertValue = innerCell,
                    )

                    Cell.switch(outerCell)
                },
                expectedInitialValue = 10,
                expectedNotificationByTick = emptyMap(),
            )
        }

        InertCellFactory.values.forEach { outerCellFactory ->
            test(
                outerCellFactory = outerCellFactory,
            )
        }
    }

    /**
     * Inner cell: initially dynamic
     *
     * - The inner cell updates
     */
    @Test
    fun test_state_innerDynamic_innerUpdates() {
        fun test(
            outerCellFactory: InertCellFactory,
            shouldInnerFreezeSimultaneously: Boolean,
        ) {
            testCell_initiallyDynamic(
                setup = {
                    val innerCell = createDynamicCellExternally(
                        givenInitialValue = 10,
                        givenUpdateByTick = mapOf(
                            Tick(1) to GivenCellTimeline.GivenPlainUpdate.of(
                                givenUpdatedValue = 11,
                            ),
                        ),
                        freezeTick = if (shouldInnerFreezeSimultaneously) Tick(1) else null,
                    )

                    val outerCell = outerCellFactory.createInertExternally(
                        inertValue = innerCell,
                    )

                    Cell.switch(outerCell)
                },
                expectedInitialValue = 10,
                expectedNotificationByTick = mapOf(
                    Tick(1) to Cell.UpdateNotification.of(
                        updatedValue = 11,
                        isFreezing = shouldInnerFreezeSimultaneously,
                    ),
                ),
            )
        }

        InertCellFactory.values.forEach { outerCellFactory ->
            test(
                outerCellFactory = outerCellFactory,
                shouldInnerFreezeSimultaneously = false,
            )

            test(
                outerCellFactory = outerCellFactory,
                shouldInnerFreezeSimultaneously = true,
            )
        }
    }

    /**
     * Inner cell: initially dynamic
     *
     * - The inner cell freezes
     */
    @Test
    fun test_state_innerDynamic_innerFreezes() {
        fun test(
            outerCellFactory: InertCellFactory,
            shouldInnerUpdateSimultaneously: Boolean,
        ) {
            val finalUpdatedValue = when {
                shouldInnerUpdateSimultaneously -> 11
                else -> null
            }

            testCell_initiallyDynamic(
                setup = {
                    val innerCell = createDynamicCellExternally(
                        givenInitialValue = 10,
                        givenUpdateByTick = mapOfNotNull(
                            finalUpdatedValue?.let {
                                Tick(1) to GivenCellTimeline.GivenPlainUpdate.of(
                                    givenUpdatedValue = it,
                                )
                            },
                        ),
                        freezeTick = Tick(1),
                    )

                    val outerCell = outerCellFactory.createInertExternally(
                        inertValue = innerCell,
                    )

                    Cell.switch(outerCell)
                },
                expectedInitialValue = 10,
                expectedNotificationByTick = mapOf(
                    Tick(1) to Cell.FreezeNotification.of(
                        updatedValue = finalUpdatedValue,
                    ),
                ),
            )
        }

        InertCellFactory.values.forEach { outerCellFactory ->
            test(
                outerCellFactory = outerCellFactory,
                shouldInnerUpdateSimultaneously = false,
            )

            test(
                outerCellFactory = outerCellFactory,
                shouldInnerUpdateSimultaneously = true,
            )
        }
    }
}
