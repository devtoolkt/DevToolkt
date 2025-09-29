package dev.toolkt.reactive.cell

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.test_utils.ConstCellFactory
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.emit
import dev.toolkt.reactive.event_stream.map
import dev.toolkt.reactive.event_stream.test_utils.OccurrenceVerificationStrategy
import kotlin.test.Ignore
import kotlin.test.Test

@Suppress("ClassName")
class Cell_divert_combo_tests {
    private fun test_initialInnerOccurrence(
        outerConstCellFactory: ConstCellFactory,
        occurrenceVerificationStrategy: OccurrenceVerificationStrategy,
    ) {
        val doOccurInner = EmitterEventStream<Unit>()

        val outerCell = MomentContext.execute {
            val initialInnerEventStream = doOccurInner.map { 20 }

            outerConstCellFactory.create(initialInnerEventStream)
        }

        val divertCell = Cell.divert(outerCell)

        val occurrenceVerifier = occurrenceVerificationStrategy.begin(
            subjectEventStream = divertCell,
        )

        occurrenceVerifier.verifyOccurrencePropagated(
            doOccur = doOccurInner,
            expectedPropagatedEvent = 20,
        )
    }

    private fun test_initialInnerOccurrence(
        occurrenceVerificationStrategy: OccurrenceVerificationStrategy,
    ) {
        ConstCellFactory.values.forEach { outerConstCellFactory ->
            test_initialInnerOccurrence(
                outerConstCellFactory = outerConstCellFactory,
                occurrenceVerificationStrategy = occurrenceVerificationStrategy,
            )
        }
    }

    @Test
    fun test_initialInnerOccurrence() {
        OccurrenceVerificationStrategy.values.forEach { occurrenceVerificationStrategy ->
            test_initialInnerOccurrence(
                occurrenceVerificationStrategy = occurrenceVerificationStrategy,
            )
        }
    }

    private fun test_outerUpdate(
        occurrenceVerificationStrategy: OccurrenceVerificationStrategy,
    ) {
        val doUpdateOuter = EmitterEventStream<Unit>()

        val initialInnerEventStream = EmitterEventStream<Int>()

        val newInnerEventStream = EmitterEventStream<Int>()

        val outerCell = MomentContext.execute {
            Cell.define(
                initialInnerEventStream,
                doUpdateOuter.map { newInnerEventStream },
            )
        }

        val divertCell = Cell.divert(outerCell)

        val occurrenceVerifier = occurrenceVerificationStrategy.begin(
            subjectEventStream = divertCell,
        )

        occurrenceVerifier.verifyOccurrenceDidNotPropagate(
            doTrigger = doUpdateOuter,
        )
    }

    @Test
    fun test_outerUpdate() {
        OccurrenceVerificationStrategy.values.forEach { occurrenceVerificationStrategy ->
            test_outerUpdate(
                occurrenceVerificationStrategy = occurrenceVerificationStrategy,
            )
        }
    }

    private fun test_outerUpdate_sameEventStream(
        occurrenceVerificationStrategy: OccurrenceVerificationStrategy,
    ) {
        val doUpdateOuter = EmitterEventStream<Unit>()

        val innerEventStream = EmitterEventStream<Int>()

        val outerCell = MomentContext.execute {
            Cell.define(
                innerEventStream,
                doUpdateOuter.map { innerEventStream },
            )
        }

        val divertCell = Cell.divert(outerCell)

        val occurrenceVerifier = occurrenceVerificationStrategy.begin(
            subjectEventStream = divertCell,
        )

        occurrenceVerifier.verifyOccurrenceDidNotPropagate(
            doTrigger = doUpdateOuter,
        )
    }

    @Test
    fun test_outerUpdate_sameEventStream() {
        OccurrenceVerificationStrategy.values.forEach { occurrenceVerificationStrategy ->
            test_outerUpdate_sameEventStream(
                occurrenceVerificationStrategy = occurrenceVerificationStrategy,
            )
        }
    }

    private fun test_outerUpdate_thenInitialInnerOccurrence(
        occurrenceVerificationStrategy: OccurrenceVerificationStrategy,
    ) {
        val doUpdateOuter = EmitterEventStream<Unit>()

        val doUpdateInitialInner = EmitterEventStream<Unit>()

        val initialInnerEventStream = EmitterEventStream<Int>()

        val newInnerEventStream = EmitterEventStream<Int>()

        val outerCell = MomentContext.execute {
            Cell.define(
                initialValue = initialInnerEventStream,
                newValues = doUpdateOuter.map { newInnerEventStream },
            )
        }

        val divertCell = Cell.divert(outerCell)

        val occurrenceVerifier = occurrenceVerificationStrategy.begin(
            subjectEventStream = divertCell,
        )

        doUpdateOuter.emit()

        occurrenceVerifier.verifyOccurrenceDidNotPropagate(
            doTrigger = doUpdateInitialInner,
        )
    }

    @Test
    fun test_outerUpdate_thenInitialInnerOccurrence_active() {
        OccurrenceVerificationStrategy.values.forEach { occurrenceVerificationStrategy ->
            test_outerUpdate_thenInitialInnerOccurrence(
                occurrenceVerificationStrategy = occurrenceVerificationStrategy,
            )
        }
    }

    private fun test_outerUpdate_thenNewInnerUpdate(
        occurrenceVerificationStrategy: OccurrenceVerificationStrategy,
    ) {
        val doUpdateOuter = EmitterEventStream<Unit>()

        val doOccurNewInner = EmitterEventStream<Unit>()

        val initialInnerEventStream = EmitterEventStream<Int>()

        val newInnerEventStream = doOccurNewInner.map { 21 }

        val outerCell = MomentContext.execute {
            Cell.define(
                initialInnerEventStream,
                doUpdateOuter.map { newInnerEventStream },
            )
        }

        val divertCell = Cell.divert(outerCell)

        val occurrenceVerifier = occurrenceVerificationStrategy.begin(
            subjectEventStream = divertCell,
        )

        doUpdateOuter.emit()

        occurrenceVerifier.verifyOccurrencePropagated(
            doOccur = doOccurNewInner,
            expectedPropagatedEvent = 21,
        )
    }

    @Test
    fun test_outerUpdate_thenNewInnerUpdate() {
        OccurrenceVerificationStrategy.values.forEach { occurrenceVerificationStrategy ->
            test_outerUpdate_thenNewInnerUpdate(
                occurrenceVerificationStrategy = occurrenceVerificationStrategy,
            )
        }
    }

    private fun test_outerUpdate_simultaneousInitialInnerOccurrence(
        occurrenceVerificationStrategy: OccurrenceVerificationStrategy,
    ) {
        val doDivert = EmitterEventStream<Unit>()

        val divertCell = MomentContext.execute {
            val initialInnerEventStream = doDivert.map { 11 }

            val newInnerEventStream = EmitterEventStream<Int>()

            val outerCell = Cell.define(
                initialValue = initialInnerEventStream,
                newValues = doDivert.map { newInnerEventStream },
            )

            Cell.divert(outerCell)
        }

        val occurrenceVerifier = occurrenceVerificationStrategy.begin(
            subjectEventStream = divertCell,
        )

        occurrenceVerifier.verifyOccurrencePropagated(
            doOccur = doDivert,
            expectedPropagatedEvent = 11,
        )
    }

    @Test
    fun test_outerUpdate_simultaneousInitialInnerOccurrence_active() {
        OccurrenceVerificationStrategy.values.forEach { occurrenceVerificationStrategy ->
            test_outerUpdate_simultaneousInitialInnerOccurrence(
                occurrenceVerificationStrategy = occurrenceVerificationStrategy,
            )
        }
    }

    private fun test_outerUpdate_simultaneousNewInnerUpdate(
        occurrenceVerificationStrategy: OccurrenceVerificationStrategy,
    ) {
        val doDivert = EmitterEventStream<Unit>()

        val divertCell = MomentContext.execute {
            val initialInnerEventStream = EmitterEventStream<Int>()

            val newInnerEventStream = doDivert.map { 21 }

            val outerCell = Cell.define(
                initialValue = initialInnerEventStream,
                newValues = doDivert.map { newInnerEventStream },
            )

            Cell.divert(outerCell)
        }

        val occurrenceVerifier = occurrenceVerificationStrategy.begin(
            subjectEventStream = divertCell,
        )

        occurrenceVerifier.verifyOccurrenceDidNotPropagate(
            doTrigger = doDivert,
        )
    }

    @Test
    fun test_outerUpdate_simultaneousNewInnerUpdate_active() {
        OccurrenceVerificationStrategy.values.forEach { occurrenceVerificationStrategy ->
            test_outerUpdate_simultaneousNewInnerUpdate(
                occurrenceVerificationStrategy = occurrenceVerificationStrategy,
            )
        }
    }

    private fun test_outerUpdate_simultaneousBothInnerUpdates(
        occurrenceVerificationStrategy: OccurrenceVerificationStrategy,
    ) {
        val doDivert = EmitterEventStream<Unit>()

        val divertCell = MomentContext.execute {
            val initialInnerEventStream = doDivert.map { 11 }

            val newInnerEventStream = doDivert.map { 21 }

            val outerCell = Cell.define(
                initialValue = initialInnerEventStream,
                newValues = doDivert.map { newInnerEventStream },
            )

            Cell.divert(outerCell)
        }

        val occurrenceVerifier = occurrenceVerificationStrategy.begin(
            subjectEventStream = divertCell,
        )

        occurrenceVerifier.verifyOccurrencePropagated(
            doOccur = doDivert,
            expectedPropagatedEvent = 11,
        )
    }

    @Test
    fun test_outerUpdate_simultaneousBothInnerUpdates_active() {
        OccurrenceVerificationStrategy.values.forEach { occurrenceVerificationStrategy ->
            test_outerUpdate_simultaneousBothInnerUpdates(
                occurrenceVerificationStrategy = occurrenceVerificationStrategy,
            )
        }
    }

    private fun test_deactivation_initial(
        occurrenceVerificationStrategy: OccurrenceVerificationStrategy,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val divertCell = MomentContext.execute {
            val initialInnerEventStream = doTrigger.map { 21 }

            val outerCell = Cell.define(
                initialValue = initialInnerEventStream,
                newValues = EmitterEventStream(),
            )

            Cell.divert(outerCell)
        }

        occurrenceVerificationStrategy.verifyDeactivation(
            subjectEventStream = divertCell,
            doTrigger = doTrigger,
        )
    }

    @Ignore // FIXME: Flaky test
    @Test
    fun test_deactivation_initial() {
        OccurrenceVerificationStrategy.values.forEach { occurrenceVerificationStrategy ->
            test_deactivation_initial(
                occurrenceVerificationStrategy = occurrenceVerificationStrategy,
            )
        }
    }

    private fun test_deactivation_afterOuterUpdate(
        occurrenceVerificationStrategy: OccurrenceVerificationStrategy,
    ) {
        val doPrepare = EmitterEventStream<Unit>()

        val doTrigger = EmitterEventStream<Unit>()

        val divertCell = MomentContext.execute {
            val initialInnerEventStream = EmitterEventStream<Int>()

            val newInnerEventStream = doTrigger.map { 21 }

            val outerCell = Cell.define(
                initialValue = initialInnerEventStream,
                newValues = doPrepare.map { newInnerEventStream },
            )

            Cell.divert(outerCell)
        }

        doPrepare.emit()

        occurrenceVerificationStrategy.verifyDeactivation(
            subjectEventStream = divertCell,
            doTrigger = doTrigger,
        )
    }

    @Ignore // FIXME: Flaky test
    @Test
    fun test_deactivation_afterOuterUpdate() {
        OccurrenceVerificationStrategy.values.forEach { occurrenceVerificationStrategy ->
            test_deactivation_afterOuterUpdate(
                occurrenceVerificationStrategy = occurrenceVerificationStrategy,
            )
        }
    }
}
