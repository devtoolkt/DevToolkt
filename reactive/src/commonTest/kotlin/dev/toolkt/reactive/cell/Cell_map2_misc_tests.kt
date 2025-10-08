package dev.toolkt.reactive.cell

import dev.toolkt.reactive.cell.test_utils.CellVerificationStrategy
import dev.toolkt.reactive.cell.test_utils.DynamicCellFactory
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.map
import dev.toolkt.reactive.event_stream.mapNotNull
import kotlin.test.Test

@Suppress("ClassName")
class Cell_map2_misc_tests {
    private fun test_allFilteredOut(
        sourceCell1Factory: DynamicCellFactory,
        sourceCell2Factory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceCell1 = sourceCell1Factory.createDynamicExternally(
            initialValue = 10,
            doUpdate = doTrigger.mapNotNull { null },
        )

        val sourceCell2 = sourceCell2Factory.createDynamicExternally(
            initialValue = 'A',
            doUpdate = doTrigger.mapNotNull { null },
        )

        val map2Cell = Cell.map2(
            sourceCell1,
            sourceCell2,
        ) { value1, value2 ->
            "$value1:$value2"
        }

        val verifier = verificationStrategy.begin(
            subjectCell = map2Cell,
        )

        verifier.verifyDoesNotUpdate(
            doTriggerPotentialUpdate = doTrigger,
            expectedNonUpdatedValue = "10:A",
        )
    }

    private fun test_allFilteredOut(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { sourceCell1Factory ->
            DynamicCellFactory.values.forEach { sourceCell2Factory ->
                test_allFilteredOut(
                    sourceCell1Factory = sourceCell1Factory,
                    sourceCell2Factory = sourceCell2Factory,
                    verificationStrategy = verificationStrategy,
                )
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

        val map2Cell = Cell.map2(
            sourceCell1,
            sourceCell2,
        ) { value1, value2 ->
            "$value1:$value2"
        }

        verificationStrategy.verifyDeactivation(
            subjectCell = map2Cell,
            doTrigger = doTrigger,
        )
    }

    @Test
    fun test_deactivation() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            DynamicCellFactory.values.forEach { source1CellFactory ->
                DynamicCellFactory.values.forEach { source2CellFactory ->
                    test_deactivation(
                        source1CellFactory = source1CellFactory,
                        source2CellFactory = source2CellFactory,
                        verificationStrategy = verificationStrategy,
                    )
                }
            }
        }
    }
}
