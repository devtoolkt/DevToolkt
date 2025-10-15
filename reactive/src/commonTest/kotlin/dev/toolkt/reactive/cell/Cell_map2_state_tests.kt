package dev.toolkt.reactive.cell

import dev.toolkt.reactive.cell.test_utils.GivenCellTimeline.GivenPlainUpdate
import dev.toolkt.reactive.cell.test_utils.InertCellFactory
import dev.toolkt.reactive.cell.test_utils.Tick
import dev.toolkt.reactive.cell.test_utils.createDynamicCellExternally
import dev.toolkt.reactive.cell.test_utils.testCell_immediatelyInert
import dev.toolkt.reactive.cell.test_utils.testCell_initiallyDynamic
import kotlin.test.Ignore
import kotlin.test.Test

@Suppress("ClassName")
class Cell_map2_state_tests {
    @Test
    @Ignore // FIXME: Expected a null subscription for an inert cell
    fun test_state_bothSourcesInert() {
        fun test(
            source1CellFactory: InertCellFactory,
            source2CellFactory: InertCellFactory,
        ) = testCell_immediatelyInert(
            setup = {
                val sourceCell1 = source1CellFactory.createInertExternally(
                    inertValue = 10,
                )

                val sourceCell2 = source2CellFactory.createInertExternally(
                    inertValue = 'A',
                )

                Cell.map2(
                    sourceCell1,
                    sourceCell2,
                ) { value1, value2 ->
                    "$value1:$value2"
                }
            },
            expectedValue = "10:A",
        )

        InertCellFactory.values.forEach { source1CellFactory ->
            InertCellFactory.values.forEach { source2CellFactory ->
                test(
                    source1CellFactory = source1CellFactory,
                    source2CellFactory = source2CellFactory,
                )
            }
        }
    }

    @Test
    fun test_state_bothSourcesDynamic_noSourceUpdates() = testCell_initiallyDynamic(
        setup = {
            val sourceCell1 = createDynamicCellExternally(
                givenInitialValue = 10,
                givenUpdateByTick = emptyMap(),
                freezeTick = null,
            )

            val sourceCell2 = createDynamicCellExternally(
                givenInitialValue = 'A',
                givenUpdateByTick = emptyMap(),
                freezeTick = null,
            )

            Cell.map2(
                sourceCell1,
                sourceCell2,
            ) { value1, value2 ->
                "$value1:$value2"
            }
        },
        expectedInitialValue = "10:A",
        expectedNotificationByTick = emptyMap(),
    )

    @Test
    fun test_state_bothSourcesDynamic_firstSourceUpdates() = testCell_initiallyDynamic(
        setup = {
            val sourceCell1 = createDynamicCellExternally(
                givenInitialValue = 10,
                givenUpdateByTick = mapOf(
                    Tick(1) to GivenPlainUpdate.of(
                        givenUpdatedValue = 20,
                    ),
                ),
                freezeTick = null,
            )

            val sourceCell2 = createDynamicCellExternally(
                givenInitialValue = 'A',
                givenUpdateByTick = emptyMap(),
                freezeTick = null,
            )

            Cell.map2(
                sourceCell1,
                sourceCell2,
            ) { value1, value2 ->
                "$value1:$value2"
            }
        },
        expectedInitialValue = "10:A",
        expectedNotificationByTick = mapOf(
            Tick(1) to Cell.IntermediateUpdateNotification(
                updatedValue = "20:A",
            ),
        ),
    )

    @Test
    fun test_state_bothSourcesDynamic_secondSourceUpdates() = testCell_initiallyDynamic(
        setup = {
            val sourceCell1 = createDynamicCellExternally(
                givenInitialValue = 10,
                givenUpdateByTick = emptyMap(),
                freezeTick = null,
            )

            val sourceCell2 = createDynamicCellExternally(
                givenInitialValue = 'A',
                givenUpdateByTick = mapOf(
                    Tick(1) to GivenPlainUpdate.of('B'),
                ),
                freezeTick = null,
            )

            Cell.map2(
                sourceCell1,
                sourceCell2,
            ) { value1, value2 ->
                "$value1:$value2"
            }
        },
        expectedInitialValue = "10:A",
        expectedNotificationByTick = mapOf(
            Tick(1) to Cell.IntermediateUpdateNotification(
                updatedValue = "10:B",
            ),
        ),
    )

    @Test
    fun test_state_bothSourcesDynamic_bothSourcesUpdateSimultaneously() = testCell_initiallyDynamic(
        setup = {
            val sourceCell1 = createDynamicCellExternally(
                givenInitialValue = 10,
                givenUpdateByTick = mapOf(
                    Tick(1) to GivenPlainUpdate.of(20),
                ),
                freezeTick = null,
            )

            val sourceCell2 = createDynamicCellExternally(
                givenInitialValue = 'A',
                givenUpdateByTick = mapOf(
                    Tick(1) to GivenPlainUpdate.of('B'),
                ),
                freezeTick = null,
            )

            Cell.map2(
                sourceCell1,
                sourceCell2,
            ) { value1, value2 ->
                "$value1:$value2"
            }
        },
        expectedInitialValue = "10:A",
        expectedNotificationByTick = mapOf(
            Tick(1) to Cell.IntermediateUpdateNotification(
                updatedValue = "20:B",
            ),
        ),
    )

    @Test
    fun test_state_firstSourceInertSecondSourceDynamic_secondSourceFreezes() {
        fun test(
            source1CellFactory: InertCellFactory,
        ) = testCell_initiallyDynamic(
            setup = {
                val sourceCell1 = source1CellFactory.createInertExternally(
                    inertValue = 10,
                )

                val sourceCell2 = createDynamicCellExternally(
                    givenInitialValue = 'A',
                    givenUpdateByTick = mapOf(
                        Tick(1) to GivenPlainUpdate.of(
                            givenUpdatedValue = 'B',
                        ),
                    ),
                    freezeTick = Tick(2),
                )

                Cell.map2(
                    sourceCell1,
                    sourceCell2,
                ) { value1, value2 ->
                    "$value1:$value2"
                }
            },
            expectedInitialValue = "10:A",
            expectedNotificationByTick = mapOf(
                Tick(1) to Cell.IntermediateUpdateNotification(
                    updatedValue = "10:B",
                ),
                Tick(2) to Cell.IsolatedFreezeNotification,
            ),
        )

        InertCellFactory.values.forEach { source1CellFactory ->
            test(
                source1CellFactory = source1CellFactory,
            )
        }
    }

    @Test
    fun test_state_firstSourceDynamicSecondSourceInert_firstSourceFreezes() {
        fun test(
            source2CellFactory: InertCellFactory,
        ) = testCell_initiallyDynamic(
            setup = {
                val sourceCell1 = createDynamicCellExternally(
                    givenInitialValue = 10,
                    givenUpdateByTick = mapOf(
                        Tick(1) to GivenPlainUpdate.of(
                            givenUpdatedValue = 20,
                        ),
                        Tick(2) to GivenPlainUpdate.of(
                            givenUpdatedValue = 30,
                        ),
                    ),
                    freezeTick = Tick(2),
                )

                val sourceCell2 = source2CellFactory.createInertExternally(
                    inertValue = 'A',
                )

                Cell.map2(
                    sourceCell1,
                    sourceCell2,
                ) { value1, value2 ->
                    "$value1:$value2"
                }
            },
            expectedInitialValue = "10:A",
            expectedNotificationByTick = mapOf(
                Tick(1) to Cell.IntermediateUpdateNotification(
                    updatedValue = "20:A",
                ),
                Tick(2) to Cell.FreezingUpdateNotification(
                    updatedFrozenValue = "30:A",
                ),
            ),
        )

        InertCellFactory.values.forEach { source2CellFactory ->
            test(
                source2CellFactory = source2CellFactory,
            )
        }
    }

    @Test
    fun test_state_bothSourcesDynamic_firstSourceJustFreezesLast() = testCell_initiallyDynamic(
        setup = {
            val sourceCell1 = createDynamicCellExternally(
                givenInitialValue = 10,
                givenUpdateByTick = mapOf(
                    Tick(2) to GivenPlainUpdate.of(
                        givenUpdatedValue = 20,
                    ),
                ),
                freezeTick = Tick(3),
            )

            val sourceCell2 = createDynamicCellExternally(
                givenInitialValue = 'A',
                givenUpdateByTick = mapOf(
                    Tick(1) to GivenPlainUpdate.of(
                        givenUpdatedValue = 'B',
                    ),
                ),
                freezeTick = Tick(2),
            )

            Cell.map2(
                sourceCell1,
                sourceCell2,
            ) { value1, value2 ->
                "$value1:$value2"
            }
        },
        expectedInitialValue = "10:A",
        expectedNotificationByTick = mapOf(
            Tick(1) to Cell.IntermediateUpdateNotification(
                updatedValue = "10:B",
            ),
            Tick(2) to Cell.IntermediateUpdateNotification(
                updatedValue = "20:B",
            ),
            Tick(3) to Cell.IsolatedFreezeNotification,
        ),
    )

    @Test
    fun test_state_bothSourcesDynamic_firstSourceUpdatesFreezingLast() = testCell_initiallyDynamic(
        setup = {
            val sourceCell1 = createDynamicCellExternally(
                givenInitialValue = 10,
                givenUpdateByTick = mapOf(
                    Tick(2) to GivenPlainUpdate.of(20),
                    Tick(3) to GivenPlainUpdate.of(30),
                ),
                freezeTick = Tick(3),
            )

            val sourceCell2 = createDynamicCellExternally(
                givenInitialValue = 'A',
                givenUpdateByTick = mapOf(
                    Tick(1) to GivenPlainUpdate.of('B'),
                ),
                freezeTick = Tick(2),
            )

            Cell.map2(
                sourceCell1,
                sourceCell2,
            ) { value1, value2 ->
                "$value1:$value2"
            }
        },
        expectedInitialValue = "10:A",
        expectedNotificationByTick = mapOf(
            Tick(1) to Cell.IntermediateUpdateNotification(
                updatedValue = "10:B",
            ),
            Tick(2) to Cell.IntermediateUpdateNotification(
                updatedValue = "20:B",
            ),
            Tick(3) to Cell.FreezingUpdateNotification(
                updatedFrozenValue = "30:B",
            ),
        ),
    )

    @Test
    fun test_state_bothSourcesDynamic_secondSourceJustFreezesLast() = testCell_initiallyDynamic(
        setup = {
            val sourceCell1 = createDynamicCellExternally(
                givenInitialValue = 10,
                givenUpdateByTick = mapOf(
                    Tick(1) to GivenPlainUpdate.of(20),
                ),
                freezeTick = Tick(2),
            )

            val sourceCell2 = createDynamicCellExternally(
                givenInitialValue = 'A',
                givenUpdateByTick = mapOf(
                    Tick(2) to GivenPlainUpdate.of('B'),
                    Tick(3) to GivenPlainUpdate.of('C'),
                ),
                freezeTick = Tick(3),
            )

            Cell.map2(
                sourceCell1,
                sourceCell2,
            ) { value1, value2 ->
                "$value1:$value2"
            }
        },
        expectedInitialValue = "10:A",
        expectedNotificationByTick = mapOf(
            Tick(1) to Cell.IntermediateUpdateNotification(
                updatedValue = "20:A",
            ),
            Tick(2) to Cell.IntermediateUpdateNotification(
                updatedValue = "20:B",
            ),
            Tick(3) to Cell.IsolatedFreezeNotification,
        ),
    )

    @Test
    fun test_state_bothSourcesDynamic_secondSourceUpdatesFreezingLast() = testCell_initiallyDynamic(
        setup = {
            val sourceCell1 = createDynamicCellExternally(
                givenInitialValue = 10,
                givenUpdateByTick = mapOf(
                    Tick(1) to GivenPlainUpdate.of(20),
                ),
                freezeTick = Tick(2),
            )

            val sourceCell2 = createDynamicCellExternally(
                givenInitialValue = 'A',
                givenUpdateByTick = mapOf(
                    Tick(2) to GivenPlainUpdate.of('B'),
                    Tick(3) to GivenPlainUpdate.of('C'),
                ),
                freezeTick = Tick(3),
            )

            Cell.map2(
                sourceCell1,
                sourceCell2,
            ) { value1, value2 ->
                "$value1:$value2"
            }
        },
        expectedInitialValue = "10:A",
        expectedNotificationByTick = mapOf(
            Tick(1) to Cell.IntermediateUpdateNotification(
                updatedValue = "20:A",
            ),
            Tick(2) to Cell.IntermediateUpdateNotification(
                updatedValue = "20:B",
            ),
            Tick(3) to Cell.FreezingUpdateNotification(
                updatedFrozenValue = "20:C",
            ),
        ),
    )

    @Test
    fun test_state_bothSourcesDynamic_bothSourcesJustFreezeSimultaneously() = testCell_initiallyDynamic(
        setup = {
            val sourceCell1 = createDynamicCellExternally(
                givenInitialValue = 10,
                givenUpdateByTick = mapOf(
                    Tick(1) to GivenPlainUpdate.of(20),
                ),
                freezeTick = Tick(3),
            )

            val sourceCell2 = createDynamicCellExternally(
                givenInitialValue = 'A',
                givenUpdateByTick = mapOf(
                    Tick(2) to GivenPlainUpdate.of('B'),
                ),
                freezeTick = Tick(3),
            )

            Cell.map2(
                sourceCell1,
                sourceCell2,
            ) { value1, value2 ->
                "$value1:$value2"
            }
        },
        expectedInitialValue = "10:A",
        expectedNotificationByTick = mapOf(
            Tick(1) to Cell.IntermediateUpdateNotification(
                updatedValue = "20:A",
            ),
            Tick(2) to Cell.IntermediateUpdateNotification(
                updatedValue = "20:B",
            ),
            Tick(3) to Cell.IsolatedFreezeNotification,
        ),
    )

    @Test
    fun test_state_bothSourcesDynamic_bothSourcesUpdateFreezingSimultaneously() = testCell_initiallyDynamic(
        setup = {
            val sourceCell1 = createDynamicCellExternally(
                givenInitialValue = 10,
                givenUpdateByTick = mapOf(
                    Tick(1) to GivenPlainUpdate.of(20),
                    Tick(3) to GivenPlainUpdate.of(30),
                ),
                freezeTick = Tick(3),
            )

            val sourceCell2 = createDynamicCellExternally(
                givenInitialValue = 'A',
                givenUpdateByTick = mapOf(
                    Tick(2) to GivenPlainUpdate.of('B'),
                    Tick(3) to GivenPlainUpdate.of('C'),
                ),
                freezeTick = Tick(3),
            )

            Cell.map2(
                sourceCell1,
                sourceCell2,
            ) { value1, value2 ->
                "$value1:$value2"
            }
        },
        // USE THIS AS AN EXAMPLE
        expectedInitialValue = "10:A",
        expectedNotificationByTick = mapOf(
            Tick(1) to Cell.IntermediateUpdateNotification(
                updatedValue = "20:A",
            ),
            Tick(2) to Cell.IntermediateUpdateNotification(
                updatedValue = "20:B",
            ),
            Tick(3) to Cell.FreezingUpdateNotification(
                updatedFrozenValue = "30:C",
            ),
        ),
    )

    @Test
    fun test_state_bothSourcesDynamic_firstSourceUpdatesFreezingSecondJustFreezesSimultaneously() =
        testCell_initiallyDynamic(
            setup = {
                val sourceCell1 = createDynamicCellExternally(
                    givenInitialValue = 10,
                    givenUpdateByTick = mapOf(
                        Tick(1) to GivenPlainUpdate.of(20),
                        Tick(3) to GivenPlainUpdate.of(30),
                    ),
                    freezeTick = Tick(3),
                )

                val sourceCell2 = createDynamicCellExternally(
                    givenInitialValue = 'A',
                    givenUpdateByTick = mapOf(
                        Tick(2) to GivenPlainUpdate.of('B'),
                    ),
                    freezeTick = Tick(3),
                )

                Cell.map2(
                    sourceCell1,
                    sourceCell2,
                ) { value1, value2 ->
                    "$value1:$value2"
                }
            },
            expectedInitialValue = "10:A",
            expectedNotificationByTick = mapOf(
                Tick(1) to Cell.IntermediateUpdateNotification(
                    updatedValue = "20:A",
                ),
                Tick(2) to Cell.IntermediateUpdateNotification(
                    updatedValue = "20:B",
                ),
                Tick(3) to Cell.FreezingUpdateNotification(
                    updatedFrozenValue = "30:B",
                ),
            ),
        )

    @Test
    fun test_state_bothSourcesDynamic_firstSourceJustFreezesSecondUpdatesFreezingSimultaneously() =
        testCell_initiallyDynamic(
            setup = {
                val sourceCell1 = createDynamicCellExternally(
                    givenInitialValue = 10,
                    givenUpdateByTick = mapOf(
                        Tick(1) to GivenPlainUpdate.of(
                            givenUpdatedValue = 20,
                        ),
                    ),
                    freezeTick = Tick(3),
                )

                val sourceCell2 = createDynamicCellExternally(
                    givenInitialValue = 'A',
                    givenUpdateByTick = mapOf(
                        Tick(2) to GivenPlainUpdate.of(
                            givenUpdatedValue = 'B',
                        ),
                        Tick(3) to GivenPlainUpdate.of(
                            givenUpdatedValue = 'C',
                        ),
                    ),
                    freezeTick = Tick(3),
                )

                Cell.map2(
                    sourceCell1,
                    sourceCell2,
                ) { value1, value2 ->
                    "$value1:$value2"
                }
            },
            expectedInitialValue = "10:A",
            expectedNotificationByTick = mapOf(
                Tick(1) to Cell.IntermediateUpdateNotification(
                    updatedValue = "20:A",
                ),
                Tick(2) to Cell.IntermediateUpdateNotification(
                    updatedValue = "20:B",
                ),
                Tick(3) to Cell.FreezingUpdateNotification(
                    updatedFrozenValue = "20:C",
                ),
            ),
        )

    @Test
    @Ignore // FIXME: Vertex (...) is already a dependent (...)
    fun test_state_sameSourceInert() {
        fun test(
            sourceCellFactory: InertCellFactory,
        ) = testCell_immediatelyInert(
            setup = {
                val sourceCell = sourceCellFactory.createInertExternally(
                    inertValue = 10,
                )

                Cell.map2(
                    sourceCell,
                    sourceCell,
                ) { value1, value2 ->
                    "$value1:$value2"
                }
            },
            expectedValue = "10:10",
        )

        InertCellFactory.values.forEach { sourceCellFactory ->
            test(
                sourceCellFactory = sourceCellFactory,
            )
        }
    }

    @Test
    @Ignore // FIXME: Vertex (...) is already a dependent (...)
    fun test_state_sameSourceDynamic() = testCell_initiallyDynamic(
        setup = {
            val sourceCell = createDynamicCellExternally(
                givenInitialValue = 10,
                givenUpdateByTick = mapOf(
                    Tick(1) to GivenPlainUpdate.of(
                        givenUpdatedValue = 20,
                    ),
                ),
                freezeTick = Tick(2),
            )

            Cell.map2(
                sourceCell,
                sourceCell,
            ) { value1, value2 ->
                    "$value1:$value2"
                }
        },
        expectedInitialValue = "10:10",
        expectedNotificationByTick = mapOf(
            Tick(1) to Cell.IntermediateUpdateNotification(
                updatedValue = "20:20",
            ),
            Tick(2) to Cell.IsolatedFreezeNotification,
        ),
    )
}
