package dev.toolkt.reactive.cell

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.test_utils.CellSamplingStrategy
import dev.toolkt.reactive.cell.test_utils.ConstCellFactory
import dev.toolkt.reactive.cell.test_utils.UpdateVerificationStrategy
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
        samplingStrategy: CellSamplingStrategy,
    ) {
        val outerCell = MomentContext.execute {
            outerConstCellFactory.create(
                innerConstCellFactory.create(10),
            )
        }

        val switchCell = Cell.switch(outerCell)

        samplingStrategy.perceive(switchCell).assertCurrentValueEquals(
            expectedCurrentValue = 10,
        )
    }

    private fun test_initial(
        samplingStrategy: CellSamplingStrategy,
    ) {
        ConstCellFactory.values.forEach { outerConstCellFactory ->
            ConstCellFactory.values.forEach { innerConstCellFactory ->
                test_initial(
                    outerConstCellFactory = outerConstCellFactory,
                    innerConstCellFactory = innerConstCellFactory,
                    samplingStrategy = samplingStrategy,
                )
            }
        }
    }

    @Test
    fun test_initial_passive() {
        test_initial(
            samplingStrategy = CellSamplingStrategy.Passive,
        )
    }

    @Test
    fun test_initial_active() {
        test_initial(
            samplingStrategy = CellSamplingStrategy.Active,
        )
    }

    private fun test_initialInnerUpdate(
        outerConstCellFactory: ConstCellFactory,
        updateVerificationStrategy: UpdateVerificationStrategy,
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

        val updateVerifier = updateVerificationStrategy.begin(
            subjectCell = switchCell,
        )

        updateVerifier.verifyUpdates(
            doTrigger = doUpdateInner,
            expectedUpdatedValue = 20,
        )
    }

    private fun test_initialInnerUpdate(
        updateVerificationStrategy: UpdateVerificationStrategy,
    ) {
        ConstCellFactory.values.forEach { outerConstCellFactory ->
            test_initialInnerUpdate(
                outerConstCellFactory = outerConstCellFactory,
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }

    @Test
    fun test_initialInnerUpdate_passive() {
        test_initialInnerUpdate(
            updateVerificationStrategy = UpdateVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_initialInnerUpdate_active() {
        UpdateVerificationStrategy.Active.values.forEach { updateVerificationStrategy ->
            test_initialInnerUpdate(
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }

    @Test
    fun test_initialInnerUpdate_quick() {
        test_initialInnerUpdate(
            updateVerificationStrategy = UpdateVerificationStrategy.Quick,
        )
    }

    private fun test_outerUpdate(
        newInnerConstCellFactory: ConstCellFactory,
        updateVerificationStrategy: UpdateVerificationStrategy,
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

        val updateVerifier = updateVerificationStrategy.begin(
            subjectCell = switchCell,
        )

        updateVerifier.verifyUpdates(
            doTrigger = doUpdateOuter,
            expectedUpdatedValue = 20,
        )
    }

    private fun test_outerUpdate(
        updateVerificationStrategy: UpdateVerificationStrategy,
    ) {
        ConstCellFactory.values.forEach { newInnerConstCellFactory ->
            test_outerUpdate(
                newInnerConstCellFactory = newInnerConstCellFactory,
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdate_passive() {
        test_outerUpdate(
            updateVerificationStrategy = UpdateVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_active() {
        UpdateVerificationStrategy.Active.values.forEach { updateVerificationStrategy ->
            test_outerUpdate(
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdate_quick() {
        test_outerUpdate(
            updateVerificationStrategy = UpdateVerificationStrategy.Quick,
        )
    }

    private fun test_outerUpdate_sameCell(
        innerConstCellFactory: ConstCellFactory,
        updateVerificationStrategy: UpdateVerificationStrategy,
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

        val updateVerifier = updateVerificationStrategy.begin(
            subjectCell = switchCell,
        )

        updateVerifier.verifyUpdates(
            doTrigger = doUpdateOuter,
            expectedUpdatedValue = 20,
        )
    }

    private fun test_outerUpdate_sameCell(
        updateVerificationStrategy: UpdateVerificationStrategy,
    ) {
        ConstCellFactory.values.forEach { innerConstCellFactory ->
            test_outerUpdate_sameCell(
                innerConstCellFactory = innerConstCellFactory,
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdate_sameCell_passive() {
        test_outerUpdate_sameCell(
            updateVerificationStrategy = UpdateVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_sameCell_active() {
        UpdateVerificationStrategy.Active.values.forEach { updateVerificationStrategy ->
            test_outerUpdate_sameCell(
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdate_sameCell_quick() {
        test_outerUpdate(
            updateVerificationStrategy = UpdateVerificationStrategy.Quick,
        )
    }

    private fun test_outerUpdate_thenInitialInnerUpdate(
        newInnerConstCellFactory: ConstCellFactory,
        updateVerificationStrategy: UpdateVerificationStrategy.Total,
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

        val updateVerifier = updateVerificationStrategy.begin(
            subjectCell = switchCell,
        )

        doUpdateOuter.emit()

        updateVerifier.verifyDoesNotUpdate(
            doTrigger = doUpdateInitialInner,
            expectedNonUpdatedValue = 20,
        )
    }

    private fun test_outerUpdate_thenInitialInnerUpdate(
        updateVerificationStrategy: UpdateVerificationStrategy.Total,
    ) {
        ConstCellFactory.values.forEach { newInnerConstCellFactory ->
            test_outerUpdate_thenInitialInnerUpdate(
                newInnerConstCellFactory = newInnerConstCellFactory,
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdate_thenInitialInnerUpdate_passive() {
        test_outerUpdate_thenInitialInnerUpdate(
            updateVerificationStrategy = UpdateVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_thenInitialInnerUpdate_active() {
        UpdateVerificationStrategy.Active.values.forEach { updateVerificationStrategy ->
            test_outerUpdate_thenInitialInnerUpdate(
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }

    private fun test_outerUpdate_thenNewInnerUpdate(
        initialInnerConstCellFactory: ConstCellFactory,
        updateVerificationStrategy: UpdateVerificationStrategy,
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

        val updateVerifier = updateVerificationStrategy.begin(
            subjectCell = switchCell,
        )

        doUpdateOuter.emit()

        updateVerifier.verifyUpdates(
            doTrigger = doUpdateNewInner,
            expectedUpdatedValue = 21,
        )
    }

    private fun test_outerUpdate_thenNewInnerUpdate(
        updateVerificationStrategy: UpdateVerificationStrategy,
    ) {
        ConstCellFactory.values.forEach { initialInnerConstCellFactory ->
            test_outerUpdate_thenNewInnerUpdate(
                initialInnerConstCellFactory = initialInnerConstCellFactory,
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdate_thenNewInnerUpdate_passive() {
        test_outerUpdate_thenNewInnerUpdate(
            updateVerificationStrategy = UpdateVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_thenNewInnerUpdate_active() {
        UpdateVerificationStrategy.Active.values.forEach { updateVerificationStrategy ->
            test_outerUpdate_thenNewInnerUpdate(
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdate_thenNewInnerUpdate_quick() {
        test_outerUpdate_thenNewInnerUpdate(
            updateVerificationStrategy = UpdateVerificationStrategy.Quick,
        )
    }

    private fun test_outerUpdate_simultaneousInitialInnerUpdate(
        newInnerConstCellFactory: ConstCellFactory,
        updateVerificationStrategy: UpdateVerificationStrategy,
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

        val updateVerifier = updateVerificationStrategy.begin(
            subjectCell = switchCell,
        )

        updateVerifier.verifyUpdates(
            doTrigger = doSwitch,
            expectedUpdatedValue = 20,
        )
    }

    private fun test_outerUpdate_simultaneousInitialInnerUpdate(
        updateVerificationStrategy: UpdateVerificationStrategy,
    ) {
        ConstCellFactory.values.forEach { newInnerConstCellFactory ->
            test_outerUpdate_simultaneousInitialInnerUpdate(
                newInnerConstCellFactory = newInnerConstCellFactory,
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdate_simultaneousInitialInnerUpdate_passive() {
        test_outerUpdate_simultaneousInitialInnerUpdate(
            updateVerificationStrategy = UpdateVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_simultaneousInitialInnerUpdate_active() {
        UpdateVerificationStrategy.Active.values.forEach { updateVerificationStrategy ->
            test_outerUpdate_simultaneousInitialInnerUpdate(
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdate_simultaneousInitialInnerUpdate_quick() {
        test_outerUpdate_simultaneousInitialInnerUpdate(
            updateVerificationStrategy = UpdateVerificationStrategy.Quick,
        )
    }

    private fun test_outerUpdate_simultaneousNewInnerUpdate(
        initialInnerConstCellFactory: ConstCellFactory,
        updateVerificationStrategy: UpdateVerificationStrategy,
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

        val updateVerifier = updateVerificationStrategy.begin(
            subjectCell = switchCell,
        )

        updateVerifier.verifyUpdates(
            doTrigger = doSwitch,
            expectedUpdatedValue = 21,
        )
    }

    private fun test_outerUpdate_simultaneousNewInnerUpdate(
        updateVerificationStrategy: UpdateVerificationStrategy,
    ) {
        ConstCellFactory.values.forEach { initialInnerConstCellFactory ->
            test_outerUpdate_simultaneousNewInnerUpdate(
                initialInnerConstCellFactory = initialInnerConstCellFactory,
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdate_simultaneousNewInnerUpdate_passive() {
        test_outerUpdate_simultaneousNewInnerUpdate(
            updateVerificationStrategy = UpdateVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_simultaneousNewInnerUpdate_active() {
        UpdateVerificationStrategy.Active.values.forEach { updateVerificationStrategy ->
            test_outerUpdate_simultaneousNewInnerUpdate(
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdate_simultaneousNewInnerUpdate_quick() {
        test_outerUpdate_simultaneousNewInnerUpdate(
            updateVerificationStrategy = UpdateVerificationStrategy.Quick,
        )
    }

    private fun test_outerUpdate_simultaneousBothInnerUpdates(
        updateVerificationStrategy: UpdateVerificationStrategy,
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

        val updateVerifier = updateVerificationStrategy.begin(
            subjectCell = switchCell,
        )

        updateVerifier.verifyUpdates(
            doTrigger = doSwitch,
            expectedUpdatedValue = 21,
        )
    }

    @Test
    fun test_outerUpdate_simultaneousBothInnerUpdates_passive() {
        test_outerUpdate_simultaneousBothInnerUpdates(
            updateVerificationStrategy = UpdateVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_simultaneousBothInnerUpdates_active() {
        UpdateVerificationStrategy.Active.values.forEach { updateVerificationStrategy ->
            test_outerUpdate_simultaneousBothInnerUpdates(
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdate_simultaneousBothInnerUpdates_quick() {
        test_outerUpdate_simultaneousBothInnerUpdates(
            updateVerificationStrategy = UpdateVerificationStrategy.Quick,
        )
    }

    private fun test_deactivation_initial(
        updateVerificationStrategy: UpdateVerificationStrategy.Active,
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

        updateVerificationStrategy.verifyDeactivation(
            subjectCell = switchCell,
            doTrigger = doSwitch,
        )
    }

    @Test
    fun test_deactivation_initial() {
        UpdateVerificationStrategy.Active.values.forEach { updateVerificationStrategy ->
            test_deactivation_initial(
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }

    private fun test_deactivation_afterOuterUpdate(
        updateVerificationStrategy: UpdateVerificationStrategy.Active,
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

        updateVerificationStrategy.verifyDeactivation(
            subjectCell = switchCell,
            doTrigger = doTrigger,
        )
    }

    @Test
    fun test_deactivation_afterOuterUpdate() {
        UpdateVerificationStrategy.Active.values.forEach { updateVerificationStrategy ->
            test_deactivation_afterOuterUpdate(
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }
}
