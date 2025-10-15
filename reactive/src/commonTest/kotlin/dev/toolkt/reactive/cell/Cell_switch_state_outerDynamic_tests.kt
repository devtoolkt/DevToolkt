package dev.toolkt.reactive.cell

import dev.toolkt.core.utils.iterable.mapOfNotNull
import dev.toolkt.reactive.cell.test_utils.GivenCellTimeline.GivenPlainUpdate
import dev.toolkt.reactive.cell.test_utils.InertCellFactory
import dev.toolkt.reactive.cell.test_utils.Tick
import dev.toolkt.reactive.cell.test_utils.createDynamicCellExternally
import dev.toolkt.reactive.cell.test_utils.testCell_initiallyDynamic
import kotlin.test.Ignore
import kotlin.test.Test

/**
 * Outer cell: initially dynamic
 */
@Suppress("ClassName")
class Cell_switch_state_outerDynamic_tests {
    /**
     * Initial inner cell: immediately inert
     */
    @Test
    fun test_state_initialInnerInert() {
        fun test(
            innerCellFactory: InertCellFactory,
        ) {
            testCell_initiallyDynamic(
                setup = {
                    val innerCell = innerCellFactory.createInertExternally(
                        inertValue = 10,
                    )

                    val outerCell = createDynamicCellExternally(
                        givenInitialValue = innerCell,
                        givenUpdateByTick = emptyMap(),
                        freezeTick = null,
                    )

                    Cell.switch(outerCell)
                },
                expectedInitialValue = 10,
                expectedNotificationByTick = emptyMap(),
            )
        }

        InertCellFactory.values.forEach { innerCellFactory ->
            test(
                innerCellFactory = innerCellFactory,
            )
        }
    }

    /**
     * Initial inner cell: initially dynamic
     */
    @Test
    fun test_state_initialInnerDynamic() = testCell_initiallyDynamic(
        setup = {
            val innerCell = createDynamicCellExternally(
                givenInitialValue = 10,
                givenUpdateByTick = emptyMap(),
                freezeTick = null,
            )

            val outerCell = createDynamicCellExternally(
                givenInitialValue = innerCell,
                givenUpdateByTick = emptyMap(),
                freezeTick = null,
            )

            Cell.switch(outerCell)
        },
        expectedInitialValue = 10,
        expectedNotificationByTick = emptyMap(),
    )

    /**
     * Initial inner cell: initially dynamic
     *
     * - The initial inner cell updates
     */
    @Test
    fun test_state_initialInnerDynamic_initialInnerUpdates() {
        fun test(
            shouldInitialInnerFreezeSimultaneously: Boolean,
        ) = testCell_initiallyDynamic(
            setup = {
                val innerCell = createDynamicCellExternally(
                    givenInitialValue = 10,
                    givenUpdateByTick = mapOf(
                        Tick(1) to GivenPlainUpdate.of(
                            givenUpdatedValue = 11,
                        ),
                    ),
                    freezeTick = if (shouldInitialInnerFreezeSimultaneously) Tick(1) else null,
                )

                val outerCell = createDynamicCellExternally(
                    givenInitialValue = innerCell,
                    givenUpdateByTick = emptyMap(),
                    freezeTick = null,
                )

                Cell.switch(outerCell)
            },
            expectedInitialValue = 10,
            expectedNotificationByTick = mapOf(
                Tick(1) to Cell.IntermediateUpdateNotification(
                    updatedValue = 11,
                ),
            ),
        )

        test(
            shouldInitialInnerFreezeSimultaneously = false,
        )

        test(
            shouldInitialInnerFreezeSimultaneously = true,
        )
    }

    @Test
    fun test_state_outerUpdates_newInnerInert() {
        fun test(
            newInnerCellFactory: InertCellFactory,
            shouldOuterFreezeSimultaneously: Boolean,
            shouldOldInnerUpdateSimultaneously: Boolean,
        ) = testCell_initiallyDynamic(
            setup = {
                val initialInnerCell = createDynamicCellExternally(
                    givenInitialValue = 10,
                    givenUpdateByTick = mapOfNotNull(
                        (Tick(1) to GivenPlainUpdate.of(
                            givenUpdatedValue = 11,
                        )).takeIf { shouldOldInnerUpdateSimultaneously },
                    ),
                    freezeTick = null,
                )

                val newInnerCell = newInnerCellFactory.createInertExternally(
                    inertValue = 20,
                )

                val outerCell = createDynamicCellExternally(
                    givenInitialValue = initialInnerCell,
                    givenUpdateByTick = mapOf(
                        Tick(1) to GivenPlainUpdate.of(
                            givenUpdatedValue = newInnerCell,
                        ),
                    ),
                    freezeTick = if (shouldOuterFreezeSimultaneously) Tick(1) else null,
                )

                Cell.switch(outerCell)
            },
            expectedInitialValue = 10,
            expectedNotificationByTick = mapOf(
                Tick(1) to if (shouldOuterFreezeSimultaneously) Cell.FreezingUpdateNotification(
                    updatedFrozenValue = 20,
                )
                else Cell.IntermediateUpdateNotification(
                    updatedValue = 20,
                )
            ),
        )

        InertCellFactory.values.forEach { newInnerCellFactory ->
            test(
                newInnerCellFactory = newInnerCellFactory, // 👈
                shouldOuterFreezeSimultaneously = true,
                shouldOldInnerUpdateSimultaneously = false,
            )
        }

        test(
            newInnerCellFactory = InertCellFactory.Const,
            shouldOuterFreezeSimultaneously = false,
            shouldOldInnerUpdateSimultaneously = true, // 👈
        )
    }

    /**
     * - The outer cell updates (new inner cell: initially dynamic)
     */
    @Test
    fun test_state_outerUpdates_newInnerDynamic() {
        fun test(
            shouldOuterFreezeSimultaneously: Boolean,
            shouldOldInnerUpdateSimultaneously: Boolean,
            shouldNewInnerFreezeSimultaneously: Boolean,
        ) = testCell_initiallyDynamic(
            setup = {
                val initialInnerCell = createDynamicCellExternally(
                    givenInitialValue = 10,
                    givenUpdateByTick = mapOfNotNull(
                        (Tick(1) to GivenPlainUpdate.of(
                            givenUpdatedValue = 11,
                        )).takeIf { shouldOldInnerUpdateSimultaneously },
                    ),
                    freezeTick = null,
                )

                val newInnerCell = createDynamicCellExternally(
                    givenInitialValue = 20,
                    givenUpdateByTick = emptyMap(),
                    freezeTick = if (shouldNewInnerFreezeSimultaneously) Tick(1) else null,
                )

                val outerCell = createDynamicCellExternally(
                    givenInitialValue = initialInnerCell,
                    givenUpdateByTick = mapOf(
                        Tick(1) to GivenPlainUpdate.of(
                            givenUpdatedValue = newInnerCell,
                        ),
                    ),
                    freezeTick = if (shouldOuterFreezeSimultaneously) Tick(1) else null,
                )

                Cell.switch(outerCell)
            },
            expectedInitialValue = 10,
            expectedNotificationByTick = mapOf(
                Tick(1) to Cell.UpdateNotification.of(
                    updatedValue = 20,
                    isFreezing = shouldOuterFreezeSimultaneously && shouldNewInnerFreezeSimultaneously,
                ),
            ),
        )

        test(
            shouldOuterFreezeSimultaneously = true,
            shouldOldInnerUpdateSimultaneously = false,
            shouldNewInnerFreezeSimultaneously = false,
        )

        test(
            shouldOuterFreezeSimultaneously = false,
            shouldOldInnerUpdateSimultaneously = true,
            shouldNewInnerFreezeSimultaneously = false,
        )

        test(
            shouldOuterFreezeSimultaneously = false,
            shouldOldInnerUpdateSimultaneously = false,
            shouldNewInnerFreezeSimultaneously = true,
        )
    }

    /**
     * - The outer cell updates (new inner cell: initially dynamic)
     * - Simultaneously: The new inner cell updates
     */
    @Test
    fun test_state_outerUpdates_newInnerDynamic_newInnerUpdatesSimultaneously() {
        fun test(
            shouldOuterFreezeSimultaneously: Boolean,
            shouldInnerFreezeSimultaneously: Boolean,
        ) = testCell_initiallyDynamic(
            setup = {
                val initialInnerCell = createDynamicCellExternally(
                    givenInitialValue = 10,
                    givenUpdateByTick = emptyMap(),
                    freezeTick = null,
                )

                val newInnerCell = createDynamicCellExternally(
                    givenInitialValue = 20,
                    givenUpdateByTick = mapOf(
                        Tick(1) to GivenPlainUpdate.of(
                            givenUpdatedValue = 21,
                        ),
                    ),
                    freezeTick = if (shouldInnerFreezeSimultaneously) Tick(1) else null,
                )

                val outerCell = createDynamicCellExternally(
                    givenInitialValue = initialInnerCell,
                    givenUpdateByTick = mapOf(
                        Tick(1) to GivenPlainUpdate.of(
                            givenUpdatedValue = newInnerCell,
                        ),
                    ),
                    freezeTick = if (shouldOuterFreezeSimultaneously) Tick(1) else null,
                )

                Cell.switch(outerCell)
            },
            expectedInitialValue = 10,
            expectedNotificationByTick = mapOf(
                Tick(1) to when {
                    shouldOuterFreezeSimultaneously && shouldInnerFreezeSimultaneously -> Cell.FreezingUpdateNotification(
                        updatedFrozenValue = 21,
                    )

                    else -> Cell.IntermediateUpdateNotification(
                        updatedValue = 21,
                    )
                }
            ),
        )

        test(
            shouldOuterFreezeSimultaneously = false,
            shouldInnerFreezeSimultaneously = false,
        )

        test(
            shouldOuterFreezeSimultaneously = true,
            shouldInnerFreezeSimultaneously = false,
        )

        test(
            shouldOuterFreezeSimultaneously = false,
            shouldInnerFreezeSimultaneously = true,
        )

        test(
            shouldOuterFreezeSimultaneously = true,
            shouldInnerFreezeSimultaneously = true,
        )
    }

    /**
     * - The outer cell updates (new inner cell: initially dynamic)
     * - The subsequent inner cell updates
     */
    @Test
    @Ignore // FIXME: Vertex (...) is not a dependent of (...)
    fun test_state_outerUpdates_newInnerDynamic_newInnerUpdatesLater() {
        fun test(
            shouldOuterFreezeSimultaneously: Boolean,
            shouldSubsequentInnerFreezeSimultaneously: Boolean,
        ) = testCell_initiallyDynamic(
            setup = {
                val initialInnerCell = createDynamicCellExternally(
                    givenInitialValue = 10,
                    givenUpdateByTick = emptyMap(),
                    freezeTick = null,
                )

                val subsequentInnerCell = createDynamicCellExternally(
                    givenInitialValue = 20,
                    givenUpdateByTick = mapOf(
                        Tick(2) to GivenPlainUpdate.of(
                            givenUpdatedValue = 21,
                        ),
                    ),
                    freezeTick = if (shouldSubsequentInnerFreezeSimultaneously) Tick(2) else null,
                )

                val outerCell = createDynamicCellExternally(
                    givenInitialValue = initialInnerCell,
                    givenUpdateByTick = mapOf(
                        Tick(1) to GivenPlainUpdate.of(
                            givenUpdatedValue = subsequentInnerCell,
                        ),
                    ),
                    freezeTick = if (shouldOuterFreezeSimultaneously) Tick(1) else null,
                )

                Cell.switch(outerCell)
            },
            expectedInitialValue = 10,
            expectedNotificationByTick = mapOf(
                Tick(1) to Cell.IntermediateUpdateNotification(
                    updatedValue = 20,
                ),
                Tick(2) to Cell.UpdateNotification.of(
                    updatedValue = 21,
                    isFreezing = shouldOuterFreezeSimultaneously && shouldSubsequentInnerFreezeSimultaneously,
                ),
            ),
        )

        test(
            shouldOuterFreezeSimultaneously = false,
            shouldSubsequentInnerFreezeSimultaneously = false,
        )

        test(
            shouldOuterFreezeSimultaneously = true,
            shouldSubsequentInnerFreezeSimultaneously = false,
        )

        test(
            shouldOuterFreezeSimultaneously = false,
            shouldSubsequentInnerFreezeSimultaneously = true,
        )

        test(
            shouldOuterFreezeSimultaneously = true,
            shouldSubsequentInnerFreezeSimultaneously = true,
        )
    }

    /**
     * Initial inner cell: inert
     *
     * - The outer cell freezes
     */
    @Test
    fun test_state_initialInnerInert_outerJustFreezes() {
        fun test(
            initialInnerCellFactory: InertCellFactory,
        ) {
            testCell_initiallyDynamic(
                setup = {
                    val initialInnerCell = initialInnerCellFactory.createInertExternally(
                        inertValue = 10,
                    )

                    val outerCell = createDynamicCellExternally(
                        givenInitialValue = initialInnerCell,
                        givenUpdateByTick = emptyMap(),
                        freezeTick = Tick(1),
                    )

                    Cell.switch(outerCell)
                },
                expectedInitialValue = 10,
                expectedNotificationByTick = mapOf(
                    Tick(1) to Cell.IsolatedFreezeNotification,
                ),
            )
        }

        InertCellFactory.values.forEach { initialInnerCellFactory ->
            test(
                initialInnerCellFactory = initialInnerCellFactory,
            )
        }
    }

    /**
     * Initial inner cell: dynamic
     *
     * - The outer cell freezes
     * - The initial inner cell freezes
     */
    @Test
    @Ignore // FIXME: Vertex (...) is not a dependent of (...)
    fun test_state_initialInnerDynamic_outerJustFreezes_initialInnerFreezesLater() {
        fun test(
            shouldInitialInnerUpdateSimultaneously: Boolean,
        ) {
            val finalUpdatedValue = when {
                shouldInitialInnerUpdateSimultaneously -> 11
                else -> null
            }

            testCell_initiallyDynamic(
                setup = {
                    val initialInnerCell = createDynamicCellExternally(
                        givenInitialValue = 10,
                        givenUpdateByTick = emptyMap(),
                        freezeTick = Tick(2),
                    )

                    val outerCell = createDynamicCellExternally(
                        givenInitialValue = initialInnerCell,
                        givenUpdateByTick = emptyMap(),
                        freezeTick = Tick(1),
                    )

                    Cell.switch(outerCell)
                },
                expectedInitialValue = 10,
                expectedNotificationByTick = mapOf(
                    Tick(2) to Cell.FreezeNotification.of(
                        updatedValue = finalUpdatedValue,
                    ),
                ),
            )
        }

        test(
            shouldInitialInnerUpdateSimultaneously = false,
        )

        test(
            shouldInitialInnerUpdateSimultaneously = true,
        )
    }

    /**
     * Initial inner cell: dynamic
     *
     * - The outer cell freezes
     * - Simultaneously: The initial inner cell freezes
     */
    @Test
    fun test_state_initialInnerDynamic_outerJustFreezes_initialInnerFreezesSimultaneously() {
        fun test(
            shouldInitialInnerUpdateSimultaneously: Boolean,
        ) {
            val finalUpdatedValue = when {
                shouldInitialInnerUpdateSimultaneously -> 11
                else -> null
            }

            testCell_initiallyDynamic(
                setup = {
                    val initialInnerCell = createDynamicCellExternally(
                        givenInitialValue = 10,
                        givenUpdateByTick = mapOfNotNull(
                            finalUpdatedValue?.let {
                                Tick(1) to GivenPlainUpdate.of(
                                    givenUpdatedValue = it,
                                )
                            },
                        ),
                        freezeTick = Tick(1),
                    )

                    val outerCell = createDynamicCellExternally(
                        givenInitialValue = initialInnerCell,
                        givenUpdateByTick = emptyMap(),
                        freezeTick = Tick(1),
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

        test(
            shouldInitialInnerUpdateSimultaneously = false,
        )

        test(
            shouldInitialInnerUpdateSimultaneously = true,
        )
    }

    /**
     * - The outer cell updates (new inner cell: immediately inert)
     * - The outer cell freezes
     */
    @Test
    fun test_state_outerUpdates_newInnerInert_outerJustFreezes() {
        fun test(
            newInnerCellFactory: InertCellFactory,
        ) {
            testCell_initiallyDynamic(
                setup = {
                    val initialInnerCell = createDynamicCellExternally(
                        givenInitialValue = 10,
                        givenUpdateByTick = emptyMap(),
                        freezeTick = null,
                    )

                    val subsequentInnerCell = newInnerCellFactory.createInertExternally(
                        inertValue = 20,
                    )

                    val outerCell = createDynamicCellExternally(
                        givenInitialValue = initialInnerCell,
                        givenUpdateByTick = mapOf(
                            Tick(1) to GivenPlainUpdate.of(
                                givenUpdatedValue = subsequentInnerCell,
                            ),
                        ),
                        freezeTick = Tick(2),
                    )

                    Cell.switch(outerCell)
                },
                expectedInitialValue = 10,
                expectedNotificationByTick = mapOf(
                    Tick(1) to Cell.IntermediateUpdateNotification(
                        updatedValue = 20,
                    ),
                    Tick(2) to Cell.IsolatedFreezeNotification,
                ),
            )
        }

        InertCellFactory.values.forEach { newInnerCellFactory ->
            test(
                newInnerCellFactory = newInnerCellFactory,
            )
        }
    }

    /**
     * - The outer cell updates
     * - The outer cell freezes
     * - The subsequent inner cell freezes
     */
    @Test
    fun test_state_outerUpdates_newInnerDynamic_outerJustFreezes_newInnerFreezesLater() {
        fun test(
            shouldSubsequentInnerUpdateSimultaneously: Boolean,
        ) {
            val finalUpdatedValue = when {
                shouldSubsequentInnerUpdateSimultaneously -> 21
                else -> null
            }

            testCell_initiallyDynamic(
                setup = {
                    val initialInnerCell = createDynamicCellExternally(
                        givenInitialValue = 10,
                        givenUpdateByTick = emptyMap(),
                        freezeTick = null,
                    )

                    val subsequentInnerCell = createDynamicCellExternally(
                        givenInitialValue = 20,
                        givenUpdateByTick = mapOfNotNull(
                            finalUpdatedValue?.let {
                                Tick(3) to GivenPlainUpdate.of(
                                    givenUpdatedValue = it,
                                )
                            }
                        ),
                        freezeTick = Tick(3),
                    )

                    val outerCell = createDynamicCellExternally(
                        givenInitialValue = initialInnerCell,
                        givenUpdateByTick = mapOf(
                            Tick(1) to GivenPlainUpdate.of(
                                givenUpdatedValue = subsequentInnerCell,
                            ),
                        ),
                        freezeTick = Tick(2),
                    )

                    Cell.switch(outerCell)
                },
                expectedInitialValue = 10,
                expectedNotificationByTick = mapOf(
                    Tick(1) to Cell.IntermediateUpdateNotification(
                        updatedValue = 20,
                    ),
                    Tick(3) to Cell.FreezeNotification.of(
                        updatedValue = finalUpdatedValue,
                    ),
                ),
            )
        }

        test(
            shouldSubsequentInnerUpdateSimultaneously = false,
        )

        test(
            shouldSubsequentInnerUpdateSimultaneously = true,
        )
    }

    /**
     * - The outer cell updates
     * - The outer cell freezes
     * - Simultaneously: The subsequent inner cell freezes
     */
    @Test
    fun test_state_outerUpdates_newInnerDynamic_outerJustFreezes_newInnerFreezesSimultaneously() {
        fun test(
            shouldSubsequentInnerUpdateSimultaneously: Boolean,
        ) {
            val finalUpdatedValue = when {
                shouldSubsequentInnerUpdateSimultaneously -> 21
                else -> null
            }

            testCell_initiallyDynamic(
                setup = {
                    val initialInnerCell = createDynamicCellExternally(
                        givenInitialValue = 10,
                        givenUpdateByTick = emptyMap(),
                        freezeTick = null,
                    )

                    val subsequentInnerCell = createDynamicCellExternally(
                        givenInitialValue = 20,
                        givenUpdateByTick = mapOfNotNull(
                            finalUpdatedValue?.let {
                                Tick(2) to GivenPlainUpdate.of(
                                    givenUpdatedValue = it,
                                )
                            }
                        ),
                        freezeTick = Tick(2),
                    )

                    val outerCell = createDynamicCellExternally(
                        givenInitialValue = initialInnerCell,
                        givenUpdateByTick = mapOf(
                            Tick(1) to GivenPlainUpdate.of(
                                givenUpdatedValue = subsequentInnerCell,
                            ),
                        ),
                        freezeTick = Tick(2),
                    )

                    Cell.switch(outerCell)
                },
                expectedInitialValue = 10,
                expectedNotificationByTick = mapOf(
                    Tick(1) to Cell.IntermediateUpdateNotification(
                        updatedValue = 20,
                    ),
                    Tick(2) to Cell.FreezeNotification.of(
                        updatedValue = finalUpdatedValue,
                    ),
                ),
            )
        }

        test(
            shouldSubsequentInnerUpdateSimultaneously = false,
        )

        test(
            shouldSubsequentInnerUpdateSimultaneously = true,
        )
    }

    /**
     * - The outer cell updates, freezing (new inner cell: immediately inert)
     */
    @Test
    fun test_state_outerUpdatesFreezing_newInnerInert() {
        fun test(
            newInnerCellFactory: InertCellFactory,
        ) {
            testCell_initiallyDynamic(
                setup = {
                    val initialInnerCell = createDynamicCellExternally(
                        givenInitialValue = 10,
                        givenUpdateByTick = emptyMap(),
                        freezeTick = null,
                    )

                    val newInnerCell = newInnerCellFactory.createInertExternally(
                        inertValue = 20,
                    )

                    val outerCell = createDynamicCellExternally(
                        givenInitialValue = initialInnerCell,
                        givenUpdateByTick = mapOf(
                            Tick(1) to GivenPlainUpdate.of(
                                givenUpdatedValue = newInnerCell,
                            ),
                        ),
                        freezeTick = Tick(1),
                    )

                    Cell.switch(outerCell)
                },
                expectedInitialValue = 10,
                expectedNotificationByTick = mapOf(
                    Tick(1) to Cell.FreezingUpdateNotification(
                        updatedFrozenValue = 20,
                    ),
                ),
            )
        }

        InertCellFactory.values.forEach { newInnerCellFactory ->
            test(
                newInnerCellFactory = newInnerCellFactory,
            )
        }
    }

    /**
     * - The outer cell updates, freezing (new inner cell: initially dynamic)
     * - The new inner cell freezes
     */
    @Test
    @Ignore // FIXME: Vertex (...) is not a dependent of (...)
    fun test_state_outerUpdatesFreezing_newInnerDynamic_newInnerFreezesLater() {
        fun test(
            shouldNewInnerUpdateSimultaneously: Boolean,
        ) {
            val finalUpdatedValue = when {
                shouldNewInnerUpdateSimultaneously -> 21
                else -> null
            }

            testCell_initiallyDynamic(
                setup = {
                    val initialInnerCell = createDynamicCellExternally(
                        givenInitialValue = 10,
                        givenUpdateByTick = emptyMap(),
                        freezeTick = null,
                    )

                    val newInnerCell = createDynamicCellExternally(
                        givenInitialValue = 20,
                        givenUpdateByTick = emptyMap(),
                        freezeTick = Tick(2),
                    )

                    val outerCell = createDynamicCellExternally(
                        givenInitialValue = initialInnerCell,
                        givenUpdateByTick = mapOf(
                            Tick(1) to GivenPlainUpdate.of(
                                givenUpdatedValue = newInnerCell,
                            ),
                        ),
                        freezeTick = Tick(1),
                    )

                    Cell.switch(outerCell)
                },
                expectedInitialValue = 10,
                expectedNotificationByTick = mapOf(
                    Tick(1) to Cell.IntermediateUpdateNotification(
                        updatedValue = 20,
                    ),
                    Tick(2) to Cell.FreezeNotification.of(
                        updatedValue = finalUpdatedValue,
                    ),
                ),
            )
        }

        test(
            shouldNewInnerUpdateSimultaneously = false,
        )

        test(
            shouldNewInnerUpdateSimultaneously = true,
        )
    }

    /**
     * - The outer cell updates, freezing (new inner cell: initially dynamic)
     * - Simultaneously: The new inner cell freezes
     */
    @Test
    fun test_state_outerUpdatesFreezing_newInnerDynamic_newInnerFreezesSimultaneously() {
        fun test(
            shouldNewInnerUpdateSimultaneously: Boolean,
        ) {
            val finalUpdatedValue = when {
                shouldNewInnerUpdateSimultaneously -> 21
                else -> null
            }

            testCell_initiallyDynamic(
                setup = {
                    val initialInnerCell = createDynamicCellExternally(
                        givenInitialValue = 10,
                        givenUpdateByTick = emptyMap(),
                        freezeTick = null,
                    )

                    val newInnerCell = createDynamicCellExternally(
                        givenInitialValue = 20,
                        givenUpdateByTick = mapOfNotNull(
                            finalUpdatedValue?.let {
                                Tick(1) to GivenPlainUpdate.of(
                                    givenUpdatedValue = it,
                                )
                            }
                        ),
                        freezeTick = Tick(1),
                    )

                    val outerCell = createDynamicCellExternally(
                        givenInitialValue = initialInnerCell,
                        givenUpdateByTick = mapOf(
                            Tick(1) to GivenPlainUpdate.of(
                                givenUpdatedValue = newInnerCell,
                            ),
                        ),
                        freezeTick = Tick(1),
                    )

                    Cell.switch(outerCell)
                },
                expectedInitialValue = 10,
                expectedNotificationByTick = mapOf(
                    Tick(1) to Cell.FreezingUpdateNotification(
                        updatedFrozenValue = when {
                            shouldNewInnerUpdateSimultaneously -> 21
                            else -> 20
                        }
                    ),
                ),
            )
        }

        test(
            shouldNewInnerUpdateSimultaneously = false,
        )

        test(
            shouldNewInnerUpdateSimultaneously = true,
        )
    }
}
