package dev.toolkt.reactive.cell

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.test_utils.CellVerificationStrategy
import dev.toolkt.reactive.cell.test_utils.DynamicCellFactory
import dev.toolkt.reactive.cell.test_utils.FreezingCellFactory
import dev.toolkt.reactive.cell.test_utils.InertCellFactory
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.map
import kotlin.test.Ignore
import kotlin.test.Test

@Suppress("ClassName")
class Cell_map2_state_tests {
    private fun test_state_initial(
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

    private fun test_state_initial(
        verificationStrategy: CellVerificationStrategy,
    ) {
        InertCellFactory.values.forEach { source1CellFactory ->
            InertCellFactory.values.forEach { source2CellFactory ->
                test_state_initial(
                    source1CellFactory = source1CellFactory,
                    source2CellFactory = source2CellFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
        }
    }

    @Test
    fun test_state_initial_passive() {
        test_state_initial(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Ignore // FIXME: Subscription should not be null.
    @Test
    fun test_state_initial_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_state_initial(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_state_sameSource_initial(
        sourceCellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val sourceCell = sourceCellFactory.createDynamicExternally(
            initialValue = 10,
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

        verifier.verifyCurrentValue(
            expectedCurrentValue = "10:10",
        )
    }

    private fun test_state_sameSource_initial(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { sourceCellFactory ->
            test_state_sameSource_initial(
                sourceCellFactory = sourceCellFactory,
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_state_sameSource_initial_passive() {
        test_state_sameSource_initial(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Ignore // FIXME
    @Test
    fun test_state_sameSource_initial_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_state_sameSource_initial(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_state_sameSource_sourceUpdate(
        sourceCellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doUpdate = EmitterEventStream<Unit>()

        val sourceCell = sourceCellFactory.createDynamicExternally(
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

    private fun test_state_sameSource_sourceUpdate(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { sourceCellFactory ->
            test_state_sameSource_sourceUpdate(
                sourceCellFactory = sourceCellFactory,
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_state_sameSource_sourceUpdate_passive() {
        test_state_sameSource_sourceUpdate(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Ignore // FIXME: Flaky test
    @Test
    fun test_state_sameSource_sourceUpdate_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_state_sameSource_sourceUpdate(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Ignore // FIXME: Flaky test
    @Test
    fun test_state_sameSource_sourceUpdate_quick() {
        test_state_sameSource_sourceUpdate(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    private fun test_state_source1Update(
        source1CellFactory: DynamicCellFactory,
        source2CellFactory: InertCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceCell1 = source1CellFactory.createDynamicExternally(
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

    private fun test_state_source1Update(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { source1CellFactory ->
            InertCellFactory.values.forEach { source2CellFactory ->
                test_state_source1Update(
                    source1CellFactory = source1CellFactory,
                    source2CellFactory = source2CellFactory,
                    verificationStrategy = verificationStrategy,
                )
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
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceCell1 = source1CellFactory.createInertExternally(10)

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

        val verifier = verificationStrategy.begin(
            subjectCell = map2Cell,
        )

        verifier.verifyUpdates(
            doTriggerUpdate = doTrigger,
            expectedUpdatedValue = "10:B",
        )
    }

    private fun test_state_source2Update(
        verificationStrategy: CellVerificationStrategy,
    ) {
        InertCellFactory.values.forEach { source1CellFactory ->
            DynamicCellFactory.values.forEach { source2CellFactory ->
                test_state_source2Update(
                    source1CellFactory = source1CellFactory,
                    source2CellFactory = source2CellFactory,
                    verificationStrategy = verificationStrategy,
                )
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

    private fun test_state_simultaneousUpdates(
        source1CellFactory: DynamicCellFactory,
        source2CellFactory: DynamicCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doUpdate = EmitterEventStream<Unit>()

        val sourceCell1 = source1CellFactory.createDynamicExternally(
            initialValue = 10,
            doUpdate = doUpdate.map { 11 },
        )

        val sourceCell2 = source2CellFactory.createDynamicExternally(
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

    private fun test_state_simultaneousUpdates(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { source1CellFactory ->
            DynamicCellFactory.values.forEach { source2CellFactory ->
                test_state_simultaneousUpdates(
                    source1CellFactory = source1CellFactory,
                    source2CellFactory = source2CellFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
        }
    }

    @Test
    fun test_state_simultaneousUpdates_passive() {
        test_state_simultaneousUpdates(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_state_simultaneousUpdates_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_state_simultaneousUpdates(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_state_simultaneousUpdates_quick() {
        test_state_simultaneousUpdates(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    private fun test_state_source1Freeze(
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

        val verifier = verificationStrategy.begin(
            subjectCell = map2Cell,
        )

        verifier.verifyUpdates(
            doTriggerUpdate = doTrigger,
            expectedUpdatedValue = "10:B",
        )
    }

    private fun test_state_source1Freeze(
        verificationStrategy: CellVerificationStrategy,
    ) {
        FreezingCellFactory.values.forEach { source1CellFactory ->
            DynamicCellFactory.values.forEach { source2CellFactory ->
                test_state_source1Freeze(
                    source1CellFactory = source1CellFactory,
                    source2CellFactory = source2CellFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
        }
    }

    @Test
    fun test_state_source1Freeze_passive() {
        test_state_source1Freeze(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_state_source1Freeze_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_state_source1Freeze(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_state_source2Freeze(
        source1CellFactory: DynamicCellFactory,
        source2CellFactory: FreezingCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doFreeze = EmitterEventStream<Unit>()
        val doTrigger = EmitterEventStream<Unit>()

        val sourceCell1 = source1CellFactory.createDynamicExternally(
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

    private fun test_state_source2Freeze(
        verificationStrategy: CellVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { source1CellFactory ->
            FreezingCellFactory.values.forEach { source2CellFactory ->
                test_state_source2Freeze(
                    source1CellFactory = source1CellFactory,
                    source2CellFactory = source2CellFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
        }
    }

    @Test
    fun test_state_source2Freeze_passive() {
        test_state_source2Freeze(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_state_source2Freeze_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_state_source2Freeze(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_state_simultaneousFreeze(
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

    private fun test_state_simultaneousFreeze(
        verificationStrategy: CellVerificationStrategy,
    ) {
        FreezingCellFactory.values.forEach { source1CellFactory ->
            FreezingCellFactory.values.forEach { source2CellFactory ->
                test_state_simultaneousFreeze(
                    source1CellFactory = source1CellFactory,
                    source2CellFactory = source2CellFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
        }
    }

    @Test
    fun test_state_simultaneousFreeze_passive() {
        test_state_simultaneousFreeze(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_state_simultaneousFreeze_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_state_simultaneousFreeze(
                verificationStrategy = verificationStrategy,
            )
        }
    }
}
