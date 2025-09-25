package dev.toolkt.reactive.cell

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.test_utils.CellObservationChannel
import dev.toolkt.reactive.cell.test_utils.CellObservationStrategy
import dev.toolkt.reactive.cell.test_utils.CellSamplingStrategy
import dev.toolkt.reactive.cell.test_utils.ConstCellFactory
import dev.toolkt.reactive.cell.test_utils.sampleExternally
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.hold
import dev.toolkt.reactive.event_stream.map
import kotlin.test.Test
import kotlin.test.assertEquals

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
        observationStrategy: CellObservationStrategy,
    ) {
        val doUpdateInner = EmitterEventStream<Int>()

        val outerCell = MomentContext.execute {
            val initialInnerCell = Cell.define(
                initialValue = 10,
                newValues = doUpdateInner,
            )

            outerConstCellFactory.create(initialInnerCell)
        }

        val switchCell = Cell.switch(outerCell)

        val asserter = observationStrategy.observeForTesting(
            doTrigger = doUpdateInner,
            cell = switchCell,
        )

        doUpdateInner.emit(20)

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
    fun test_initialInnerUpdate_outerInert_active() {
        CellObservationChannel.values.forEach { observationChannel ->
            test_initialInnerUpdate(
                outerConstCellFactory = ConstCellFactory.Inert,
                observationStrategy = CellObservationStrategy.Active(
                    observationChannel = observationChannel,
                ),
            )
        }
    }

    @Test
    fun test_initialInnerUpdate_outerDynamic_passive() {
        test_initialInnerUpdate(
            outerConstCellFactory = ConstCellFactory.Dynamic,
            observationStrategy = CellObservationStrategy.Passive,
        )
    }

    @Test
    fun test_initialInnerUpdate_outerDynamic_active() {
        CellObservationChannel.values.forEach { observationChannel ->
            test_initialInnerUpdate(
                outerConstCellFactory = ConstCellFactory.Dynamic,
                observationStrategy = CellObservationStrategy.Active(
                    observationChannel = observationChannel,
                ),
            )
        }
    }

    private fun test_outerUpdate(
        newInnerConstCellFactory: ConstCellFactory,
        observationStrategy: CellObservationStrategy,
    ) {
        val doUpdateOuter = EmitterEventStream<Cell<Int>>()

        val initialInnerCell = Cell.of(10)

        val newInnerCell = MomentContext.execute {
            newInnerConstCellFactory.create(20)
        }

        val outerCell = MomentContext.execute {
            doUpdateOuter.hold(
                initialValue = initialInnerCell,
            )
        }

        val switchCell = Cell.switch(outerCell)

        val asserter = observationStrategy.observeForTesting(
            doTrigger = doUpdateOuter,
            cell = switchCell,
        )

        doUpdateOuter.emit(newInnerCell)

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
    fun test_outerUpdate_newInnerInert_active() {
        CellObservationChannel.values.forEach { observationChannel ->
            test_outerUpdate(
                newInnerConstCellFactory = ConstCellFactory.Inert,
                observationStrategy = CellObservationStrategy.Active(
                    observationChannel = observationChannel,
                ),
            )
        }
    }

    @Test
    fun test_outerUpdate_newInnerDynamic_passive() {
        test_outerUpdate(
            newInnerConstCellFactory = ConstCellFactory.Dynamic,
            observationStrategy = CellObservationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_newInnerDynamic_active() {
        CellObservationChannel.values.forEach { observationChannel ->
            test_outerUpdate(
                newInnerConstCellFactory = ConstCellFactory.Dynamic,
                observationStrategy = CellObservationStrategy.Active(
                    observationChannel = observationChannel,
                ),
            )
        }
    }

    private fun test_outerUpdate_thenInitialInnerUpdate(
        newInnerConstCellFactory: ConstCellFactory,
        observationStrategy: CellObservationStrategy,
    ) {
        val doUpdateOuter = EmitterEventStream<Cell<Int>>()

        val doUpdateInitialInner = EmitterEventStream<Int>()

        val initialInnerCell = MutableCell(
            initialValue = 10,
        )

        val newInnerCell = MomentContext.execute {
            newInnerConstCellFactory.create(20)
        }

        val outerCell = MomentContext.execute {
            doUpdateOuter.hold(
                initialValue = initialInnerCell,
            )
        }

        val switchCell = Cell.switch(outerCell)

        val asserter = observationStrategy.observeForTesting(
            doTrigger = EventStream.merge2(
                doUpdateOuter,
                doUpdateInitialInner,
            ),
            cell = switchCell,
        )

        doUpdateOuter.emit(newInnerCell)

        asserter.assertUpdatedValueEquals(
            expectedUpdatedValue = 20,
        )

        doUpdateInitialInner.emit(11)

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
    fun test_outerUpdate_thenInitialInnerUpdate_newInnerInert_active() {
        CellObservationChannel.values.forEach { observationChannel ->
            test_outerUpdate_thenInitialInnerUpdate(
                newInnerConstCellFactory = ConstCellFactory.Inert,
                observationStrategy = CellObservationStrategy.Active(
                    observationChannel = observationChannel,
                ),
            )
        }
    }

    @Test
    fun test_outerUpdate_thenInitialInnerUpdate_newInnerDynamic_passive() {
        test_outerUpdate_thenInitialInnerUpdate(
            newInnerConstCellFactory = ConstCellFactory.Dynamic,
            observationStrategy = CellObservationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_thenInitialInnerUpdate_newInnerDynamic_active() {
        CellObservationChannel.values.forEach { observationChannel ->
            test_outerUpdate_thenInitialInnerUpdate(
                newInnerConstCellFactory = ConstCellFactory.Dynamic,
                observationStrategy = CellObservationStrategy.Active(
                    observationChannel = observationChannel,
                ),
            )
        }
    }

    private fun test_outerUpdate_thenNewInnerUpdate(
        initialInnerConstCellFactory: ConstCellFactory,
        observationStrategy: CellObservationStrategy,
    ) {
        val doUpdateOuter = EmitterEventStream<Cell<Int>>()

        val doUpdateNewInner = EmitterEventStream<Int>()

        val initialInnerCell = MomentContext.execute {
            initialInnerConstCellFactory.create(10)
        }

        val newInnerCell = MomentContext.execute {
            doUpdateNewInner.hold(
                initialValue = 20,
            )
        }

        val outerCell = MomentContext.execute {
            doUpdateOuter.hold(
                initialValue = initialInnerCell,
            )
        }

        val switchCell = Cell.switch(outerCell)

        val asserter = observationStrategy.observeForTesting(
            doTrigger = EventStream.merge2(
                doUpdateOuter,
                doUpdateNewInner,
            ),
            cell = switchCell,
        )

        doUpdateOuter.emit(newInnerCell)

        asserter.assertUpdatedValueEquals(
            expectedUpdatedValue = 20,
        )

        doUpdateNewInner.emit(21)

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
    fun test_outerUpdate_thenNewInnerUpdate_newInnerInert_active() {
        CellObservationChannel.values.forEach { observationChannel ->
            test_outerUpdate_thenNewInnerUpdate(
                initialInnerConstCellFactory = ConstCellFactory.Inert,
                observationStrategy = CellObservationStrategy.Active(
                    observationChannel = observationChannel,
                ),
            )
        }
    }

    @Test
    fun test_outerUpdate_thenNewInnerUpdate_newInnerDynamic_passive() {
        test_outerUpdate_thenNewInnerUpdate(
            initialInnerConstCellFactory = ConstCellFactory.Dynamic,
            observationStrategy = CellObservationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_thenNewInnerUpdate_newInnerDynamic_active() {
        CellObservationChannel.values.forEach { observationChannel ->
            test_outerUpdate_thenNewInnerUpdate(
                initialInnerConstCellFactory = ConstCellFactory.Dynamic,
                observationStrategy = CellObservationStrategy.Active(
                    observationChannel = observationChannel,
                ),
            )
        }
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

        val asserter = observationStrategy.observeForTesting(
            doTrigger = doSwitch,
            cell = switchCell,
        )

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
    fun test_outerUpdate_simultaneousInitialInnerUpdate_newInnerInert_active() {
        CellObservationChannel.values.forEach { observationChannel ->
            test_outerUpdate_simultaneousInitialInnerUpdate(
                newInnerConstCellFactory = ConstCellFactory.Inert,
                observationStrategy = CellObservationStrategy.Active(
                    observationChannel = observationChannel,
                ),
            )
        }
    }

    @Test
    fun test_outerUpdate_simultaneousInitialInnerUpdate_newInnerDynamic_passive() {
        test_outerUpdate_simultaneousInitialInnerUpdate(
            newInnerConstCellFactory = ConstCellFactory.Dynamic,
            observationStrategy = CellObservationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_simultaneousInitialInnerUpdate_newInnerDynamic_active() {
        CellObservationChannel.values.forEach { observationChannel ->
            test_outerUpdate_simultaneousInitialInnerUpdate(
                newInnerConstCellFactory = ConstCellFactory.Dynamic,
                observationStrategy = CellObservationStrategy.Active(
                    observationChannel = observationChannel,
                ),
            )
        }
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

        val asserter = observationStrategy.observeForTesting(
            doTrigger = doSwitch,
            cell = switchCell,
        )

        doSwitch.emit(Unit)

        asserter.assertUpdatedValueEquals(
            expectedUpdatedValue = 21,
        )
    }

    @Test
    fun test_outerUpdate_simultaneousNewInnerUpdate_newInnerInert_passive() {
        test_outerUpdate_simultaneousNewInnerUpdate(
            initialInnerConstCellFactory = ConstCellFactory.Inert,
            observationStrategy = CellObservationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_simultaneousNewInnerUpdate_newInnerInert_active() {
        CellObservationChannel.values.forEach { observationChannel ->
            test_outerUpdate_simultaneousNewInnerUpdate(
                initialInnerConstCellFactory = ConstCellFactory.Inert,
                observationStrategy = CellObservationStrategy.Active(
                    observationChannel = observationChannel,
                ),
            )
        }
    }

    @Test
    fun test_outerUpdate_simultaneousNewInnerUpdate_newInnerDynamic_passive() {
        test_outerUpdate_simultaneousNewInnerUpdate(
            initialInnerConstCellFactory = ConstCellFactory.Dynamic,
            observationStrategy = CellObservationStrategy.Passive,
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

        val asserter = observationStrategy.observeForTesting(
            doTrigger = doSwitch,
            cell = switchCell,
        )

        doSwitch.emit(Unit)

        asserter.assertUpdatedValueEquals(
            expectedUpdatedValue = 21,
        )
    }


    @Test
    fun test_outerUpdate_simultaneousNewInnerUpdate_newInnerDynamic_active() {
        CellObservationChannel.values.forEach { observationChannel ->
            test_outerUpdate_simultaneousNewInnerUpdate(
                initialInnerConstCellFactory = ConstCellFactory.Dynamic,
                observationStrategy = CellObservationStrategy.Active(
                    observationChannel = observationChannel,
                ),
            )
        }
    }

    @Test
    fun test_outerUpdate_simultaneousBothInnerUpdates_passive() {
        test_outerUpdate_simultaneousBothInnerUpdates(
            observationStrategy = CellObservationStrategy.Passive,
        )
    }

    @Test
    fun test_outerUpdate_simultaneousBothInnerUpdates_active() {
        CellObservationChannel.values.forEach { observationChannel ->
            test_outerUpdate_simultaneousBothInnerUpdates(
                observationStrategy = CellObservationStrategy.Active(
                    observationChannel = observationChannel,
                ),
            )
        }
    }
}
