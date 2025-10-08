package dev.toolkt.reactive.cell

import dev.toolkt.reactive.cell.test_utils.CellVerificationStrategy
import dev.toolkt.reactive.cell.test_utils.CellVerifier
import dev.toolkt.reactive.cell.test_utils.DynamicCellFactory
import dev.toolkt.reactive.cell.test_utils.InertCellFactory
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.map
import dev.toolkt.reactive.event_stream.mapNotNull
import kotlin.test.Ignore
import kotlin.test.Test

@Suppress("ClassName")
class Cell_map3_state_tests {
    private fun test_state_initial(
        source1CellFactory: InertCellFactory,
        source2CellFactory: InertCellFactory,
        source3CellFactory: InertCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val sourceCell1 = source1CellFactory.createInertExternally(10)

        val sourceCell2 = source2CellFactory.createInertExternally('A')

        val sourceCell3 = source3CellFactory.createInertExternally(true)

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

        verifier.verifyCurrentValue(
            expectedCurrentValue = "10:A:true",
        )
    }

    private fun test_state_initial(
        verificationStrategy: CellVerificationStrategy,
    ) {
        InertCellFactory.values.forEach { source1CellFactory ->
            InertCellFactory.values.forEach { source2CellFactory ->
                InertCellFactory.values.forEach { source3CellFactory ->
                    test_state_initial(
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
    fun test_state_initial_passive() {
        test_state_initial(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Ignore // FIXME: ... is already a dependent ...
    @Test
    fun test_state_initial_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_state_initial(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_state_sameSource(
        sourceCellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doUpdate = EmitterEventStream<Unit>()

        val sourceCell = sourceCellFactory.createDynamicExternally(
            initialValue = 10,
            doUpdate = doUpdate.map { 20 },
        )

        val map3Cell = Cell.map3(
            sourceCell,
            sourceCell,
            sourceCell,
        ) { value1, value2, value3 ->
            "$value1:$value2:$value3"
        }

        val verifier = verificationStrategy.begin(
            subjectCell = map3Cell,
        )

        verifier.verifyUpdates(
            doTriggerUpdate = doUpdate,
            expectedUpdatedValue = "20:20:20",
        )
    }

    private fun test_state_sameSource(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { sourceCellFactory ->
            test_state_sameSource(
                sourceCellFactory = sourceCellFactory,
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_state_sameSource_passive() {
        test_state_sameSource(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Ignore // FIXME: Flaky test
    @Test
    fun test_state_sameSource_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_state_sameSource(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Ignore // FIXME: Flaky test
    @Test
    fun test_state_sameSource_quick() {
        test_state_sameSource(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    private fun test_state_source1Update(
        source1CellFactory: DynamicCellFactory,
        source2CellFactory: InertCellFactory,
        source3CellFactory: InertCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doUpdate = EmitterEventStream<Unit>()

        val sourceCell1 = source1CellFactory.createDynamicExternally(
            initialValue = 10,
            doUpdate = doUpdate.map { 20 },
        )

        val sourceCell2 = source2CellFactory.createInertExternally('A')

        val sourceCell3 = source3CellFactory.createInertExternally(true)

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

        verifier.verifyUpdates(
            doTriggerUpdate = doUpdate,
            expectedUpdatedValue = "20:A:true",
        )
    }

    private fun test_state_source1Update(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { source1CellFactory ->
            InertCellFactory.values.forEach { source2CellFactory ->
                InertCellFactory.values.forEach { source3CellFactory ->
                    test_state_source1Update(
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
    fun test_state_source1Update_passive() {
        test_state_source1Update(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_state_source1Update_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_state_source1Update(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_state_source1Update_quick() {
        test_state_source1Update(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    private fun test_state_source2Update(
        source1CellFactory: InertCellFactory,
        source2CellFactory: DynamicCellFactory,
        source3CellFactory: InertCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doUpdate = EmitterEventStream<Unit>()

        val sourceCell1 = source1CellFactory.createInertExternally(10)

        val sourceCell2 = source2CellFactory.createDynamicExternally(
            initialValue = 'A',
            doUpdate = doUpdate.map { 'B' },
        )

        val sourceCell3 = source3CellFactory.createInertExternally(true)

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

        verifier.verifyUpdates(
            doTriggerUpdate = doUpdate,
            expectedUpdatedValue = "10:B:true",
        )
    }

    private fun test_state_source2Update(
        verificationStrategy: CellVerificationStrategy,
    ) {
        InertCellFactory.values.forEach { source1CellFactory ->
            DynamicCellFactory.values.forEach { source2CellFactory ->
                InertCellFactory.values.forEach { source3CellFactory ->
                    test_state_source2Update(
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
    fun test_state_source2Update_passive() {
        test_state_source2Update(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_state_source2Update_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_state_source2Update(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_state_source2Update_quick() {
        test_state_source2Update(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    private fun test_state_source3Update(
        source1CellFactory: InertCellFactory,
        source2CellFactory: InertCellFactory,
        source3CellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doUpdate = EmitterEventStream<Unit>()

        val sourceCell1 = source1CellFactory.createInertExternally(10)

        val sourceCell2 = source2CellFactory.createInertExternally('A')

        val sourceCell3 = source3CellFactory.createDynamicExternally(
            initialValue = true,
            doUpdate = doUpdate.map { false },
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

        verifier.verifyUpdates(
            doTriggerUpdate = doUpdate,
            expectedUpdatedValue = "10:A:false",
        )
    }

    private fun test_state_source3Update(
        verificationStrategy: CellVerificationStrategy,
    ) {
        InertCellFactory.values.forEach { source1CellFactory ->
            InertCellFactory.values.forEach { source2CellFactory ->
                DynamicCellFactory.values.forEach { source3CellFactory ->
                    test_state_source3Update(
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
    fun test_state_source3Update_passive() {
        test_state_source3Update(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_state_source3Update_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_state_source3Update(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_state_source3Update_quick() {
        test_state_source3Update(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    @Suppress("SameParameterValue")
    private fun test_state_mixedUpdates(
        source1CellFactory: DynamicCellFactory,
        source2CellFactory: DynamicCellFactory,
        source3CellFactory: DynamicCellFactory,
        initialSource1Value: Int,
        newSource1Value: Int?,
        initialSource2Value: Char,
        newSource2Value: Char?,
        initialSource3Value: Boolean,
        newSource3Value: Boolean?,
    ) {
        val doUpdate = EmitterEventStream<Unit>()

        val sourceCell1 = source1CellFactory.createDynamicExternally(
            initialValue = initialSource1Value,
            doUpdate = doUpdate.mapNotNull { newSource1Value },
        )

        val sourceCell2 = source2CellFactory.createDynamicExternally(
            initialValue = initialSource2Value,
            doUpdate = doUpdate.mapNotNull { newSource2Value },
        )

        val sourceCell3 = source3CellFactory.createDynamicExternally(
            initialValue = initialSource3Value,
            doUpdate = doUpdate.mapNotNull { newSource3Value },
        )

        val map3Cell = Cell.map3(
            sourceCell1,
            sourceCell2,
            sourceCell3,
        ) { value1, value2, value3 ->
            "$value1:$value2:$value3"
        }

        val verifier = CellVerifier.observeActively(
            subjectCell = map3Cell,
        )

        val expectedValue1 = newSource1Value ?: initialSource1Value
        val expectedValue2 = newSource2Value ?: initialSource2Value
        val expectedValue3 = newSource3Value ?: initialSource3Value

        verifier.verifyUpdates(
            doTriggerUpdate = doUpdate,
            expectedUpdatedValue = "$expectedValue1:$expectedValue2:$expectedValue3",
        )
    }

    @Suppress("SameParameterValue")
    private fun test_state_mixedUpdates(
        initialSource1Value: Int,
        newSource1Value: Int?,
        initialSource2Value: Char,
        newSource2Value: Char?,
        initialSource3Value: Boolean,
        newSource3Value: Boolean?,
    ) {
        DynamicCellFactory.values.forEach { source1CellFactory ->
            DynamicCellFactory.values.forEach { source2CellFactory ->
                DynamicCellFactory.values.forEach { source3CellFactory ->
                    test_state_mixedUpdates(
                        source1CellFactory = source1CellFactory,
                        source2CellFactory = source2CellFactory,
                        source3CellFactory = source3CellFactory,
                        initialSource1Value = initialSource1Value,
                        newSource1Value = newSource1Value,
                        initialSource2Value = initialSource2Value,
                        newSource2Value = newSource2Value,
                        initialSource3Value = initialSource3Value,
                        newSource3Value = newSource3Value,
                    )
                }
            }
        }
    }

    @Test
    fun test_state_mixedUpdates() {
        val initialSource1Value = 10
        val initialSource2Value = 'A'
        val initialSource3Value = true

        listOf(null, 11).forEach { newSource1Value ->
            listOf(null, 'B').forEach { newSource2Value ->
                listOf(null, false).forEach { newSource3Value ->
                    if (newSource1Value != null || newSource2Value != null || newSource3Value != null) {
                        // At least one source must update

                        test_state_mixedUpdates(
                            initialSource1Value = initialSource1Value,
                            newSource1Value = newSource1Value,
                            initialSource2Value = initialSource2Value,
                            newSource2Value = newSource2Value,
                            initialSource3Value = initialSource3Value,
                            newSource3Value = newSource3Value,
                        )
                    }
                }
            }
        }
    }
}
