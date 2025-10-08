package dev.toolkt.reactive.cell

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.test_utils.CellVerificationStrategy
import dev.toolkt.reactive.cell.test_utils.DynamicCellFactory
import dev.toolkt.reactive.cell.test_utils.FreezingCellFactory
import dev.toolkt.reactive.cell.test_utils.InertCellFactory
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.map
import dev.toolkt.reactive.event_stream.mapNotNull
import kotlin.test.Ignore
import kotlin.test.Test

@Suppress("ClassName")
class Cell_map2_combo_tests {
    private fun test_initial(
        source1CellFactory: InertCellFactory,
        source2CellFactory: InertCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val sourceCell1 = source1CellFactory.createInertExternally(10)

        val sourceCell2 = source2CellFactory.createInertExternally('A')

        val map2Cell = Cell.map2(
            sourceCell1,
            sourceCell2,
        ) { value1, value2 ->
            "$value1:$value2"
        }

        val verifier = verificationStrategy.begin(
            subjectCell = map2Cell,
        )

        verifier.verifyCurrentValue(
            expectedCurrentValue = "10:A",
        )
    }

    private fun test_initial(
        verificationStrategy: CellVerificationStrategy,
    ) {
        InertCellFactory.values.forEach { source1CellFactory ->
            InertCellFactory.values.forEach { source2CellFactory ->
                test_initial(
                    source1CellFactory = source1CellFactory,
                    source2CellFactory = source2CellFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
        }
    }

    @Test
    fun test_initial_passive() {
        test_initial(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Ignore // FIXME: Subscription should not be null.
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

        val map2Cell = Cell.map2(
            sourceCell,
            sourceCell,
        ) { value1, value2 ->
            "$value1:$value2"
        }

        val verifier = verificationStrategy.begin(
            subjectCell = map2Cell,
        )

        verifier.verifyUpdates(
            doTriggerUpdate = doUpdate,
            expectedUpdatedValue = "20:20",
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
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceCell1 = sourceCell1Factory.createExternally(
            initialValue = 10,
            doUpdate = doTrigger.mapNotNull { null },
        )

        val sourceCell2 = sourceCell2Factory.createExternally(
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

    private fun test_source1Update(
        source1CellFactory: DynamicCellFactory,
        source2CellFactory: InertCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceCell1 = source1CellFactory.createExternally(
            initialValue = 10,
            doUpdate = doTrigger.map { 20 },
        )

        val sourceCell2 = source2CellFactory.createInertExternally('A')

        val map2Cell = Cell.map2(
            sourceCell1,
            sourceCell2,
        ) { value1, value2 ->
            "$value1:$value2"
        }

        val verifier = verificationStrategy.begin(
            subjectCell = map2Cell,
        )

        verifier.verifyUpdates(
            doTriggerUpdate = doTrigger,
            expectedUpdatedValue = "20:A",
        )
    }

    private fun test_source1Update(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { source1CellFactory ->
            InertCellFactory.values.forEach { source2CellFactory ->
                test_source1Update(
                    source1CellFactory = source1CellFactory,
                    source2CellFactory = source2CellFactory,
                    verificationStrategy = verificationStrategy,
                )
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
        source1CellFactory: InertCellFactory,
        source2CellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceCell1 = source1CellFactory.createInertExternally(10)

        val sourceCell2 = source2CellFactory.createExternally(
            initialValue = 'A',
            doUpdate = doTrigger.map { 'B' },
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

        verifier.verifyUpdates(
            doTriggerUpdate = doTrigger,
            expectedUpdatedValue = "10:B",
        )
    }

    private fun test_source2Update(
        verificationStrategy: CellVerificationStrategy,
    ) {
        InertCellFactory.values.forEach { source1CellFactory ->
            DynamicCellFactory.values.forEach { source2CellFactory ->
                test_source2Update(
                    source1CellFactory = source1CellFactory,
                    source2CellFactory = source2CellFactory,
                    verificationStrategy = verificationStrategy,
                )
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

    private fun test_simultaneousUpdates(
        source1CellFactory: DynamicCellFactory,
        source2CellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doUpdate = EmitterEventStream<Unit>()

        val sourceCell1 = source1CellFactory.createExternally(
            initialValue = 10,
            doUpdate = doUpdate.map { 11 },
        )

        val sourceCell2 = source2CellFactory.createExternally(
            initialValue = 'A',
            doUpdate = doUpdate.map { 'B' },
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

        verifier.verifyUpdates(
            doTriggerUpdate = doUpdate,
            expectedUpdatedValue = "11:B",
        )
    }

    private fun test_simultaneousUpdates(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { source1CellFactory ->
            DynamicCellFactory.values.forEach { source2CellFactory ->
                test_simultaneousUpdates(
                    source1CellFactory = source1CellFactory,
                    source2CellFactory = source2CellFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
        }
    }

    @Test
    fun test_simultaneousUpdates_passive() {
        test_simultaneousUpdates(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_simultaneousUpdates_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_simultaneousUpdates(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_simultaneousUpdates_quick() {
        test_simultaneousUpdates(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    private fun test_source1Freeze(
        source1CellFactory: FreezingCellFactory,
        source2CellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doFreeze = EmitterEventStream<Unit>()
        val doTrigger = EmitterEventStream<Unit>()

        val sourceCell1 = MomentContext.execute {
            source1CellFactory.create(
                value = 10,
                doFreeze = doFreeze,
            )
        }

        val sourceCell2 = source2CellFactory.createExternally(
            initialValue = 'A',
            doUpdate = doTrigger.map { 'B' },
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

        verifier.verifyUpdates(
            doTriggerUpdate = doTrigger,
            expectedUpdatedValue = "10:B",
        )
    }

    private fun test_source1Freeze(
        verificationStrategy: CellVerificationStrategy,
    ) {
        FreezingCellFactory.values.forEach { source1CellFactory ->
            DynamicCellFactory.values.forEach { source2CellFactory ->
                test_source1Freeze(
                    source1CellFactory = source1CellFactory,
                    source2CellFactory = source2CellFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
        }
    }

    @Test
    fun test_source1Freeze_passive() {
        test_source1Freeze(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_source1Freeze_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_source1Freeze(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_source2Freeze(
        source1CellFactory: DynamicCellFactory,
        source2CellFactory: FreezingCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doFreeze = EmitterEventStream<Unit>()
        val doTrigger = EmitterEventStream<Unit>()

        val sourceCell1 = source1CellFactory.createExternally(
            initialValue = 10,
            doUpdate = doTrigger.map { 11 },
        )

        val sourceCell2 = MomentContext.execute {
            source2CellFactory.create(
                value = 'A',
                doFreeze = doFreeze,
            )
        }

        val map2Cell = Cell.map2(
            sourceCell1,
            sourceCell2,
        ) { value1, value2 ->
            "$value1:$value2"
        }

        val verifier = verificationStrategy.begin(
            subjectCell = map2Cell,
        )

        verifier.verifyUpdates(
            doTriggerUpdate = doTrigger,
            expectedUpdatedValue = "11:A",
        )
    }

    private fun test_source2Freeze(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { source1CellFactory ->
            FreezingCellFactory.values.forEach { source2CellFactory ->
                test_source2Freeze(
                    source1CellFactory = source1CellFactory,
                    source2CellFactory = source2CellFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
        }
    }

    @Test
    fun test_source2Freeze_passive() {
        test_source2Freeze(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_source2Freeze_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_source2Freeze(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_simultaneousFreeze(
        source1CellFactory: FreezingCellFactory,
        source2CellFactory: FreezingCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doFreeze = EmitterEventStream<Unit>()

        val sourceCell1 = MomentContext.execute {
            source1CellFactory.create(
                value = 10,
                doFreeze = doFreeze,
            )
        }

        val sourceCell2 = MomentContext.execute {
            source2CellFactory.create(
                value = 'A',
                doFreeze = doFreeze,
            )
        }

        val map2Cell = Cell.map2(
            sourceCell1,
            sourceCell2,
        ) { value1, value2 ->
            "$value1:$value2"
        }

        verificationStrategy.verifyCompleteFreeze(
            subjectCell = map2Cell,
            doFreeze = doFreeze,
            expectedFrozenValue = "10:A",
        )
    }

    private fun test_simultaneousFreeze(
        verificationStrategy: CellVerificationStrategy,
    ) {
        FreezingCellFactory.values.forEach { source1CellFactory ->
            FreezingCellFactory.values.forEach { source2CellFactory ->
                test_simultaneousFreeze(
                    source1CellFactory = source1CellFactory,
                    source2CellFactory = source2CellFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
        }
    }

    @Test
    fun test_simultaneousFreeze_passive() {
        test_simultaneousFreeze(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_simultaneousFreeze_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_simultaneousFreeze(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_deactivation(
        source1CellFactory: DynamicCellFactory,
        source2CellFactory: DynamicCellFactory,
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
