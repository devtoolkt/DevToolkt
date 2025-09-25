package dev.toolkt.reactive.cell

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.test_utils.CellObservationStrategy
import dev.toolkt.reactive.cell.test_utils.CellSamplingStrategy
import dev.toolkt.reactive.cell.test_utils.ConstCellFactory
import dev.toolkt.reactive.cell.test_utils.sampleExternally
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.map
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("ClassName")
class Cell_switch_combo_tests {
    private fun test_initial(
        outerConstCellFactory: ConstCellFactory,
        innerConstCellFactory: ConstCellFactory,
        samplingStrategy: CellSamplingStrategy,
    ) {
        val switchCell = Cell.switch(
            outerConstCellFactory.create(
                innerConstCellFactory.create(10),
            ),
        )

        samplingStrategy.perceive(switchCell).assertCurrentValueEquals(
            expectedCurrentValue = 10,
        )
    }

    @Test
    fun test_initial_outerInert_innerInert() {
        test_initial(
            outerConstCellFactory = ConstCellFactory.Inert,
            innerConstCellFactory = ConstCellFactory.Inert,
            samplingStrategy = CellSamplingStrategy.Passive,
        )
    }

    @Test
    fun test_initial_outerInert_innerInert_preActivate() {
        test_initial(
            outerConstCellFactory = ConstCellFactory.Inert,
            innerConstCellFactory = ConstCellFactory.Inert,
            samplingStrategy = CellSamplingStrategy.Active,
        )
    }

    @Test
    fun test_initial_outerInert_innerDynamic() {
        test_initial(
            outerConstCellFactory = ConstCellFactory.Inert,
            innerConstCellFactory = ConstCellFactory.Dynamic,
            samplingStrategy = CellSamplingStrategy.Passive,
        )
    }

    @Test
    fun test_initial_outerInert_innerDynamic_preActivate() {
        test_initial(
            outerConstCellFactory = ConstCellFactory.Inert,
            innerConstCellFactory = ConstCellFactory.Dynamic,
            samplingStrategy = CellSamplingStrategy.Active,
        )
    }

    @Test
    fun test_initial_outerDynamic_innerInert() {
        test_initial(
            outerConstCellFactory = ConstCellFactory.Dynamic,
            innerConstCellFactory = ConstCellFactory.Inert,
            samplingStrategy = CellSamplingStrategy.Passive,
        )
    }

    @Test
    fun test_initial_outerDynamic_innerInert_preActivate() {
        test_initial(
            outerConstCellFactory = ConstCellFactory.Dynamic,
            innerConstCellFactory = ConstCellFactory.Inert,
            samplingStrategy = CellSamplingStrategy.Active,
        )
    }

    @Test
    fun test_initial_outerDynamic_innerDynamic() {
        test_initial(
            outerConstCellFactory = ConstCellFactory.Dynamic,
            innerConstCellFactory = ConstCellFactory.Dynamic,
            samplingStrategy = CellSamplingStrategy.Passive,
        )
    }

    @Test
    fun test_initial_outerDynamic_innerDynamic_preActivate() {
        test_initial(
            outerConstCellFactory = ConstCellFactory.Dynamic,
            innerConstCellFactory = ConstCellFactory.Dynamic,
            samplingStrategy = CellSamplingStrategy.Active,
        )
    }

    private fun test_initialInnerUpdate(
        outerConstCellFactory: ConstCellFactory,
        observationStrategy: CellObservationStrategy,
    ) {
        val initialInnerCell = MutableCell(
            initialValue = 10,
        )

        val switchCell = Cell.switch(
            outerConstCellFactory.create(initialInnerCell),
        )

        val asserter = observationStrategy.observe(switchCell)

        initialInnerCell.set(20)

        asserter.assertUpdatedValueEquals(
            expectedUpdatedValue = 20,
        )
    }

    @Test
    fun test_initialInnerUpdate_outerInert_passive() {
        test_initialInnerUpdate(
            outerConstCellFactory = ConstCellFactory.Inert,
            observationStrategy = CellObservationStrategy.Passive,
        )
    }


    @Test
    fun test_initialInnerUpdate_outerInert_activeUpdatedValues() {
        test_initialInnerUpdate(
            outerConstCellFactory = ConstCellFactory.Inert,
            observationStrategy = CellObservationStrategy.ActiveUpdatedValues,
        )
    }

    @Test
    fun test_initialInnerUpdate_outerInert_activeNewValues() {
        test_initialInnerUpdate(
            outerConstCellFactory = ConstCellFactory.Inert,
            observationStrategy = CellObservationStrategy.ActiveNewValues,
        )
    }

    @Test
    fun test_initialInnerUpdate_outerDynamic_passive() {
        test_initialInnerUpdate(
            outerConstCellFactory = ConstCellFactory.Dynamic,
            observationStrategy = CellObservationStrategy.Passive,
        )
    }

    @Test
    fun test_initialInnerUpdate_outerDynamic_activeUpdatedValues() {
        test_initialInnerUpdate(
            outerConstCellFactory = ConstCellFactory.Dynamic,
            observationStrategy = CellObservationStrategy.ActiveUpdatedValues,
        )
    }

    @Test
    fun test_initialInnerUpdate_outerDynamic_activeNewValues() {
        test_initialInnerUpdate(
            outerConstCellFactory = ConstCellFactory.Dynamic,
            observationStrategy = CellObservationStrategy.ActiveNewValues,
        )
    }

    private fun test_outerUpdate(
        newInnerConstCellFactory: ConstCellFactory,
        observationStrategy: CellObservationStrategy,
    ) {
        val initialInnerCell = Cell.of(10)

        val newInnerCell = newInnerConstCellFactory.create(20)

        val outerCell = MutableCell(
            initialValue = initialInnerCell,
        )

        val switchCell = Cell.switch(outerCell)

        val asserter = observationStrategy.observe(switchCell)

        outerCell.set(newInnerCell)

        asserter.assertUpdatedValueEquals(
            expectedUpdatedValue = 20,
        )
    }

    @Test
    fun test_outerUpdate_newInnerInert_passive() {
        test_outerUpdate(
            newInnerConstCellFactory = ConstCellFactory.Inert,
            observationStrategy = CellObservationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_newInnerInert_activeUpdatedValues() {
        test_outerUpdate(
            newInnerConstCellFactory = ConstCellFactory.Inert,
            observationStrategy = CellObservationStrategy.ActiveUpdatedValues,
        )
    }

    @Test
    fun test_outerUpdate_newInnerInert_activeNewValues() {
        test_outerUpdate(
            newInnerConstCellFactory = ConstCellFactory.Inert,
            observationStrategy = CellObservationStrategy.ActiveNewValues,
        )
    }

    @Test
    fun test_outerUpdate_newInnerDynamic_passive() {
        test_outerUpdate(
            newInnerConstCellFactory = ConstCellFactory.Dynamic,
            observationStrategy = CellObservationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_newInnerDynamic_activeUpdatedValues() {
        test_outerUpdate(
            newInnerConstCellFactory = ConstCellFactory.Dynamic,
            observationStrategy = CellObservationStrategy.ActiveUpdatedValues,
        )
    }

    @Test
    fun test_outerUpdate_newInnerDynamic_activeNewValues() {
        test_outerUpdate(
            newInnerConstCellFactory = ConstCellFactory.Dynamic,
            observationStrategy = CellObservationStrategy.ActiveNewValues,
        )
    }

    private fun test_outerUpdate_thenInitialInnerUpdate(
        newInnerConstCellFactory: ConstCellFactory,
        observationStrategy: CellObservationStrategy,
    ) {
        val initialInnerCell = MutableCell(
            initialValue = 10,
        )

        val newInnerCell = newInnerConstCellFactory.create(20)

        val outerCell: MutableCell<Cell<Int>> = MutableCell(
            initialValue = initialInnerCell,
        )

        val switchCell = Cell.switch(outerCell)

        val asserter = observationStrategy.observe(switchCell)

        outerCell.set(newInnerCell)

        asserter.assertUpdatedValueEquals(
            expectedUpdatedValue = 20,
        )

        initialInnerCell.set(11)

        assertEquals(
            expected = 20,
            actual = switchCell.sampleExternally(),
        )
    }


    @Test
    fun test_outerUpdate_thenInitialInnerUpdate_newInnerInert_passive() {
        test_outerUpdate_thenInitialInnerUpdate(
            newInnerConstCellFactory = ConstCellFactory.Inert,
            observationStrategy = CellObservationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_thenInitialInnerUpdate_newInnerInert_activeUpdatedValues() {
        test_outerUpdate_thenInitialInnerUpdate(
            newInnerConstCellFactory = ConstCellFactory.Inert,
            observationStrategy = CellObservationStrategy.ActiveUpdatedValues,
        )
    }

    @Test
    fun test_outerUpdate_thenInitialInnerUpdate_newInnerInert_activeNewValues() {
        test_outerUpdate_thenInitialInnerUpdate(
            newInnerConstCellFactory = ConstCellFactory.Inert,
            observationStrategy = CellObservationStrategy.ActiveNewValues,
        )
    }

    @Test
    fun test_outerUpdate_thenInitialInnerUpdate_newInnerDynamic_passive() {
        test_outerUpdate_thenInitialInnerUpdate(
            newInnerConstCellFactory = ConstCellFactory.Dynamic,
            observationStrategy = CellObservationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_thenInitialInnerUpdate_newInnerDynamic_activeUpdatedValues() {
        test_outerUpdate_thenInitialInnerUpdate(
            newInnerConstCellFactory = ConstCellFactory.Dynamic,
            observationStrategy = CellObservationStrategy.ActiveUpdatedValues,
        )
    }

    @Test
    fun test_outerUpdate_thenInitialInnerUpdate_newInnerDynamic_activeNewValues() {
        test_outerUpdate_thenInitialInnerUpdate(
            newInnerConstCellFactory = ConstCellFactory.Dynamic,
            observationStrategy = CellObservationStrategy.ActiveNewValues,
        )
    }

    private fun test_outerUpdate_thenNewInnerUpdate(
        initialInnerConstCellFactory: ConstCellFactory,
        observationStrategy: CellObservationStrategy,
    ) {
        val initialInnerCell = initialInnerConstCellFactory.create(10)

        val newInnerCell = MutableCell(
            initialValue = 20,
        )

        val outerCell = MutableCell(
            initialValue = initialInnerCell,
        )

        val switchCell = Cell.switch(outerCell)

        val asserter = observationStrategy.observe(switchCell)

        outerCell.set(newInnerCell)

        asserter.assertUpdatedValueEquals(
            expectedUpdatedValue = 20,
        )

        newInnerCell.set(21)

        asserter.assertUpdatedValueEquals(
            expectedUpdatedValue = 21,
        )
    }

    @Test
    fun test_outerUpdate_thenNewInnerUpdate_newInnerInert_passive() {
        test_outerUpdate_thenNewInnerUpdate(
            initialInnerConstCellFactory = ConstCellFactory.Inert,
            observationStrategy = CellObservationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_thenNewInnerUpdate_newInnerInert_activeUpdatedValues() {
        test_outerUpdate_thenNewInnerUpdate(
            initialInnerConstCellFactory = ConstCellFactory.Inert,
            observationStrategy = CellObservationStrategy.ActiveUpdatedValues,
        )
    }

    @Test
    fun test_outerUpdate_thenNewInnerUpdate_newInnerInert_activeNewValues() {
        test_outerUpdate_thenNewInnerUpdate(
            initialInnerConstCellFactory = ConstCellFactory.Inert,
            observationStrategy = CellObservationStrategy.ActiveNewValues,
        )
    }

    @Test
    fun test_outerUpdate_thenNewInnerUpdate_newInnerDynamic_passive() {
        test_outerUpdate_thenNewInnerUpdate(
            initialInnerConstCellFactory = ConstCellFactory.Dynamic,
            observationStrategy = CellObservationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_thenNewInnerUpdate_newInnerDynamic_activeUpdatedValues() {
        test_outerUpdate_thenNewInnerUpdate(
            initialInnerConstCellFactory = ConstCellFactory.Dynamic,
            observationStrategy = CellObservationStrategy.ActiveUpdatedValues,
        )
    }

    @Test
    fun test_outerUpdate_thenNewInnerUpdate_newInnerDynamic_activeNewValues() {
        test_outerUpdate_thenNewInnerUpdate(
            initialInnerConstCellFactory = ConstCellFactory.Dynamic,
            observationStrategy = CellObservationStrategy.ActiveNewValues,
        )
    }

    private fun test_outerUpdate_simultaneousInitialInnerUpdate(
        newInnerConstCellFactory: ConstCellFactory,
        observationStrategy: CellObservationStrategy,
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

        val asserter = observationStrategy.observe(switchCell)

        doSwitch.emit(Unit)

        asserter.assertUpdatedValueEquals(
            expectedUpdatedValue = 20,
        )
    }

    @Test
    fun test_outerUpdate_simultaneousInitialInnerUpdate_newInnerInert_passive() {
        test_outerUpdate_simultaneousInitialInnerUpdate(
            newInnerConstCellFactory = ConstCellFactory.Inert,
            observationStrategy = CellObservationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_simultaneousInitialInnerUpdate_newInnerInert_activeUpdatedValues() {
        test_outerUpdate_simultaneousInitialInnerUpdate(
            newInnerConstCellFactory = ConstCellFactory.Inert,
            observationStrategy = CellObservationStrategy.ActiveUpdatedValues,
        )
    }

    @Test
    fun test_outerUpdate_simultaneousInitialInnerUpdate_newInnerInert_activeNewValues() {
        test_outerUpdate_simultaneousInitialInnerUpdate(
            newInnerConstCellFactory = ConstCellFactory.Inert,
            observationStrategy = CellObservationStrategy.ActiveNewValues,
        )
    }

    @Test
    fun test_outerUpdate_simultaneousInitialInnerUpdate_newInnerDynamic_passive() {
        test_outerUpdate_simultaneousInitialInnerUpdate(
            newInnerConstCellFactory = ConstCellFactory.Dynamic,
            observationStrategy = CellObservationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_simultaneousInitialInnerUpdate_newInnerDynamic_activeUpdatedValues() {
        test_outerUpdate_simultaneousInitialInnerUpdate(
            newInnerConstCellFactory = ConstCellFactory.Dynamic,
            observationStrategy = CellObservationStrategy.ActiveUpdatedValues,
        )
    }

    @Test
    fun test_outerUpdate_simultaneousInitialInnerUpdate_newInnerDynamic_activeNewValues() {
        test_outerUpdate_simultaneousInitialInnerUpdate(
            newInnerConstCellFactory = ConstCellFactory.Dynamic,
            observationStrategy = CellObservationStrategy.ActiveNewValues,
        )
    }


    private fun test_outerUpdate_simultaneousNewInnerUpdate(
        initialInnerConstCellFactory: ConstCellFactory,
        observationStrategy: CellObservationStrategy,
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

        val asserter = observationStrategy.observe(switchCell)

        doSwitch.emit(Unit)

        asserter.assertUpdatedValueEquals(
            expectedUpdatedValue = 20,
        )
    }

    @Ignore // FIXME: Flaky test
    @Test
    fun test_outerUpdate_simultaneousNewInnerUpdate_newInnerInert_passive() {
        test_outerUpdate_simultaneousNewInnerUpdate(
            initialInnerConstCellFactory = ConstCellFactory.Inert,
            observationStrategy = CellObservationStrategy.Passive,
        )
    }

    @Ignore // FIXME: Flaky test
    @Test
    fun test_outerUpdate_simultaneousNewInnerUpdate_newInnerInert_activeUpdatedValues() {
        test_outerUpdate_simultaneousNewInnerUpdate(
            initialInnerConstCellFactory = ConstCellFactory.Inert,
            observationStrategy = CellObservationStrategy.ActiveUpdatedValues,
        )
    }

    @Ignore // FIXME: Flaky test
    @Test
    fun test_outerUpdate_simultaneousNewInnerUpdate_newInnerInert_activeNewValues() {
        test_outerUpdate_simultaneousNewInnerUpdate(
            initialInnerConstCellFactory = ConstCellFactory.Inert,
            observationStrategy = CellObservationStrategy.ActiveNewValues,
        )
    }

    @Ignore // FIXME: Flaky test
    @Test
    fun test_outerUpdate_simultaneousNewInnerUpdate_newInnerDynamic_passive() {
        test_outerUpdate_simultaneousNewInnerUpdate(
            initialInnerConstCellFactory = ConstCellFactory.Dynamic,
            observationStrategy = CellObservationStrategy.Passive,
        )
    }

    @Ignore // FIXME: Flaky test
    @Test
    fun test_outerUpdate_simultaneousNewInnerUpdate_newInnerDynamic_activeUpdatedValues() {
        test_outerUpdate_simultaneousNewInnerUpdate(
            initialInnerConstCellFactory = ConstCellFactory.Dynamic,
            observationStrategy = CellObservationStrategy.ActiveUpdatedValues,
        )
    }

    @Ignore // FIXME: Flaky test
    @Test
    fun test_outerUpdate_simultaneousNewInnerUpdate_newInnerDynamic_activeNewValues() {
        test_outerUpdate_simultaneousNewInnerUpdate(
            initialInnerConstCellFactory = ConstCellFactory.Dynamic,
            observationStrategy = CellObservationStrategy.ActiveNewValues,
        )
    }

    private fun test_outerUpdate_simultaneousBothInnerUpdates(
        observationStrategy: CellObservationStrategy,
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

        val asserter = observationStrategy.observe(switchCell)

        doSwitch.emit(Unit)

        asserter.assertUpdatedValueEquals(
            expectedUpdatedValue = 21,
        )
    }

    @Test
    fun test_outerUpdate_simultaneousBothInnerUpdates_passive() {
        test_outerUpdate_simultaneousBothInnerUpdates(
            observationStrategy = CellObservationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_simultaneousBothInnerUpdates_activeUpdatedValues() {
        test_outerUpdate_simultaneousBothInnerUpdates(
            observationStrategy = CellObservationStrategy.ActiveUpdatedValues,
        )
    }

    @Test
    fun test_outerUpdate_simultaneousBothInnerUpdates_activeNewValues() {
        test_outerUpdate_simultaneousBothInnerUpdates(
            observationStrategy = CellObservationStrategy.ActiveNewValues,
        )
    }
}
