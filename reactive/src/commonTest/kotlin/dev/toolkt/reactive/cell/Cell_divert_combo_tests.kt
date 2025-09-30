package dev.toolkt.reactive.cell

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.test_utils.ConstCellFactory
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.emit
import dev.toolkt.reactive.event_stream.map
import dev.toolkt.reactive.event_stream.test_utils.EventStreamVerificationStrategy
import kotlin.test.Ignore
import kotlin.test.Test

@Suppress("ClassName")
class Cell_divert_combo_tests {
    private fun test_initialInnerOccurrence(
        outerConstCellFactory: ConstCellFactory,
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        val doTriggerInner = EmitterEventStream<Unit>()

        val outerCell = MomentContext.execute {
            val initialInnerEventStream = doTriggerInner.map { 20 }

            outerConstCellFactory.create(initialInnerEventStream)
        }

        val divertCell = Cell.divert(outerCell)

        val verifier = verificationStrategy.begin(
            subjectEventStream = divertCell,
        )

        verifier.verifyOccurrencePropagates(
            doTrigger = doTriggerInner,
            expectedPropagatedEvent = 20,
        )
    }

    private fun test_initialInnerOccurrence(
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        ConstCellFactory.values.forEach { outerConstCellFactory ->
            test_initialInnerOccurrence(
                outerConstCellFactory = outerConstCellFactory,
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_initialInnerOccurrence() {
        EventStreamVerificationStrategy.values.forEach { verificationStrategy ->
            test_initialInnerOccurrence(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_outerUpdate(
        verificationStrategy: EventStreamVerificationStrategy,
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

        val verifier = verificationStrategy.begin(
            subjectEventStream = divertCell,
        )

        verifier.verifyOccurrenceDoesNotPropagate(
            doTrigger = doUpdateOuter,
        )
    }

    @Test
    fun test_outerUpdate() {
        EventStreamVerificationStrategy.values.forEach { verificationStrategy ->
            test_outerUpdate(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_outerUpdate_sameEventStream(
        verificationStrategy: EventStreamVerificationStrategy,
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

        val verifier = verificationStrategy.begin(
            subjectEventStream = divertCell,
        )

        verifier.verifyOccurrenceDoesNotPropagate(
            doTrigger = doUpdateOuter,
        )
    }

    @Test
    fun test_outerUpdate_sameEventStream() {
        EventStreamVerificationStrategy.values.forEach { verificationStrategy ->
            test_outerUpdate_sameEventStream(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_outerUpdate_thenInitialInnerOccurrence(
        verificationStrategy: EventStreamVerificationStrategy,
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

        val verifier = verificationStrategy.begin(
            subjectEventStream = divertCell,
        )

        doUpdateOuter.emit()

        verifier.verifyOccurrenceDoesNotPropagate(
            doTrigger = doUpdateInitialInner,
        )
    }

    @Test
    fun test_outerUpdate_thenInitialInnerOccurrence_active() {
        EventStreamVerificationStrategy.values.forEach { verificationStrategy ->
            test_outerUpdate_thenInitialInnerOccurrence(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_outerUpdate_thenNewInnerUpdate(
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        val doUpdateOuter = EmitterEventStream<Unit>()

        val doTriggerNewInner = EmitterEventStream<Unit>()

        val initialInnerEventStream = EmitterEventStream<Int>()

        val newInnerEventStream = doTriggerNewInner.map { 21 }

        val outerCell = MomentContext.execute {
            Cell.define(
                initialInnerEventStream,
                doUpdateOuter.map { newInnerEventStream },
            )
        }

        val divertCell = Cell.divert(outerCell)

        val verifier = verificationStrategy.begin(
            subjectEventStream = divertCell,
        )

        doUpdateOuter.emit()

        verifier.verifyOccurrencePropagates(
            doTrigger = doTriggerNewInner,
            expectedPropagatedEvent = 21,
        )
    }

    @Test
    fun test_outerUpdate_thenNewInnerUpdate() {
        EventStreamVerificationStrategy.values.forEach { verificationStrategy ->
            test_outerUpdate_thenNewInnerUpdate(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_outerUpdate_simultaneousInitialInnerOccurrence(
        verificationStrategy: EventStreamVerificationStrategy,
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

        val verifier = verificationStrategy.begin(
            subjectEventStream = divertCell,
        )

        verifier.verifyOccurrencePropagates(
            doTrigger = doDivert,
            expectedPropagatedEvent = 11,
        )
    }

    @Test
    fun test_outerUpdate_simultaneousInitialInnerOccurrence_active() {
        EventStreamVerificationStrategy.values.forEach { verificationStrategy ->
            test_outerUpdate_simultaneousInitialInnerOccurrence(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_outerUpdate_simultaneousNewInnerUpdate(
        verificationStrategy: EventStreamVerificationStrategy,
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

        val verifier = verificationStrategy.begin(
            subjectEventStream = divertCell,
        )

        verifier.verifyOccurrenceDoesNotPropagate(
            doTrigger = doDivert,
        )
    }

    @Test
    fun test_outerUpdate_simultaneousNewInnerUpdate_active() {
        EventStreamVerificationStrategy.values.forEach { verificationStrategy ->
            test_outerUpdate_simultaneousNewInnerUpdate(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_outerUpdate_simultaneousBothInnerUpdates(
        verificationStrategy: EventStreamVerificationStrategy,
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

        val verifier = verificationStrategy.begin(
            subjectEventStream = divertCell,
        )

        verifier.verifyOccurrencePropagates(
            doTrigger = doDivert,
            expectedPropagatedEvent = 11,
        )
    }

    @Test
    fun test_outerUpdate_simultaneousBothInnerUpdates_active() {
        EventStreamVerificationStrategy.values.forEach { verificationStrategy ->
            test_outerUpdate_simultaneousBothInnerUpdates(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_deactivation_initial(
        verificationStrategy: EventStreamVerificationStrategy,
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

        verificationStrategy.verifyDeactivation(
            subjectEventStream = divertCell,
            doTrigger = doTrigger,
        )
    }

    @Ignore // FIXME: Flaky test
    @Test
    fun test_deactivation_initial() {
        EventStreamVerificationStrategy.values.forEach { verificationStrategy ->
            test_deactivation_initial(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_deactivation_afterOuterUpdate(
        verificationStrategy: EventStreamVerificationStrategy,
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

        verificationStrategy.verifyDeactivation(
            subjectEventStream = divertCell,
            doTrigger = doTrigger,
        )
    }

    @Ignore // FIXME: Flaky test
    @Test
    fun test_deactivation_afterOuterUpdate() {
        EventStreamVerificationStrategy.values.forEach { verificationStrategy ->
            test_deactivation_afterOuterUpdate(
                verificationStrategy = verificationStrategy,
            )
        }
    }
}
