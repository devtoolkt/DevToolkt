package dev.toolkt.reactive.cell

import dev.toolkt.reactive.cell.test_utils.GivenCellTimeline.GivenFreezingUpdate
import dev.toolkt.reactive.cell.test_utils.GivenCellTimeline.GivenJustFreeze
import dev.toolkt.reactive.cell.test_utils.GivenCellTimeline.GivenPlainUpdate
import dev.toolkt.reactive.cell.test_utils.InertCellFactory
import dev.toolkt.reactive.cell.test_utils.Tick
import dev.toolkt.reactive.cell.test_utils.createDynamicCellExternally
import dev.toolkt.reactive.cell.test_utils.testCell_immediatelyInert
import dev.toolkt.reactive.cell.test_utils.testCell_initiallyDynamic
import kotlin.test.Ignore
import kotlin.test.Test

@Suppress("ClassName")
class Cell_map_state_tests {
    @Test
    @Ignore // FIXME: Expected a null subscription for an inert cell
    fun test_state_sourceInert() {
        fun test(
            sourceCellFactory: InertCellFactory,
        ) = testCell_immediatelyInert(
            setup = {
                val sourceCell = sourceCellFactory.createInertExternally(
                    inertValue = 10,
                )

                sourceCell.map {
                    it.toString()
                }
            },
            expectedValue = "10",
        )

        InertCellFactory.values.forEach { source1CellFactory ->
            test(
                sourceCellFactory = source1CellFactory,
            )
        }
    }

    @Test
    fun test_state_sourceDynamic_noSourceUpdates() = testCell_initiallyDynamic(
        setup = {
            val sourceCell = createDynamicCellExternally(
                givenInitialValue = 10,
                givenNotificationByTick = emptyMap(),
            )

            sourceCell.map {
                it.toString()
            }
        },
        expectedInitialValue = "10",
        expectedNotificationByTick = emptyMap(),
    )

    @Test
    fun test_state_sourceDynamic_sourceUpdates() = testCell_initiallyDynamic(
        setup = {
            val sourceCell = createDynamicCellExternally(
                givenInitialValue = 10,
                givenNotificationByTick = mapOf(
                    Tick(1) to GivenPlainUpdate.of(
                        givenUpdatedValue = 20,
                    ),
                ),
            )

            sourceCell.map {
                it.toString()
            }
        },
        expectedInitialValue = "10",
        expectedNotificationByTick = mapOf(
            Tick(1) to Cell.IntermediateUpdateNotification(
                updatedValue = "20",
            ),
        ),
    )

    @Test
    fun test_state_sourceDynamic_sourceJustFreezes() = testCell_initiallyDynamic(
        setup = {
            val sourceCell = createDynamicCellExternally(
                givenInitialValue = 10,
                givenNotificationByTick = mapOf(
                    Tick(1) to GivenPlainUpdate.of(
                        givenUpdatedValue = 20,
                    ),
                    Tick(2) to GivenJustFreeze,
                ),
            )

            sourceCell.map {
                it.toString()
            }
        },
        expectedInitialValue = "10",
        expectedNotificationByTick = mapOf(
            Tick(1) to Cell.IntermediateUpdateNotification(
                updatedValue = "20",
            ),
            Tick(2) to Cell.IsolatedFreezeNotification,
        ),
    )

    @Test
    fun test_state_sourceDynamic_sourceUpdatesFreezing() = testCell_initiallyDynamic(
        setup = {
            val sourceCell = createDynamicCellExternally(
                givenInitialValue = 10,
                givenNotificationByTick = mapOf(
                    Tick(1) to GivenPlainUpdate.of(
                        givenUpdatedValue = 20,
                    ),
                    Tick(2) to GivenFreezingUpdate.of(
                        givenUpdatedValue = 30,
                    ),
                ),
            )

            sourceCell.map {
                it.toString()
            }
        },
        expectedInitialValue = "10",
        expectedNotificationByTick = mapOf(
            Tick(1) to Cell.IntermediateUpdateNotification(
                updatedValue = "20",
            ),
            Tick(2) to Cell.FreezingUpdateNotification(
                updatedFrozenValue = "30",
            ),
        ),
    )
}
