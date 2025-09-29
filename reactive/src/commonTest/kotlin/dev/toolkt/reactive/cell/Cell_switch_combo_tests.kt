package dev.toolkt.reactive.cell

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.test_utils.CellSamplingStrategy
import dev.toolkt.reactive.cell.test_utils.ConstCellFactory
import dev.toolkt.reactive.cell.test_utils.UpdateVerificationStrategy
import dev.toolkt.reactive.event_stream.EmitterEventStream
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

    @Test
    fun test_initial_outerInert_innerInert_passive() {
        test_initial(
            outerConstCellFactory = ConstCellFactory.Inert,
            innerConstCellFactory = ConstCellFactory.Inert,
            samplingStrategy = CellSamplingStrategy.Passive,
        )
    }

    @Test
    fun test_initial_outerInert_innerInert_active() {
        test_initial(
            outerConstCellFactory = ConstCellFactory.Inert,
            innerConstCellFactory = ConstCellFactory.Inert,
            samplingStrategy = CellSamplingStrategy.Active,
        )
    }

    @Test
    fun test_initial_outerInert_innerDynamic_passive() {
        test_initial(
            outerConstCellFactory = ConstCellFactory.Inert,
            innerConstCellFactory = ConstCellFactory.Dynamic,
            samplingStrategy = CellSamplingStrategy.Passive,
        )
    }

    @Test
    fun test_initial_outerInert_innerDynamic_active() {
        test_initial(
            outerConstCellFactory = ConstCellFactory.Inert,
            innerConstCellFactory = ConstCellFactory.Dynamic,
            samplingStrategy = CellSamplingStrategy.Active,
        )
    }

    @Test
    fun test_initial_outerDynamic_innerInert_passive() {
        test_initial(
            outerConstCellFactory = ConstCellFactory.Dynamic,
            innerConstCellFactory = ConstCellFactory.Inert,
            samplingStrategy = CellSamplingStrategy.Passive,
        )
    }

    @Test
    fun test_initial_outerDynamic_innerInert_active() {
        test_initial(
            outerConstCellFactory = ConstCellFactory.Dynamic,
            innerConstCellFactory = ConstCellFactory.Inert,
            samplingStrategy = CellSamplingStrategy.Active,
        )
    }

    @Test
    fun test_initial_outerDynamic_innerDynamic_passive() {
        test_initial(
            outerConstCellFactory = ConstCellFactory.Dynamic,
            innerConstCellFactory = ConstCellFactory.Dynamic,
            samplingStrategy = CellSamplingStrategy.Passive,
        )
    }

    @Test
    fun test_initial_outerDynamic_innerDynamic_active() {
        test_initial(
            outerConstCellFactory = ConstCellFactory.Dynamic,
            innerConstCellFactory = ConstCellFactory.Dynamic,
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

        val updateVerificationProcess = updateVerificationStrategy.begin(
            subjectCell = switchCell,
        )

        updateVerificationProcess.verifyUpdates(
            doUpdate = doUpdateInner,
            expectedUpdatedValue = 20,
        )
    }

    @Test
    fun test_initialInnerUpdate_outerInert_passive() {
        test_initialInnerUpdate(
            outerConstCellFactory = ConstCellFactory.Inert,
            updateVerificationStrategy = UpdateVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_initialInnerUpdate_outerInert_active() {
        UpdateVerificationStrategy.Active.values.forEach { updateVerificationStrategy ->
            test_initialInnerUpdate(
                outerConstCellFactory = ConstCellFactory.Inert,
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }

    @Test
    fun test_initialInnerUpdate_outerDynamic_passive() {
        test_initialInnerUpdate(
            outerConstCellFactory = ConstCellFactory.Dynamic,
            updateVerificationStrategy = UpdateVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_initialInnerUpdate_outerDynamic_active() {
        UpdateVerificationStrategy.Active.values.forEach { updateVerificationStrategy ->
            test_initialInnerUpdate(
                outerConstCellFactory = ConstCellFactory.Dynamic,
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
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

        val updateVerificationProcess = updateVerificationStrategy.begin(
            subjectCell = switchCell,
        )

        updateVerificationProcess.verifyUpdates(
            doUpdate = doUpdateOuter,
            expectedUpdatedValue = 20,
        )
    }

    @Test
    fun test_outerUpdate_newInnerInert_passive() {
        test_outerUpdate(
            newInnerConstCellFactory = ConstCellFactory.Inert,
            updateVerificationStrategy = UpdateVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_newInnerInert_active() {
        UpdateVerificationStrategy.Active.values.forEach { updateVerificationStrategy ->
            test_outerUpdate(
                newInnerConstCellFactory = ConstCellFactory.Inert,
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdate_newInnerDynamic_passive() {
        test_outerUpdate(
            newInnerConstCellFactory = ConstCellFactory.Dynamic,
            updateVerificationStrategy = UpdateVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_newInnerDynamic_active() {
        UpdateVerificationStrategy.Active.values.forEach { updateVerificationStrategy ->
            test_outerUpdate(
                newInnerConstCellFactory = ConstCellFactory.Dynamic,
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
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

        val updateVerificationProcess = updateVerificationStrategy.begin(
            subjectCell = switchCell,
        )

        doUpdateOuter.emit()

        updateVerificationProcess.verifyDoesNotUpdate(
            doTrigger = doUpdateInitialInner,
            expectedNonUpdatedValue = 20,
        )
    }

    @Test
    fun test_outerUpdate_thenInitialInnerUpdate_newInnerInert_passive() {
        test_outerUpdate_thenInitialInnerUpdate(
            newInnerConstCellFactory = ConstCellFactory.Inert,
            updateVerificationStrategy = UpdateVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_thenInitialInnerUpdate_newInnerInert_active() {
        UpdateVerificationStrategy.Active.values.forEach { updateVerificationStrategy ->
            test_outerUpdate_thenInitialInnerUpdate(
                newInnerConstCellFactory = ConstCellFactory.Inert,
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdate_thenInitialInnerUpdate_newInnerDynamic_passive() {
        test_outerUpdate_thenInitialInnerUpdate(
            newInnerConstCellFactory = ConstCellFactory.Dynamic,
            updateVerificationStrategy = UpdateVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_thenInitialInnerUpdate_newInnerDynamic_active() {
        UpdateVerificationStrategy.Active.values.forEach { updateVerificationStrategy ->
            test_outerUpdate_thenInitialInnerUpdate(
                newInnerConstCellFactory = ConstCellFactory.Dynamic,
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

        val updateVerificationProcess = updateVerificationStrategy.begin(
            subjectCell = switchCell,
        )

        doUpdateOuter.emit()

        updateVerificationProcess.verifyUpdates(
            doUpdate = doUpdateNewInner,
            expectedUpdatedValue = 21,
        )
    }

    @Test
    fun test_outerUpdate_thenNewInnerUpdate_newInnerInert_passive() {
        test_outerUpdate_thenNewInnerUpdate(
            initialInnerConstCellFactory = ConstCellFactory.Inert,
            updateVerificationStrategy = UpdateVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_thenNewInnerUpdate_newInnerInert_active() {
        UpdateVerificationStrategy.Active.values.forEach { updateVerificationStrategy ->
            test_outerUpdate_thenNewInnerUpdate(
                initialInnerConstCellFactory = ConstCellFactory.Inert,
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdate_thenNewInnerUpdate_newInnerDynamic_passive() {
        test_outerUpdate_thenNewInnerUpdate(
            initialInnerConstCellFactory = ConstCellFactory.Dynamic,
            updateVerificationStrategy = UpdateVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_thenNewInnerUpdate_newInnerDynamic_active() {
        UpdateVerificationStrategy.Active.values.forEach { updateVerificationStrategy ->
            test_outerUpdate_thenNewInnerUpdate(
                initialInnerConstCellFactory = ConstCellFactory.Dynamic,
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
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

        val updateVerificationProcess = updateVerificationStrategy.begin(
            subjectCell = switchCell,
        )

        updateVerificationProcess.verifyUpdates(
            doUpdate = doSwitch,
            expectedUpdatedValue = 20,
        )
    }

    @Test
    fun test_outerUpdate_simultaneousInitialInnerUpdate_newInnerInert_passive() {
        test_outerUpdate_simultaneousInitialInnerUpdate(
            newInnerConstCellFactory = ConstCellFactory.Inert,
            updateVerificationStrategy = UpdateVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_simultaneousInitialInnerUpdate_newInnerInert_active() {
        UpdateVerificationStrategy.Active.values.forEach { updateVerificationStrategy ->
            test_outerUpdate_simultaneousInitialInnerUpdate(
                newInnerConstCellFactory = ConstCellFactory.Inert,
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdate_simultaneousInitialInnerUpdate_newInnerDynamic_passive() {
        test_outerUpdate_simultaneousInitialInnerUpdate(
            newInnerConstCellFactory = ConstCellFactory.Dynamic,
            updateVerificationStrategy = UpdateVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_simultaneousInitialInnerUpdate_newInnerDynamic_active() {
        UpdateVerificationStrategy.Active.values.forEach { updateVerificationStrategy ->
            test_outerUpdate_simultaneousInitialInnerUpdate(
                newInnerConstCellFactory = ConstCellFactory.Dynamic,
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
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

        val updateVerificationProcess = updateVerificationStrategy.begin(
            subjectCell = switchCell,
        )

        updateVerificationProcess.verifyUpdates(
            doUpdate = doSwitch,
            expectedUpdatedValue = 21,
        )
    }

    @Test
    fun test_outerUpdate_simultaneousNewInnerUpdate_newInnerInert_passive() {
        test_outerUpdate_simultaneousNewInnerUpdate(
            initialInnerConstCellFactory = ConstCellFactory.Inert,
            updateVerificationStrategy = UpdateVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_simultaneousNewInnerUpdate_newInnerInert_active() {
        UpdateVerificationStrategy.Active.values.forEach { updateVerificationStrategy ->
            test_outerUpdate_simultaneousNewInnerUpdate(
                initialInnerConstCellFactory = ConstCellFactory.Inert,
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdate_simultaneousNewInnerUpdate_newInnerDynamic_passive() {
        test_outerUpdate_simultaneousNewInnerUpdate(
            initialInnerConstCellFactory = ConstCellFactory.Dynamic,
            updateVerificationStrategy = UpdateVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_simultaneousNewInnerUpdate_newInnerDynamic_active() {
        UpdateVerificationStrategy.Active.values.forEach { updateVerificationStrategy ->
            test_outerUpdate_simultaneousNewInnerUpdate(
                initialInnerConstCellFactory = ConstCellFactory.Dynamic,
                updateVerificationStrategy = updateVerificationStrategy,
            )
        }
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

        val updateVerificationProcess = updateVerificationStrategy.begin(
            subjectCell = switchCell,
        )

        updateVerificationProcess.verifyUpdates(
            doUpdate = doSwitch,
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
}
