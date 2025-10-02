package dev.toolkt.reactive.cell

import dev.toolkt.reactive.cell.test_utils.CellVerificationStrategy
import dev.toolkt.reactive.cell.test_utils.CellVerifier
import dev.toolkt.reactive.cell.test_utils.DynamicCellFactory
import dev.toolkt.reactive.cell.test_utils.StaticCellFactory
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.map
import dev.toolkt.reactive.event_stream.mapNotNull
import kotlin.test.Ignore
import kotlin.test.Test

@Suppress("ClassName")
class Cell_map3_combo_tests {
    private fun test_initial(
        source1CellFactory: StaticCellFactory,
        source2CellFactory: StaticCellFactory,
        source3CellFactory: StaticCellFactory,
        verificationStrategy: CellVerificationStrategy.Total,
    ) {
        val sourceCell1 = source1CellFactory.createExternally(10)

        val sourceCell2 = source2CellFactory.createExternally('A')

        val sourceCell3 = source3CellFactory.createExternally(true)

        val map3Cell = Cell.map3(
            sourceCell1,
            sourceCell2,
            sourceCell3,
        ) { value1, value2, value3 ->
            "$value1:$value2:$value3"
        }

        verificationStrategy.begin(
            subjectCell = map3Cell,
        ).verifyCurrentValue(
            expectedCurrentValue = "10:A:true",
        )
    }

    private fun test_initial(
        verificationStrategy: CellVerificationStrategy.Total,
    ) {
        StaticCellFactory.values.forEach { source1CellFactory ->
            StaticCellFactory.values.forEach { source2CellFactory ->
                StaticCellFactory.values.forEach { source3CellFactory ->
                    test_initial(
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
    fun test_initial_passive() {
        test_initial(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Ignore // FIXME: ... is already a dependent ...
    @Test
    fun test_initial_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_initial(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_sameSource(
        sourceCellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doUpdate = EmitterEventStream<Unit>()

        val sourceCell = sourceCellFactory.createExternally(
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
            doTrigger = doUpdate,
            expectedUpdatedValue = "20:20:20",
        )
    }

    private fun test_sameSource(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { sourceCellFactory ->
            test_sameSource(
                sourceCellFactory = sourceCellFactory,
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_sameSource_passive() {
        test_sameSource(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Ignore // FIXME: Flaky test
    @Test
    fun test_sameSource_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_sameSource(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Ignore // FIXME: Flaky test
    @Test
    fun test_sameSource_quick() {
        test_sameSource(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    private fun test_allFilteredOut(
        sourceCell1Factory: DynamicCellFactory,
        sourceCell2Factory: DynamicCellFactory,
        sourceCell3Factory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy.Total,
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
            doTrigger = doTrigger,
            expectedNonUpdatedValue = "10:A:true",
        )
    }

    private fun test_allFilteredOut(
        verificationStrategy: CellVerificationStrategy.Total,
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

    private fun test_source1Update(
        source1CellFactory: DynamicCellFactory,
        source2CellFactory: StaticCellFactory,
        source3CellFactory: StaticCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doUpdate = EmitterEventStream<Unit>()

        val sourceCell1 = source1CellFactory.createExternally(
            initialValue = 10,
            doUpdate = doUpdate.map { 20 },
        )

        val sourceCell2 = source2CellFactory.createExternally('A')

        val sourceCell3 = source3CellFactory.createExternally(true)

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
            doTrigger = doUpdate,
            expectedUpdatedValue = "20:A:true",
        )
    }

    private fun test_source1Update(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { source1CellFactory ->
            StaticCellFactory.values.forEach { source2CellFactory ->
                StaticCellFactory.values.forEach { source3CellFactory ->
                    test_source1Update(
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
    fun test_source1Update_passive() {
        test_source1Update(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_source1Update_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_source1Update(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_source1Update_quick() {
        test_source1Update(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    private fun test_source2Update(
        source1CellFactory: StaticCellFactory,
        source2CellFactory: DynamicCellFactory,
        source3CellFactory: StaticCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doUpdate = EmitterEventStream<Unit>()

        val sourceCell1 = source1CellFactory.createExternally(10)

        val sourceCell2 = source2CellFactory.createExternally(
            initialValue = 'A',
            doUpdate = doUpdate.map { 'B' },
        )

        val sourceCell3 = source3CellFactory.createExternally(true)

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
            doTrigger = doUpdate,
            expectedUpdatedValue = "10:B:true",
        )
    }

    private fun test_source2Update(
        verificationStrategy: CellVerificationStrategy,
    ) {
        StaticCellFactory.values.forEach { source1CellFactory ->
            DynamicCellFactory.values.forEach { source2CellFactory ->
                StaticCellFactory.values.forEach { source3CellFactory ->
                    test_source2Update(
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
    fun test_source2Update_passive() {
        test_source2Update(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_source2Update_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_source2Update(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_source2Update_quick() {
        test_source2Update(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    private fun test_source3Update(
        source1CellFactory: StaticCellFactory,
        source2CellFactory: StaticCellFactory,
        source3CellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doUpdate = EmitterEventStream<Unit>()

        val sourceCell1 = source1CellFactory.createExternally(10)

        val sourceCell2 = source2CellFactory.createExternally('A')

        val sourceCell3 = source3CellFactory.createExternally(
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
            doTrigger = doUpdate,
            expectedUpdatedValue = "10:A:false",
        )
    }

    private fun test_source3Update(
        verificationStrategy: CellVerificationStrategy,
    ) {
        StaticCellFactory.values.forEach { source1CellFactory ->
            StaticCellFactory.values.forEach { source2CellFactory ->
                DynamicCellFactory.values.forEach { source3CellFactory ->
                    test_source3Update(
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
    fun test_source3Update_passive() {
        test_source3Update(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_source3Update_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_source3Update(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_source3Update_quick() {
        test_source3Update(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    @Suppress("SameParameterValue")
    private fun test_mixedUpdates(
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

        val sourceCell1 = source1CellFactory.createExternally(
            initialValue = initialSource1Value,
            doUpdate = doUpdate.mapNotNull { newSource1Value },
        )

        val sourceCell2 = source2CellFactory.createExternally(
            initialValue = initialSource2Value,
            doUpdate = doUpdate.mapNotNull { newSource2Value },
        )

        val sourceCell3 = source3CellFactory.createExternally(
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
            doTrigger = doUpdate,
            expectedUpdatedValue = "$expectedValue1:$expectedValue2:$expectedValue3",
        )
    }

    @Suppress("SameParameterValue")
    private fun test_mixedUpdates(
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
                    test_mixedUpdates(
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
    fun test_mixedUpdates() {
        val initialSource1Value = 10
        val initialSource2Value = 'A'
        val initialSource3Value = true

        listOf(null, 11).forEach { newSource1Value ->
            listOf(null, 'B').forEach { newSource2Value ->
                listOf(null, false).forEach { newSource3Value ->
                    if (newSource1Value != null || newSource2Value != null || newSource3Value != null) {
                        // At least one source must update

                        test_mixedUpdates(
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

    private fun test_deactivation(
        source1CellFactory: DynamicCellFactory,
        source2CellFactory: DynamicCellFactory,
        source3CellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy.Active,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceCell1 = source1CellFactory.createExternally(
            initialValue = 10,
            doUpdate = doTrigger.map { 11 },
        )

        val sourceCell2 = source2CellFactory.createExternally(
            initialValue = 'A',
            doUpdate = doTrigger.map { 'B' },
        )

        val sourceCell3 = source3CellFactory.createExternally(
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
