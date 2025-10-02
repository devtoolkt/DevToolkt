package dev.toolkt.reactive.cell

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.test_utils.CellVerificationStrategy
import dev.toolkt.reactive.cell.test_utils.FreezingCellFactory
import dev.toolkt.reactive.cell.test_utils.NonChangingCellFactory
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.filter
import dev.toolkt.reactive.event_stream.map
import kotlin.test.Ignore
import kotlin.test.Test

@Suppress("ClassName")
class Cell_map2_combo_tests {
    private fun test_initial(
        source1CellFactory: NonChangingCellFactory,
        source2CellFactory: NonChangingCellFactory,
        verificationStrategy: CellVerificationStrategy.Total,
    ) {
        val sourceCell1 = source1CellFactory.createExternally(10)

        val sourceCell2 = source2CellFactory.createExternally('A')

        val map2Cell = Cell.map2(
            sourceCell1,
            sourceCell2,
        ) { value1, value2 ->
            "$value1:$value2"
        }

        verificationStrategy.begin(
            subjectCell = map2Cell,
        ).verifyCurrentValue(
            expectedCurrentValue = "10:A",
        )
    }

    private fun test_initial(
        verificationStrategy: CellVerificationStrategy.Total,
    ) {
        NonChangingCellFactory.values.forEach { source1CellFactory ->
            NonChangingCellFactory.values.forEach { source2CellFactory ->
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
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doUpdate = EmitterEventStream<Unit>()

        val sourceCell = MomentContext.execute {
            Cell.define(
                initialValue = 10,
                newValues = doUpdate.map { 20 },
            )
        }

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
            doTrigger = doUpdate,
            expectedUpdatedValue = "20:20",
        )
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
        verificationStrategy: CellVerificationStrategy.Total,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceCell1 = MomentContext.execute {
            Cell.define(
                initialValue = 10,
                newValues = doTrigger.filter { false },
            )
        }

        val sourceCell2 = MomentContext.execute {
            Cell.define(
                initialValue = 'A',
                newValues = doTrigger.filter { false },
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

        verifier.verifyDoesNotUpdate(
            doTrigger = doTrigger,
            expectedNonUpdatedValue = "10:A",
        )
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
        source2CellFactory: NonChangingCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doUpdate = EmitterEventStream<Unit>()

        val sourceCell1 = MomentContext.execute {
            Cell.define(
                initialValue = 10,
                newValues = doUpdate.map { 20 },
            )
        }

        val sourceCell2 = source2CellFactory.createExternally('A')

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
            doTrigger = doUpdate,
            expectedUpdatedValue = "20:A",
        )
    }

    private fun test_source1Update(
        verificationStrategy: CellVerificationStrategy,
    ) {
        NonChangingCellFactory.values.forEach { source2CellFactory ->
            test_source1Update(
                source2CellFactory = source2CellFactory,
                verificationStrategy = verificationStrategy,
            )
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
        source1CellFactory: NonChangingCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doUpdate = EmitterEventStream<Unit>()

        val sourceCell1 = source1CellFactory.createExternally(10)

        val sourceCell2 = MomentContext.execute {
            Cell.define(
                initialValue = 'A',
                newValues = doUpdate.map { 'B' },
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
            doTrigger = doUpdate,
            expectedUpdatedValue = "10:B",
        )
    }

    private fun test_source2Update(
        verificationStrategy: CellVerificationStrategy,
    ) {
        NonChangingCellFactory.values.forEach { source1CellFactory ->
            test_source2Update(
                source1CellFactory = source1CellFactory,
                verificationStrategy = verificationStrategy,
            )
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
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doUpdate = EmitterEventStream<Unit>()

        val sourceCell1 = MomentContext.execute {
            Cell.define(
                initialValue = 10,
                newValues = doUpdate.map { 11 },
            )
        }

        val sourceCell2 = MomentContext.execute {
            Cell.define(
                initialValue = 'A',
                newValues = doUpdate.map { 'B' },
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
            doTrigger = doUpdate,
            expectedUpdatedValue = "11:B",
        )
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
        verificationStrategy: CellVerificationStrategy.Total,
    ) {
        val doFreeze = EmitterEventStream<Unit>()

        val doTrigger = EmitterEventStream<Unit>()

        val sourceCell1 = MomentContext.execute {
            source1CellFactory.create(
                value = 10,
                doFreeze = doFreeze,
            )
        }

        val sourceCell2 = MomentContext.execute {
            Cell.define(
                initialValue = 'A',
                newValues = doTrigger.map { 'B' },
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
            doTrigger = doTrigger,
            expectedUpdatedValue = "10:B",
        )
    }

    private fun test_source1Freeze(
        verificationStrategy: CellVerificationStrategy.Total,
    ) {
        FreezingCellFactory.values.forEach { source1CellFactory ->
            test_source1Freeze(
                source1CellFactory = source1CellFactory,
                verificationStrategy = verificationStrategy,
            )
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
        source2CellFactory: FreezingCellFactory,
        verificationStrategy: CellVerificationStrategy.Total,
    ) {
        val doFreeze = EmitterEventStream<Unit>()

        val doTrigger = EmitterEventStream<Unit>()

        val sourceCell1 = MomentContext.execute {
            Cell.define(
                initialValue = 10,
                newValues = doTrigger.map { 11 },
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

        val verifier = verificationStrategy.begin(
            subjectCell = map2Cell,
        )

        verifier.verifyUpdates(
            doTrigger = doTrigger,
            expectedUpdatedValue = "11:A",
        )
    }

    private fun test_source2Freeze(
        verificationStrategy: CellVerificationStrategy.Total,
    ) {
        FreezingCellFactory.values.forEach { source2CellFactory ->
            test_source2Freeze(
                source2CellFactory = source2CellFactory,
                verificationStrategy = verificationStrategy,
            )
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
        verificationStrategy: CellVerificationStrategy.Total,
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
        verificationStrategy: CellVerificationStrategy.Total,
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
        verificationStrategy: CellVerificationStrategy.Active,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceCell1 = MomentContext.execute {
            Cell.define(
                initialValue = 10,
                newValues = doTrigger.map { 11 },
            )
        }

        val sourceCell2 = MomentContext.execute {
            Cell.define(
                initialValue = 'A',
                newValues = doTrigger.map { 'B' },
            )
        }

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
            test_deactivation(
                verificationStrategy = verificationStrategy,
            )
        }
    }
}
