package dev.toolkt.reactive.cell


import dev.toolkt.core.utils.iterable.mapOfNotNull
import dev.toolkt.reactive.cell.test_utils.InertCellFactory
import dev.toolkt.reactive.cell.test_utils.StillCellFactory
import dev.toolkt.reactive.cell.test_utils.Tick
import dev.toolkt.reactive.cell.test_utils.createDynamicCellExternally
import dev.toolkt.reactive.cell.test_utils.testCell_immediatelyInert
import dev.toolkt.reactive.cell.test_utils.testCell_initiallyDynamic
import kotlin.test.Ignore
import kotlin.test.Test

@Suppress("ClassName")
class Cell_map3_state_tests {
    private sealed class SourceKind {
        data class Inert(
            val inertCellFactory: InertCellFactory,
        ) : SourceKind()

        data object Dynamic : SourceKind()
    }

    @Test
    @Ignore // FIXME: Expected a null subscription for an inert cell
    fun test_allSourcesInert() {
        fun test(
            source1CellFactory: InertCellFactory,
            source2CellFactory: InertCellFactory,
            source3CellFactory: InertCellFactory,
        ) = testCell_immediatelyInert(
            setup = {
                val sourceCell1 = source1CellFactory.createInertExternally(
                    inertValue = 10,
                )

                val sourceCell2 = source2CellFactory.createInertExternally(
                    inertValue = 'A',
                )

                val sourceCell3 = source3CellFactory.createInertExternally(
                    inertValue = true,
                )

                Cell.map3(
                    sourceCell1,
                    sourceCell2,
                    sourceCell3,
                ) { value1, value2, value3 ->
                    "$value1:$value2:$value3"
                }

            },
            expectedValue = "10:A:true",
        )

        InertCellFactory.values.forEach { source1CellFactory ->
            InertCellFactory.values.forEach { source2CellFactory ->
                InertCellFactory.values.forEach { source3CellFactory ->
                    test(
                        source1CellFactory = source1CellFactory,
                        source2CellFactory = source2CellFactory,
                        source3CellFactory = source3CellFactory,
                    )
                }
            }
        }
    }

    @Test
    fun test_someSourcesDynamic() {
        fun test(
            source1CellFactory: StillCellFactory,
            source2CellFactory: StillCellFactory,
            source3CellFactory: StillCellFactory,
        ) = testCell_initiallyDynamic(
            setup = {
                val sourceCell1 = source1CellFactory.createStillExternally(
                    stillValue = 10,
                )

                val sourceCell2 = source2CellFactory.createStillExternally(
                    stillValue = 'A',
                )

                val sourceCell3 = source3CellFactory.createStillExternally(
                    stillValue = true,
                )

                Cell.map3(
                    sourceCell1,
                    sourceCell2,
                    sourceCell3,
                ) { value1, value2, value3 ->
                    "$value1:$value2:$value3"
                }

            },
            expectedInitialValue = "10:A:true",
            expectedNotificationByTick = emptyMap(),
        )

        // 1 dynamic

        test(
            source1CellFactory = StillCellFactory.Dynamic,
            source2CellFactory = InertCellFactory.Const,
            source3CellFactory = InertCellFactory.Frozen,
        )

        test(
            source1CellFactory = InertCellFactory.Const,
            source2CellFactory = StillCellFactory.Dynamic,
            source3CellFactory = InertCellFactory.Frozen,
        )

        test(
            source1CellFactory = InertCellFactory.Const,
            source2CellFactory = InertCellFactory.TransformedFrozen,
            source3CellFactory = StillCellFactory.Dynamic,
        )

        // 2 dynamic

        test(
            source1CellFactory = StillCellFactory.Dynamic,
            source2CellFactory = StillCellFactory.Dynamic,
            source3CellFactory = InertCellFactory.Const,
        )

        test(
            source1CellFactory = StillCellFactory.Dynamic,
            source2CellFactory = InertCellFactory.Const,
            source3CellFactory = StillCellFactory.Dynamic,
        )

        test(
            source1CellFactory = InertCellFactory.Const,
            source2CellFactory = StillCellFactory.Dynamic,
            source3CellFactory = StillCellFactory.Dynamic,
        )

        // 3 dynamic

        test(
            source1CellFactory = StillCellFactory.Dynamic,
            source2CellFactory = StillCellFactory.Dynamic,
            source3CellFactory = StillCellFactory.Dynamic,
        )
    }

    @Test
    fun test_sameSourceInert() {
        fun test(
            sourceCellFactory: InertCellFactory,
        ) {
            testCell_immediatelyInert(
                setup = {
                    val sourceCell = sourceCellFactory.createInertExternally(
                        inertValue = 10,
                    )

                    Cell.map3(
                        sourceCell,
                        sourceCell,
                        sourceCell,
                    ) { value1, value2, value3 ->
                        "$value1:$value2:$value3"
                    }

                },
                expectedValue = "10:10:10",
            )
        }

        test(
            sourceCellFactory = InertCellFactory.Const,
        )
    }

    @Test
    @Ignore // FIXME: Vertex (...) is already a dependent (...)
    fun test_sameSourceDynamic() {
        testCell_initiallyDynamic(
            setup = {
                val sourceCell = createDynamicCellExternally(
                    givenInitialValue = 10,
                    givenUpdatedValueByTick = emptyMap(),
                    freezeTick = null,
                )

                Cell.map3(
                    sourceCell,
                    sourceCell,
                    sourceCell,
                ) { value1, value2, value3 ->
                    "$value1:$value2:$value3"
                }

            },
            expectedInitialValue = "10:10:10",
            expectedNotificationByTick = emptyMap(),
        )
    }

    @Test
    fun test_source1Dynamic_source1Updates() {
        fun test(
            source2CellFactory: StillCellFactory,
            source3CellFactory: StillCellFactory,
        ) = testCell_initiallyDynamic(
            setup = {
                val sourceCell1 = createDynamicCellExternally(
                    givenInitialValue = 10,
                    givenUpdatedValueByTick = mapOf(
                        Tick(1) to 20,
                    ),
                    freezeTick = null,
                )

                val sourceCell2 = source2CellFactory.createStillExternally(
                    stillValue = 'A',
                )

                val sourceCell3 = source3CellFactory.createStillExternally(
                    stillValue = false,
                )

                Cell.map3(
                    sourceCell1,
                    sourceCell2,
                    sourceCell3,
                ) { value1, value2, value3 ->
                    "$value1:$value2:$value3"
                }

            },
            expectedInitialValue = "10:A:false",
            expectedNotificationByTick = mapOf(
                Tick(1) to Cell.IntermediateUpdateNotification(
                    updatedValue = "20:A:false",
                ),
            ),
        )

        test(
            source2CellFactory = InertCellFactory.Const,
            source3CellFactory = InertCellFactory.Frozen,
        )

        test(
            source2CellFactory = InertCellFactory.Const,
            source3CellFactory = StillCellFactory.Dynamic,
        )

        test(
            source2CellFactory = StillCellFactory.Dynamic,
            source3CellFactory = InertCellFactory.Frozen,
        )

        test(
            source2CellFactory = StillCellFactory.Dynamic,
            source3CellFactory = StillCellFactory.Dynamic,
        )
    }

    @Test
    fun test_source2Dynamic_source2Updates() {
        fun test(
            source1CellFactory: StillCellFactory,
            source3CellFactory: StillCellFactory,
        ) = testCell_initiallyDynamic(
            setup = {
                val sourceCell1 = source1CellFactory.createStillExternally(
                    stillValue = 10,
                )

                val sourceCell2 = createDynamicCellExternally(
                    givenInitialValue = 'A',
                    givenUpdatedValueByTick = mapOf(
                        Tick(2) to 'B',
                    ),
                    freezeTick = null,
                )

                val sourceCell3 = source3CellFactory.createStillExternally(
                    stillValue = false,
                )

                Cell.map3(
                    sourceCell1,
                    sourceCell2,
                    sourceCell3,
                ) { value1, value2, value3 ->
                    "$value1:$value2:$value3"
                }

            },
            expectedInitialValue = "10:A:false",
            expectedNotificationByTick = mapOf(
                Tick(2) to Cell.IntermediateUpdateNotification(
                    updatedValue = "10:B:false",
                ),
            ),
        )

        test(
            source1CellFactory = InertCellFactory.Const,
            source3CellFactory = InertCellFactory.Frozen,
        )

        test(
            source1CellFactory = InertCellFactory.Const,
            source3CellFactory = StillCellFactory.Dynamic,
        )

        test(
            source1CellFactory = StillCellFactory.Dynamic,
            source3CellFactory = InertCellFactory.Frozen,
        )

        test(
            source1CellFactory = StillCellFactory.Dynamic,
            source3CellFactory = StillCellFactory.Dynamic,
        )
    }

    @Test
    fun test_source3Dynamic_source3Updates() {
        fun test(
            source1CellFactory: StillCellFactory,
            source2CellFactory: StillCellFactory,
        ) = testCell_initiallyDynamic(
            setup = {
                val sourceCell1 = source1CellFactory.createStillExternally(
                    stillValue = 10,
                )

                val sourceCell2 = source2CellFactory.createStillExternally(
                    stillValue = 'A',
                )

                val sourceCell3 = createDynamicCellExternally(
                    givenInitialValue = false,
                    givenUpdatedValueByTick = mapOf(
                        Tick(3) to true,
                    ),
                    freezeTick = null,
                )

                Cell.map3(
                    sourceCell1,
                    sourceCell2,
                    sourceCell3,
                ) { value1, value2, value3 ->
                    "$value1:$value2:$value3"
                }

            },
            expectedInitialValue = "10:A:false",
            expectedNotificationByTick = mapOf(
                Tick(3) to Cell.IntermediateUpdateNotification(
                    updatedValue = "10:A:true",
                ),
            ),
        )

        test(
            source1CellFactory = InertCellFactory.Const,
            source2CellFactory = InertCellFactory.Frozen,
        )

        test(
            source1CellFactory = InertCellFactory.Const,
            source2CellFactory = StillCellFactory.Dynamic,
        )

        test(
            source1CellFactory = StillCellFactory.Dynamic,
            source2CellFactory = InertCellFactory.Frozen,
        )

        test(
            source1CellFactory = StillCellFactory.Dynamic,
            source2CellFactory = StillCellFactory.Dynamic,
        )
    }

    @Test
    fun test_source1Inert_dynamicSourcesFreeze() {
        fun test(
            source1CellFactory: InertCellFactory,
        ) = testCell_initiallyDynamic(
            setup = {
                val sourceCell1 = source1CellFactory.createInertExternally(
                    inertValue = 10,
                )

                val sourceCell2 = createDynamicCellExternally(
                    givenInitialValue = 'A',
                    givenUpdatedValueByTick = emptyMap(),
                    freezeTick = Tick(1),
                )

                val sourceCell3 = createDynamicCellExternally(
                    givenInitialValue = false,
                    givenUpdatedValueByTick = emptyMap(),
                    freezeTick = Tick(2),
                )

                Cell.map3(
                    sourceCell1,
                    sourceCell2,
                    sourceCell3,
                ) { value1, value2, value3 ->
                    "$value1:$value2:$value3"
                }

            },
            expectedInitialValue = "10:A:false",
            expectedNotificationByTick = mapOf(
                Tick(3) to Cell.IsolatedFreezeNotification,
            ),
        )

        test(
            source1CellFactory = InertCellFactory.Const,
        )

        test(
            source1CellFactory = InertCellFactory.Frozen,
        )
    }

    @Test
    fun test_source2Inert_dynamicSourcesFreeze() {
        fun test(
            source1CellFactory: InertCellFactory,
        ) = testCell_initiallyDynamic(
            setup = {
                val sourceCell1 = createDynamicCellExternally(
                    givenInitialValue = 10,
                    givenUpdatedValueByTick = mapOf(
                        Tick(1) to 11,
                    ),
                    freezeTick = Tick(1),
                )

                val sourceCell2 = source1CellFactory.createInertExternally(
                    inertValue = 'A',
                )

                val sourceCell3 = createDynamicCellExternally(
                    givenInitialValue = false,
                    givenUpdatedValueByTick = mapOf(
                        Tick(1) to true,
                    ),
                    freezeTick = Tick(1),
                )

                Cell.map3(
                    sourceCell1,
                    sourceCell2,
                    sourceCell3,
                ) { value1, value2, value3 ->
                    "$value1:$value2:$value3"
                }

            },
            expectedInitialValue = "10:A:false",
            expectedNotificationByTick = mapOf(
                Tick(1) to Cell.IsolatedFreezeNotification,
            ),
        )

        test(
            source1CellFactory = InertCellFactory.Const,
        )

        test(
            source1CellFactory = InertCellFactory.Frozen,
        )
    }

    @Test
    fun test_source3Inert_dynamicSourcesFreeze() {
        fun test(
            source1CellFactory: InertCellFactory,
        ) = testCell_initiallyDynamic(
            setup = {
                val sourceCell1 = createDynamicCellExternally(
                    givenInitialValue = 10,
                    givenUpdatedValueByTick = mapOf(
                        Tick(1) to 11,
                    ),
                    freezeTick = Tick(1),
                )

                val sourceCell2 = createDynamicCellExternally(
                    givenInitialValue = 'A',
                    givenUpdatedValueByTick = emptyMap(),
                    freezeTick = Tick(2),
                )

                val sourceCell3 = source1CellFactory.createInertExternally(
                    inertValue = false,
                )

                Cell.map3(
                    sourceCell1,
                    sourceCell2,
                    sourceCell3,
                ) { value1, value2, value3 ->
                    "$value1:$value2:$value3"
                }

            },
            expectedInitialValue = "10:A:false",
            expectedNotificationByTick = mapOf(
                Tick(1) to Cell.IntermediateUpdateNotification(
                    updatedValue = "11:A:false",
                ),
                Tick(3) to Cell.IsolatedFreezeNotification,
            ),
        )

        test(
            source1CellFactory = InertCellFactory.Const,
        )

        test(
            source1CellFactory = InertCellFactory.Frozen,
        )
    }

    @Test
    fun test_someSourcesDynamic_dynamicSourcesUpdate() {
        fun test(
            source1Kind: SourceKind,
            source2Kind: SourceKind,
            source3Kind: SourceKind,
        ) {
            val finalSource1Value = when (source1Kind) {
                is SourceKind.Dynamic -> 11
                is SourceKind.Inert -> 10
            }

            val finalSource2Value = when (source2Kind) {
                is SourceKind.Dynamic -> 'B'
                is SourceKind.Inert -> 'A'
            }

            testCell_initiallyDynamic(
                setup = {
                    val sourceCell1 = when (source1Kind) {
                        is SourceKind.Dynamic -> createDynamicCellExternally(
                            givenInitialValue = 10,
                            givenUpdatedValueByTick = mapOf(
                                Tick(1) to 11,
                            ),
                            freezeTick = null,
                        )

                        is SourceKind.Inert -> source1Kind.inertCellFactory.createInertExternally(
                            inertValue = 10,
                        )
                    }

                    val sourceCell2 = when (source2Kind) {
                        is SourceKind.Dynamic -> createDynamicCellExternally(
                            givenInitialValue = 'A',
                            givenUpdatedValueByTick = mapOf(
                                Tick(2) to 'B',
                            ),
                            freezeTick = null,
                        )

                        is SourceKind.Inert -> source2Kind.inertCellFactory.createInertExternally(
                            inertValue = 'A',
                        )
                    }

                    val sourceCell3 = when (source3Kind) {
                        is SourceKind.Dynamic -> createDynamicCellExternally(
                            givenInitialValue = false,
                            givenUpdatedValueByTick = mapOf(
                                Tick(3) to true,
                            ),
                            freezeTick = null,
                        )

                        is SourceKind.Inert -> source3Kind.inertCellFactory.createInertExternally(
                            inertValue = false,
                        )
                    }

                    Cell.map3(
                        sourceCell1,
                        sourceCell2,
                        sourceCell3,
                    ) { value1, value2, value3 ->
                        "$value1:$value2:$value3"
                    }
                },
                expectedInitialValue = "10:A:false",
                expectedNotificationByTick = mapOfNotNull(
                    (Tick(1) to Cell.IntermediateUpdateNotification(
                        updatedValue = "11:A:false",
                    )).takeIf { source1Kind is SourceKind.Dynamic },
                    (Tick(2) to Cell.IntermediateUpdateNotification(
                        updatedValue = "$finalSource1Value:B:false",
                    )).takeIf { source2Kind is SourceKind.Dynamic },
                    (Tick(3) to Cell.IntermediateUpdateNotification(
                        updatedValue = "$finalSource1Value:$finalSource2Value:true",
                    )).takeIf { source3Kind is SourceKind.Dynamic },
                ),
            )
        }

        test(
            source1Kind = SourceKind.Dynamic,
            source2Kind = SourceKind.Inert(
                inertCellFactory = InertCellFactory.Const,
            ),
            source3Kind = SourceKind.Inert(
                inertCellFactory = InertCellFactory.Frozen,
            ),
        )

        test(
            source1Kind = SourceKind.Inert(
                inertCellFactory = InertCellFactory.Const,
            ),
            source2Kind = SourceKind.Dynamic,
            source3Kind = SourceKind.Inert(
                inertCellFactory = InertCellFactory.Frozen,
            ),
        )

        test(
            source1Kind = SourceKind.Inert(
                inertCellFactory = InertCellFactory.Const,
            ),
            source2Kind = SourceKind.Inert(
                inertCellFactory = InertCellFactory.Frozen,
            ),
            source3Kind = SourceKind.Dynamic,
        )

        test(
            source1Kind = SourceKind.Dynamic,
            source2Kind = SourceKind.Dynamic,
            source3Kind = SourceKind.Inert(
                inertCellFactory = InertCellFactory.Const,
            ),
        )

        test(
            source1Kind = SourceKind.Dynamic,
            source2Kind = SourceKind.Inert(
                inertCellFactory = InertCellFactory.Frozen,
            ),
            source3Kind = SourceKind.Dynamic,
        )

        test(
            source1Kind = SourceKind.Inert(
                inertCellFactory = InertCellFactory.Const,
            ),
            source2Kind = SourceKind.Dynamic,
            source3Kind = SourceKind.Dynamic,
        )

        test(
            source1Kind = SourceKind.Dynamic,
            source2Kind = SourceKind.Dynamic,
            source3Kind = SourceKind.Dynamic,
        )
    }

    @Test
    fun test_someSourcesDynamic_dynamicSourcesFreeze() {
        fun test(
            source1Kind: SourceKind,
            source2Kind: SourceKind,
            source3Kind: SourceKind,
        ) {
            val freezeTick = when (source3Kind) {
                is SourceKind.Dynamic -> Tick(3)

                is SourceKind.Inert -> when (source2Kind) {
                    is SourceKind.Dynamic -> Tick(2)

                    is SourceKind.Inert -> when (source1Kind) {
                        is SourceKind.Dynamic -> Tick(1)

                        is SourceKind.Inert -> null
                    }
                }
            }

            testCell_initiallyDynamic(
                setup = {
                    val sourceCell1 = when (source1Kind) {
                        is SourceKind.Dynamic -> createDynamicCellExternally(
                            givenInitialValue = 10,
                            givenUpdatedValueByTick = emptyMap(),
                            freezeTick = Tick(1),
                        )

                        is SourceKind.Inert -> source1Kind.inertCellFactory.createInertExternally(
                            inertValue = 10,
                        )
                    }

                    val sourceCell2 = when (source2Kind) {
                        is SourceKind.Dynamic -> createDynamicCellExternally(
                            givenInitialValue = 'A',
                            givenUpdatedValueByTick = emptyMap(),
                            freezeTick = Tick(2),
                        )

                        is SourceKind.Inert -> source2Kind.inertCellFactory.createInertExternally(
                            inertValue = 'A',
                        )
                    }

                    val sourceCell3 = when (source3Kind) {
                        is SourceKind.Dynamic -> createDynamicCellExternally(
                            givenInitialValue = false,
                            givenUpdatedValueByTick = emptyMap(),
                            freezeTick = Tick(3),
                        )

                        is SourceKind.Inert -> source3Kind.inertCellFactory.createInertExternally(
                            inertValue = false,
                        )
                    }

                    Cell.map3(
                        sourceCell1,
                        sourceCell2,
                        sourceCell3,
                    ) { value1, value2, value3 ->
                        "$value1:$value2:$value3"
                    }
                },
                expectedInitialValue = "10:A:false",
                expectedNotificationByTick = mapOfNotNull(
                    freezeTick?.let {
                        it to Cell.IsolatedFreezeNotification
                    },
                ),
            )
        }

        test(
            source1Kind = SourceKind.Dynamic,
            source2Kind = SourceKind.Inert(
                inertCellFactory = InertCellFactory.Const,
            ),
            source3Kind = SourceKind.Inert(
                inertCellFactory = InertCellFactory.Frozen,
            ),
        )

        test(
            source1Kind = SourceKind.Inert(
                inertCellFactory = InertCellFactory.Const,
            ),
            source2Kind = SourceKind.Dynamic,
            source3Kind = SourceKind.Inert(
                inertCellFactory = InertCellFactory.Frozen,
            ),
        )

        test(
            source1Kind = SourceKind.Inert(
                inertCellFactory = InertCellFactory.Const,
            ),
            source2Kind = SourceKind.Inert(
                inertCellFactory = InertCellFactory.Frozen,
            ),
            source3Kind = SourceKind.Dynamic,
        )

        test(
            source1Kind = SourceKind.Dynamic,
            source2Kind = SourceKind.Dynamic,
            source3Kind = SourceKind.Inert(
                inertCellFactory = InertCellFactory.Const,
            ),
        )

        test(
            source1Kind = SourceKind.Dynamic,
            source2Kind = SourceKind.Inert(
                inertCellFactory = InertCellFactory.Frozen,
            ),
            source3Kind = SourceKind.Dynamic,
        )

        test(
            source1Kind = SourceKind.Inert(
                inertCellFactory = InertCellFactory.Const,
            ),
            source2Kind = SourceKind.Dynamic,
            source3Kind = SourceKind.Dynamic,
        )

        test(
            source1Kind = SourceKind.Dynamic,
            source2Kind = SourceKind.Dynamic,
            source3Kind = SourceKind.Dynamic,
        )
    }

    @Test
    fun test_allSourcesDynamic_allSourcesFreezeSimultaneously() {
        fun test(
            shouldSource1UpdateSimultaneously: Boolean,
            shouldSource2UpdateSimultaneously: Boolean,
            shouldSource3UpdateSimultaneously: Boolean,
        ) {
            val finalSource1Value = when {
                shouldSource1UpdateSimultaneously -> 11
                else -> 10
            }

            val finalSource2Value = when {
                shouldSource2UpdateSimultaneously -> 'B'
                else -> 'A'
            }

            val finalSource3Value = when {
                shouldSource3UpdateSimultaneously -> true
                else -> false
            }

            val expectedUpdatedValue = when {
                shouldSource1UpdateSimultaneously || shouldSource2UpdateSimultaneously || shouldSource3UpdateSimultaneously -> "$finalSource1Value:$finalSource2Value:$finalSource3Value"
                else -> null
            }

            testCell_initiallyDynamic(
                setup = {
                    val sourceCell1 = createDynamicCellExternally(
                        givenInitialValue = 10,
                        givenUpdatedValueByTick = mapOfNotNull(
                            (Tick(1) to 11).takeIf { shouldSource1UpdateSimultaneously }),
                        freezeTick = Tick(1),
                    )

                    val sourceCell2 = createDynamicCellExternally(
                        givenInitialValue = 'A',
                        givenUpdatedValueByTick = mapOfNotNull(
                            (Tick(1) to 'B').takeIf { shouldSource2UpdateSimultaneously },
                        ),
                        freezeTick = Tick(1),
                    )

                    val sourceCell3 = createDynamicCellExternally(
                        givenInitialValue = false,
                        givenUpdatedValueByTick = mapOfNotNull(
                            (Tick(1) to true).takeIf { shouldSource3UpdateSimultaneously },
                        ),
                        freezeTick = Tick(1),
                    )

                    Cell.map3(
                        sourceCell1,
                        sourceCell2,
                        sourceCell3,
                    ) { value1, value2, value3 ->
                        "$value1:$value2:$value3"
                    }
                },
                expectedInitialValue = "10:A:false",
                expectedNotificationByTick = mapOf(
                    Tick(1) to Cell.FreezeNotification.of(
                        updatedValue = expectedUpdatedValue,
                    ),
                ),
            )
        }

        test(
            shouldSource1UpdateSimultaneously = false,
            shouldSource2UpdateSimultaneously = false,
            shouldSource3UpdateSimultaneously = false,
        )

        test(
            shouldSource1UpdateSimultaneously = true,
            shouldSource2UpdateSimultaneously = false,
            shouldSource3UpdateSimultaneously = false,
        )

        test(
            shouldSource1UpdateSimultaneously = false,
            shouldSource2UpdateSimultaneously = true,
            shouldSource3UpdateSimultaneously = false,
        )

        test(
            shouldSource1UpdateSimultaneously = false,
            shouldSource2UpdateSimultaneously = false,
            shouldSource3UpdateSimultaneously = true,
        )

        test(
            shouldSource1UpdateSimultaneously = true,
            shouldSource2UpdateSimultaneously = true,
            shouldSource3UpdateSimultaneously = true,
        )
    }
}
