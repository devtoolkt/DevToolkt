package dev.toolkt.reactive.cell

import dev.toolkt.reactive.cell.test_utils.ExpectedCellTimeline
import dev.toolkt.reactive.cell.test_utils.ExpectedCellTimeline.ExpectedFreezingNotification
import dev.toolkt.reactive.cell.test_utils.ExpectedCellTimeline.ExpectedUpdate
import dev.toolkt.reactive.cell.test_utils.GivenCellTimeline.GivenFreezingNotification
import dev.toolkt.reactive.cell.test_utils.GivenCellTimeline.GivenUpdate
import dev.toolkt.reactive.cell.test_utils.InertCellFactory
import dev.toolkt.reactive.cell.test_utils.Tick
import dev.toolkt.reactive.cell.test_utils.createDynamicCellExternally
import dev.toolkt.reactive.cell.test_utils.testCell_immediatelyInert
import dev.toolkt.reactive.cell.test_utils.testCell_initiallyDynamic
import kotlin.test.Test

/**
 * Outer cell: immediately inert
 */
@Suppress("ClassName")
class Cell_switch_state_outerInert_tests {
    /**
     * Inner cell: immediately inert
     */
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
                        givenInitialValue = 10, givenNotificationByTick = emptyMap()
                    )

                    val outerCell = outerCellFactory.createInertExternally(
                        inertValue = innerCell,
                    )

                    Cell.switch(outerCell)
                },
                expectedTimeline = ExpectedCellTimeline(
                    expectedInitialValue = 10,
                    expectedNotificationByTick = emptyMap(),
                ),
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
                        givenNotificationByTick = mapOf(
                            Tick(1) to GivenUpdate.of(
                                givenUpdatedValue = 11,
                                shouldFreeze = shouldInnerFreezeSimultaneously,
                            ),
                        ),
                    )

                    val outerCell = outerCellFactory.createInertExternally(
                        inertValue = innerCell,
                    )

                    Cell.switch(outerCell)
                },
                expectedTimeline = ExpectedCellTimeline(
                    expectedInitialValue = 10,
                    expectedNotificationByTick = mapOf(
                        Tick(1) to ExpectedUpdate.of(
                            expectedUpdatedValue = 11,
                            shouldExpectFreeze = shouldInnerFreezeSimultaneously,
                        ),
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
                        givenNotificationByTick = mapOf(
                            Tick(1) to GivenFreezingNotification.of(
                                givenUpdatedValue = finalUpdatedValue,
                            ),
                        ),
                    )

                    val outerCell = outerCellFactory.createInertExternally(
                        inertValue = innerCell,
                    )

                    Cell.switch(outerCell)
                },
                expectedTimeline = ExpectedCellTimeline(
                    expectedInitialValue = 10,
                    expectedNotificationByTick = mapOf(
                        Tick(1) to ExpectedFreezingNotification.of(
                            expectedUpdatedValue = finalUpdatedValue,
                        ),
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
