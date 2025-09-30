package dev.toolkt.reactive.cell

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.test_utils.CellVerificationStrategy
import dev.toolkt.reactive.cell.test_utils.ConstCellFactory
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.emit
import dev.toolkt.reactive.event_stream.hold
import dev.toolkt.reactive.event_stream.map
import kotlin.test.Test

@Suppress("ClassName")
class Cell_switch_combo_tests {
    private fun test_initial(
        outerConstCellFactory: ConstCellFactory,
        innerConstCellFactory: ConstCellFactory,
        verificationStrategy: CellVerificationStrategy.Total,
    ) {
        val outerCell = MomentContext.execute {
            outerConstCellFactory.create(
                innerConstCellFactory.create(10),
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
        ConstCellFactory.values.forEach { outerConstCellFactory ->
            ConstCellFactory.values.forEach { innerConstCellFactory ->
                test_initial(
                    outerConstCellFactory = outerConstCellFactory,
                    innerConstCellFactory = innerConstCellFactory,
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

    @Test
    fun test_initial_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_initial(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_initialInnerUpdate(
        outerConstCellFactory: ConstCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doUpdateInner = EmitterEventStream<Unit>()

        val outerCell = MomentContext.execute {
            val initialInnerCell = Cell.define(
                initialValue = 10,
                newValues = doUpdateInner.map { 20 },
            )

            outerConstCellFactory.create(initialInnerCell)
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
        ConstCellFactory.values.forEach { outerConstCellFactory ->
            test_initialInnerUpdate(
                outerConstCellFactory = outerConstCellFactory,
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
        newInnerConstCellFactory: ConstCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doUpdateOuter = EmitterEventStream<Unit>()

        val initialInnerCell = Cell.of(10)

        val newInnerCell = MomentContext.execute {
            newInnerConstCellFactory.create(20)
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
        ConstCellFactory.values.forEach { newInnerConstCellFactory ->
            test_outerUpdate(
                newInnerConstCellFactory = newInnerConstCellFactory,
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
        innerConstCellFactory: ConstCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doUpdateOuter = EmitterEventStream<Unit>()

        val innerCell = MomentContext.execute {
            innerConstCellFactory.create(20)
        }

        val outerCell = MomentContext.execute {
            Cell.define(
                innerCell,
                doUpdateOuter.map { innerCell },
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
        ConstCellFactory.values.forEach { innerConstCellFactory ->
            test_outerUpdate_sameCell(
                innerConstCellFactory = innerConstCellFactory,
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
        newInnerConstCellFactory: ConstCellFactory,
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
            newInnerConstCellFactory.create(20)
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
        ConstCellFactory.values.forEach { newInnerConstCellFactory ->
            test_outerUpdate_thenInitialInnerUpdate(
                newInnerConstCellFactory = newInnerConstCellFactory,
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
        initialInnerConstCellFactory: ConstCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doUpdateOuter = EmitterEventStream<Unit>()

        val doUpdateNewInner = EmitterEventStream<Unit>()

        val initialInnerCell = MomentContext.execute {
            initialInnerConstCellFactory.create(10)
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
        ConstCellFactory.values.forEach { initialInnerConstCellFactory ->
            test_outerUpdate_thenNewInnerUpdate(
                initialInnerConstCellFactory = initialInnerConstCellFactory,
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
        newInnerConstCellFactory: ConstCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doSwitch = EmitterEventStream<Unit>()

        val switchCell = MomentContext.execute {
            val initialInnerCell = Cell.define(
                initialValue = 10,
                newValues = doSwitch.map { 11 },
            )

            val newInnerCell = newInnerConstCellFactory.create(20)

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
        ConstCellFactory.values.forEach { newInnerConstCellFactory ->
            test_outerUpdate_simultaneousInitialInnerUpdate(
                newInnerConstCellFactory = newInnerConstCellFactory,
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
        initialInnerConstCellFactory: ConstCellFactory,
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doSwitch = EmitterEventStream<Unit>()

        val switchCell = MomentContext.execute {
            val initialInnerCell = initialInnerConstCellFactory.create(10)

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
        ConstCellFactory.values.forEach { initialInnerConstCellFactory ->
            test_outerUpdate_simultaneousNewInnerUpdate(
                initialInnerConstCellFactory = initialInnerConstCellFactory,
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
