package dev.toolkt.reactive.cell

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.test_utils.CellVerificationStrategy
import dev.toolkt.reactive.cell.test_utils.FreezingCellFactory
import dev.toolkt.reactive.cell.test_utils.NonChangingCellFactory
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.emit
import dev.toolkt.reactive.event_stream.hold
import dev.toolkt.reactive.event_stream.map
import kotlin.test.Ignore
import kotlin.test.Test

@Suppress("ClassName")
class Cell_switch_combo_tests {
    private fun test_initial(
        outerCellFactory: NonChangingCellFactory,
        innerCellFactory: NonChangingCellFactory,
        verificationStrategy: CellVerificationStrategy.Total,
    ) {
        val outerCell = MomentContext.execute {
            outerCellFactory.create(
                innerCellFactory.create(10),
            )
        }

        val switchCell = Cell.switch(outerCell)

        verificationStrategy.begin(
            subjectCell = switchCell,
        ).verifyCurrentValue(
            expectedCurrentValue = 10,
        )
    }

    private fun test_initial(
        verificationStrategy: CellVerificationStrategy.Total,
    ) {
        NonChangingCellFactory.values.forEach { outerCellFactory ->
            NonChangingCellFactory.values.forEach { innerCellFactory ->
                test_initial(
                    outerCellFactory = outerCellFactory,
                    innerCellFactory = innerCellFactory,
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

    private fun test_initialInnerUpdate(
        outerCellFactory: NonChangingCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doUpdateInner = EmitterEventStream<Unit>()

        val outerCell = MomentContext.execute {
            val initialInnerCell = Cell.define(
                initialValue = 10,
                newValues = doUpdateInner.map { 20 },
            )

            outerCellFactory.create(initialInnerCell)
        }

        val switchCell = Cell.switch(outerCell)

        val verifier = verificationStrategy.begin(
            subjectCell = switchCell,
        )

        verifier.verifyUpdates(
            doTrigger = doUpdateInner,
            expectedUpdatedValue = 20,
        )
    }

    private fun test_initialInnerUpdate(
        verificationStrategy: CellVerificationStrategy,
    ) {
        NonChangingCellFactory.values.forEach { outerCellFactory ->
            test_initialInnerUpdate(
                outerCellFactory = outerCellFactory,
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_initialInnerUpdate_passive() {
        test_initialInnerUpdate(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_initialInnerUpdate_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_initialInnerUpdate(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_initialInnerUpdate_quick() {
        test_initialInnerUpdate(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    private fun test_outerUpdate(
        newInnerCellFactory: NonChangingCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doUpdateOuter = EmitterEventStream<Unit>()

        val initialInnerCell = Cell.of(10)

        val newInnerCell = MomentContext.execute {
            newInnerCellFactory.create(20)
        }

        val outerCell = MomentContext.execute {
            doUpdateOuter.map { newInnerCell }.hold(
                initialValue = initialInnerCell,
            )
        }

        val switchCell = Cell.switch(outerCell)

        val verifier = verificationStrategy.begin(
            subjectCell = switchCell,
        )

        verifier.verifyUpdates(
            doTrigger = doUpdateOuter,
            expectedUpdatedValue = 20,
        )
    }

    private fun test_outerUpdate(
        verificationStrategy: CellVerificationStrategy,
    ) {
        NonChangingCellFactory.values.forEach { newInnerCellFactory ->
            test_outerUpdate(
                newInnerCellFactory = newInnerCellFactory,
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdate_passive() {
        test_outerUpdate(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerUpdate(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdate_quick() {
        test_outerUpdate(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    private fun test_outerUpdate_sameCell(
        innerCellFactory: NonChangingCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doUpdateOuter = EmitterEventStream<Unit>()

        val innerCell = MomentContext.execute {
            innerCellFactory.create(20)
        }

        val outerCell = MomentContext.execute {
            Cell.define(
                initialValue = innerCell,
                newValues = doUpdateOuter.map { innerCell },
            )
        }

        val switchCell = Cell.switch(outerCell)

        val verifier = verificationStrategy.begin(
            subjectCell = switchCell,
        )

        verifier.verifyUpdates(
            doTrigger = doUpdateOuter,
            expectedUpdatedValue = 20,
        )
    }

    private fun test_outerUpdate_sameCell(
        verificationStrategy: CellVerificationStrategy,
    ) {
        NonChangingCellFactory.values.forEach { innerCellFactory ->
            test_outerUpdate_sameCell(
                innerCellFactory = innerCellFactory,
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdate_sameCell_passive() {
        test_outerUpdate_sameCell(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_sameCell_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerUpdate_sameCell(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdate_sameCell_quick() {
        test_outerUpdate(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    private fun test_outerUpdate_thenInitialInnerUpdate(
        newInnerCellFactory: NonChangingCellFactory,
        verificationStrategy: CellVerificationStrategy.Total,
    ) {
        val doUpdateOuter = EmitterEventStream<Unit>()

        val doUpdateInitialInner = EmitterEventStream<Unit>()

        val initialInnerCell = MomentContext.execute {
            Cell.define(
                initialValue = 10,
                newValues = doUpdateInitialInner.map { 11 },
            )
        }

        val newInnerCell = MomentContext.execute {
            newInnerCellFactory.create(20)
        }

        val outerCell = MomentContext.execute {
            Cell.define(
                initialValue = initialInnerCell,
                newValues = doUpdateOuter.map { newInnerCell },
            )
        }

        val switchCell = Cell.switch(outerCell)

        val verifier = verificationStrategy.begin(
            subjectCell = switchCell,
        )

        doUpdateOuter.emit()

        verifier.verifyDoesNotUpdate(
            doTrigger = doUpdateInitialInner,
            expectedNonUpdatedValue = 20,
        )
    }

    private fun test_outerUpdate_thenInitialInnerUpdate(
        verificationStrategy: CellVerificationStrategy.Total,
    ) {
        NonChangingCellFactory.values.forEach { newInnerCellFactory ->
            test_outerUpdate_thenInitialInnerUpdate(
                newInnerCellFactory = newInnerCellFactory,
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdate_thenInitialInnerUpdate_passive() {
        test_outerUpdate_thenInitialInnerUpdate(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_thenInitialInnerUpdate_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerUpdate_thenInitialInnerUpdate(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_outerUpdate_thenNewInnerUpdate(
        initialInnerCellFactory: NonChangingCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doUpdateOuter = EmitterEventStream<Unit>()

        val doUpdateNewInner = EmitterEventStream<Unit>()

        val initialInnerCell = MomentContext.execute {
            initialInnerCellFactory.create(10)
        }

        val newInnerCell = MomentContext.execute {
            doUpdateNewInner.map { 21 }.hold(
                initialValue = 20,
            )
        }

        val outerCell = MomentContext.execute {
            doUpdateOuter.map { newInnerCell }.hold(
                initialValue = initialInnerCell,
            )
        }

        val switchCell = Cell.switch(outerCell)

        val verifier = verificationStrategy.begin(
            subjectCell = switchCell,
        )

        doUpdateOuter.emit()

        verifier.verifyUpdates(
            doTrigger = doUpdateNewInner,
            expectedUpdatedValue = 21,
        )
    }

    private fun test_outerUpdate_thenNewInnerUpdate(
        verificationStrategy: CellVerificationStrategy,
    ) {
        NonChangingCellFactory.values.forEach { initialInnerCellFactory ->
            test_outerUpdate_thenNewInnerUpdate(
                initialInnerCellFactory = initialInnerCellFactory,
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdate_thenNewInnerUpdate_passive() {
        test_outerUpdate_thenNewInnerUpdate(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_thenNewInnerUpdate_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerUpdate_thenNewInnerUpdate(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdate_thenNewInnerUpdate_quick() {
        test_outerUpdate_thenNewInnerUpdate(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    private fun test_outerUpdate_simultaneousInitialInnerUpdate(
        newInnerCellFactory: NonChangingCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doSwitch = EmitterEventStream<Unit>()

        val switchCell = MomentContext.execute {
            val initialInnerCell = Cell.define(
                initialValue = 10,
                newValues = doSwitch.map { 11 },
            )

            val newInnerCell = newInnerCellFactory.create(20)

            val outerCell = Cell.define(
                initialValue = initialInnerCell,
                newValues = doSwitch.map { newInnerCell },
            )

            Cell.switch(outerCell)
        }

        val verifier = verificationStrategy.begin(
            subjectCell = switchCell,
        )

        verifier.verifyUpdates(
            doTrigger = doSwitch,
            expectedUpdatedValue = 20,
        )
    }

    private fun test_outerUpdate_simultaneousInitialInnerUpdate(
        verificationStrategy: CellVerificationStrategy,
    ) {
        NonChangingCellFactory.values.forEach { newInnerCellFactory ->
            test_outerUpdate_simultaneousInitialInnerUpdate(
                newInnerCellFactory = newInnerCellFactory,
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdate_simultaneousInitialInnerUpdate_passive() {
        test_outerUpdate_simultaneousInitialInnerUpdate(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_simultaneousInitialInnerUpdate_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerUpdate_simultaneousInitialInnerUpdate(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdate_simultaneousInitialInnerUpdate_quick() {
        test_outerUpdate_simultaneousInitialInnerUpdate(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    private fun test_outerUpdate_simultaneousNewInnerUpdate(
        initialInnerCellFactory: NonChangingCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doSwitch = EmitterEventStream<Unit>()

        val switchCell = MomentContext.execute {
            val initialInnerCell = initialInnerCellFactory.create(10)

            val newInnerCell = Cell.define(
                initialValue = 20,
                newValues = doSwitch.map { 21 },
            )

            val outerCell = Cell.define(
                initialValue = initialInnerCell,
                newValues = doSwitch.map { newInnerCell },
            )

            Cell.switch(outerCell)
        }

        val verifier = verificationStrategy.begin(
            subjectCell = switchCell,
        )

        verifier.verifyUpdates(
            doTrigger = doSwitch,
            expectedUpdatedValue = 21,
        )
    }

    private fun test_outerUpdate_simultaneousNewInnerUpdate(
        verificationStrategy: CellVerificationStrategy,
    ) {
        NonChangingCellFactory.values.forEach { initialInnerCellFactory ->
            test_outerUpdate_simultaneousNewInnerUpdate(
                initialInnerCellFactory = initialInnerCellFactory,
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdate_simultaneousNewInnerUpdate_passive() {
        test_outerUpdate_simultaneousNewInnerUpdate(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_simultaneousNewInnerUpdate_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerUpdate_simultaneousNewInnerUpdate(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdate_simultaneousNewInnerUpdate_quick() {
        test_outerUpdate_simultaneousNewInnerUpdate(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    private fun test_outerUpdate_simultaneousBothInnerUpdates(
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doSwitch = EmitterEventStream<Unit>()

        val switchCell = MomentContext.execute {
            val initialInnerCell = Cell.define(
                initialValue = 10,
                newValues = doSwitch.map { 11 },
            )

            val newInnerCell = Cell.define(
                initialValue = 20,
                newValues = doSwitch.map { 21 },
            )

            val outerCell = Cell.define(
                initialValue = initialInnerCell,
                newValues = doSwitch.map { newInnerCell },
            )

            Cell.switch(outerCell)
        }

        val verifier = verificationStrategy.begin(
            subjectCell = switchCell,
        )

        verifier.verifyUpdates(
            doTrigger = doSwitch,
            expectedUpdatedValue = 21,
        )
    }

    @Test
    fun test_outerUpdate_simultaneousBothInnerUpdates_passive() {
        test_outerUpdate_simultaneousBothInnerUpdates(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_simultaneousBothInnerUpdates_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerUpdate_simultaneousBothInnerUpdates(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdate_simultaneousBothInnerUpdates_quick() {
        test_outerUpdate_simultaneousBothInnerUpdates(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    private fun test_outerFreeze_innerNonChanging(
        outerCellFactory: FreezingCellFactory,
        innerCellFactory: NonChangingCellFactory,
        verificationStrategy: CellVerificationStrategy.Total,
    ) {
        val doFreeze = EmitterEventStream<Unit>()

        val initialInnerCell = MomentContext.execute {
            innerCellFactory.create(10)
        }

        val outerCell = MomentContext.execute {
            outerCellFactory.create(
                value = initialInnerCell,
                doFreeze = doFreeze,
            )
        }

        val switchCell = Cell.switch(outerCell)

        verificationStrategy.verifyCompleteFreeze(
            subjectCell = switchCell,
            doFreeze = doFreeze,
            expectedFrozenValue = 10,
        )
    }

    private fun test_outerFreeze_innerNonChanging(
        verificationStrategy: CellVerificationStrategy.Total,
    ) {
        FreezingCellFactory.values.forEach { outerCellFactory ->
            NonChangingCellFactory.values.forEach { innerCellFactory ->
                test_outerFreeze_innerNonChanging(
                    outerCellFactory = outerCellFactory,
                    innerCellFactory = innerCellFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
        }
    }

    @Test
    fun test_outerFreeze_innerNonChanging_passive() {
        test_outerFreeze_innerNonChanging(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerFreeze_innerNonChanging_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerFreeze_innerNonChanging(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_outerFreeze_thenInnerUpdate(
        outerCellFactory: FreezingCellFactory,
        verificationStrategy: CellVerificationStrategy.Total,
    ) {
        val doFreeze = EmitterEventStream<Unit>()

        val doTrigger = EmitterEventStream<Unit>()

        val innerCell = MomentContext.execute {
            Cell.define(
                initialValue = 10,
                newValues = doTrigger.map { 11 },
            )
        }

        val outerCell = MomentContext.execute {
            outerCellFactory.create(
                value = innerCell,
                doFreeze = doFreeze,
            )
        }

        val switchCell = Cell.switch(outerCell)

        val verifier = verificationStrategy.begin(
            subjectCell = switchCell,
        )

        verifier.verifyUpdates(
            doTrigger = doTrigger,
            expectedUpdatedValue = 11,
        )
    }

    private fun test_outerFreeze_thenInnerUpdate(
        verificationStrategy: CellVerificationStrategy.Total,
    ) {
        FreezingCellFactory.values.forEach { outerCellFactory ->
            test_outerFreeze_thenInnerUpdate(
                outerCellFactory = outerCellFactory,
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerFreeze_thenInnerUpdate_passive() {
        test_outerFreeze_thenInnerUpdate(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerFreeze_thenInnerUpdate_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerFreeze_thenInnerUpdate(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_outerFreeze_simultaneousInnerUpdate(
        outerCellFactory: FreezingCellFactory,
        verificationStrategy: CellVerificationStrategy.Total,
    ) {
        val doFreeze = EmitterEventStream<Unit>()

        val innerCell = MomentContext.execute {
            Cell.define(
                initialValue = 10,
                newValues = doFreeze.map { 11 },
            )
        }

        val outerCell = MomentContext.execute {
            outerCellFactory.create(
                value = innerCell,
                doFreeze = doFreeze,
            )
        }

        val switchCell = Cell.switch(outerCell)

        val verifier = verificationStrategy.begin(
            subjectCell = switchCell,
        )

        verifier.verifyUpdates(
            doTrigger = doFreeze,
            expectedUpdatedValue = 11,
        )
    }

    private fun test_outerFreeze_simultaneousInnerUpdate(
        verificationStrategy: CellVerificationStrategy.Total,
    ) {
        FreezingCellFactory.values.forEach { outerCellFactory ->
            test_outerFreeze_simultaneousInnerUpdate(
                outerCellFactory = outerCellFactory,
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerFreeze_simultaneousInnerUpdate_passive() {
        test_outerFreeze_simultaneousInnerUpdate(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerFreeze_simultaneousInnerUpdate_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerFreeze_simultaneousInnerUpdate(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_outerFreeze_simultaneousInnerFreeze(
        outerCellFactory: FreezingCellFactory,
        innerCellFactory: FreezingCellFactory,
        verificationStrategy: CellVerificationStrategy.Total,
    ) {
        val doFreeze = EmitterEventStream<Unit>()

        val innerCell = MomentContext.execute {
            innerCellFactory.create(
                value = 10,
                doFreeze = doFreeze,
            )
        }

        val outerCell = MomentContext.execute {
            outerCellFactory.create(
                value = innerCell,
                doFreeze = doFreeze,
            )
        }

        val switchCell = Cell.switch(outerCell)

        verificationStrategy.verifyCompleteFreeze(
            subjectCell = switchCell,
            doFreeze = doFreeze,
            expectedFrozenValue = 10,
        )
    }

    private fun test_outerFreeze_simultaneousInnerFreeze(
        verificationStrategy: CellVerificationStrategy.Total,
    ) {
        FreezingCellFactory.values.forEach { outerCellFactory ->
            FreezingCellFactory.values.forEach { innerCellFactory ->
                test_outerFreeze_simultaneousInnerFreeze(
                    outerCellFactory = outerCellFactory,
                    innerCellFactory = innerCellFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
        }
    }

    @Test
    fun test_outerFreeze_simultaneousInnerFreeze_passive() {
        test_outerFreeze_simultaneousInnerFreeze(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerFreeze_simultaneousInnerFreeze_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_outerFreeze_simultaneousInnerFreeze(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_initialInnerFreeze_thenOuterUpdate_thenNewInnerUpdate(
        initialInnerCellFactory: FreezingCellFactory,
        verificationStrategy: CellVerificationStrategy.Total,
    ) {
        val doFreezeInitialInner = EmitterEventStream<Unit>()

        val doTriggerOuter = EmitterEventStream<Unit>()

        val doTriggerNewInner = EmitterEventStream<Unit>()

        val initialInnerCell = MomentContext.execute {
            initialInnerCellFactory.create(
                value = 10,
                doFreeze = doFreezeInitialInner,
            )
        }

        val newInnerCell = MomentContext.execute {
            Cell.define(
                initialValue = 20,
                newValues = doTriggerNewInner.map { 21 },
            )
        }

        val outerCell = MomentContext.execute {
            Cell.define(
                initialValue = initialInnerCell,
                newValues = doTriggerOuter.map { newInnerCell },
            )
        }

        val switchCell = Cell.switch(outerCell)

        val verifier = verificationStrategy.begin(
            subjectCell = switchCell,
        )

        doFreezeInitialInner.emit()

        verifier.verifyUpdates(
            doTrigger = doTriggerOuter,
            expectedUpdatedValue = 20,
        )

        verifier.verifyUpdates(
            doTrigger = doTriggerNewInner,
            expectedUpdatedValue = 21,
        )
    }

    private fun test_initialInnerFreeze_thenOuterUpdate_thenNewInnerUpdate(
        verificationStrategy: CellVerificationStrategy.Total,
    ) {
        FreezingCellFactory.values.forEach { initialInnerCellFactory ->
            test_initialInnerFreeze_thenOuterUpdate_thenNewInnerUpdate(
                initialInnerCellFactory = initialInnerCellFactory,
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_initialInnerFreeze_thenOuterUpdate_thenNewInnerUpdate_passive() {
        test_initialInnerFreeze_thenOuterUpdate_thenNewInnerUpdate(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_initialInnerFreeze_thenOuterUpdate_thenNewInnerUpdate_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_initialInnerFreeze_thenOuterUpdate_thenNewInnerUpdate(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_deactivation_initial(
        verificationStrategy: CellVerificationStrategy.Active,
    ) {
        val doSwitch = EmitterEventStream<Unit>()

        val switchCell = MomentContext.execute {
            val initialInnerCell = Cell.define(
                initialValue = 10,
                newValues = doSwitch.map { 11 },
            )

            val newInnerCell = Cell.define(
                initialValue = 20,
                newValues = doSwitch.map { 21 },
            )

            val outerCell = Cell.define(
                initialValue = initialInnerCell,
                newValues = doSwitch.map { newInnerCell },
            )

            Cell.switch(outerCell)
        }

        verificationStrategy.verifyDeactivation(
            subjectCell = switchCell,
            doTrigger = doSwitch,
        )
    }

    @Test
    fun test_deactivation_initial() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_deactivation_initial(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_deactivation_afterOuterUpdate(
        verificationStrategy: CellVerificationStrategy.Active,
    ) {
        val doPrepare = EmitterEventStream<Unit>()

        val doTrigger = EmitterEventStream<Unit>()

        val switchCell = MomentContext.execute {
            val initialInnerCell = Cell.define(
                initialValue = 10,
                newValues = doTrigger.map { 11 },
            )

            val newInnerCell = Cell.define(
                initialValue = 20,
                newValues = doTrigger.map { 21 },
            )

            val newerInnerCell = Cell.define(
                initialValue = 30,
                newValues = doTrigger.map { 31 },
            )

            val outerCell = Cell.define(
                initialValue = initialInnerCell,
                newValues = EventStream.merge2(
                    doPrepare.map { newInnerCell },
                    doTrigger.map { newerInnerCell },
                ),
            )

            Cell.switch(outerCell)
        }

        doPrepare.emit()

        verificationStrategy.verifyDeactivation(
            subjectCell = switchCell,
            doTrigger = doTrigger,
        )
    }

    @Test
    fun test_deactivation_afterOuterUpdate() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_deactivation_afterOuterUpdate(
                verificationStrategy = verificationStrategy,
            )
        }
    }
}
