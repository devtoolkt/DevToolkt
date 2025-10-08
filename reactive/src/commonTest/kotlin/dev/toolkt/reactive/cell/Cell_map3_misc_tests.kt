package dev.toolkt.reactive.cell

import dev.toolkt.reactive.cell.test_utils.CellVerificationStrategy
import dev.toolkt.reactive.cell.test_utils.DynamicCellFactory
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.map
import kotlin.test.Test

@Suppress("ClassName")
class Cell_map3_misc_tests {
    private fun test_allFilteredOut(
        sourceCell1Factory: DynamicCellFactory,
        sourceCell2Factory: DynamicCellFactory,
        sourceCell3Factory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceCell1 = sourceCell1Factory.createFilteredOutExternally(
            initialValue = 10,
            doTrigger = doTrigger,
        )

        val sourceCell2 = sourceCell2Factory.createFilteredOutExternally(
            initialValue = 'A',
            doTrigger = doTrigger,
        )

        val sourceCell3 = sourceCell3Factory.createFilteredOutExternally(
            initialValue = true,
            doTrigger = doTrigger,
        )

        val map3Cell = Cell.map3(
            sourceCell1,
            sourceCell2,
            sourceCell3,
        ) { value1, value2, value3 ->
            "$value1:$value2:$value3"
        }

        val verifier = verificationStrategy.begin(
            subjectCell = map3Cell,
        )

        verifier.verifyDoesNotUpdate(
            doTriggerPotentialUpdate = doTrigger,
            expectedNonUpdatedValue = "10:A:true",
        )
    }

    private fun test_allFilteredOut(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { sourceCell1Factory ->
            DynamicCellFactory.values.forEach { sourceCell2Factory ->
                DynamicCellFactory.values.forEach { sourceCell3Factory ->
                    test_allFilteredOut(
                        sourceCell1Factory = sourceCell1Factory,
                        sourceCell2Factory = sourceCell2Factory,
                        sourceCell3Factory = sourceCell3Factory,
                        verificationStrategy = verificationStrategy,
                    )
                }
            }
        }
    }

    @Test
    fun test_allFilteredOut_passive() {
        test_allFilteredOut(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_allFilteredOut_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_allFilteredOut(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_allFilteredOut_quick() {
        test_allFilteredOut(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    private fun test_deactivation(
        source1CellFactory: DynamicCellFactory,
        source2CellFactory: DynamicCellFactory,
        source3CellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy.Active,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceCell1 = source1CellFactory.createDynamicExternally(
            initialValue = 10,
            doUpdate = doTrigger.map { 11 },
        )

        val sourceCell2 = source2CellFactory.createDynamicExternally(
            initialValue = 'A',
            doUpdate = doTrigger.map { 'B' },
        )

        val sourceCell3 = source3CellFactory.createDynamicExternally(
            initialValue = true,
            doUpdate = doTrigger.map { false },
        )

        val map3Cell = Cell.map3(
            sourceCell1,
            sourceCell2,
            sourceCell3,
        ) { value1, value2, value3 ->
            "$value1:$value2:$value3"
        }

        verificationStrategy.verifyDeactivation(
            subjectCell = map3Cell,
            doTrigger = doTrigger,
        )
    }

    private fun test_deactivation(
        verificationStrategy: CellVerificationStrategy.Active,
    ) {
        DynamicCellFactory.values.forEach { source1CellFactory ->
            DynamicCellFactory.values.forEach { source2CellFactory ->
                DynamicCellFactory.values.forEach { source3CellFactory ->
                    test_deactivation(
                        source1CellFactory = source1CellFactory,
                        source2CellFactory = source2CellFactory,
                        source3CellFactory = source3CellFactory,
                        verificationStrategy = verificationStrategy,
                    )
                }
            }
        }
    }

    @Test
    fun test_deactivation() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_deactivation(
                verificationStrategy = verificationStrategy,
            )
        }
    }
}
