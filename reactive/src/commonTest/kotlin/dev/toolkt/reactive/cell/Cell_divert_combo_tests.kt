package dev.toolkt.reactive.cell

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.test_utils.QuietEventStreamFactory
import dev.toolkt.reactive.cell.test_utils.StaticCellFactory
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.emit
import dev.toolkt.reactive.event_stream.map
import dev.toolkt.reactive.event_stream.test_utils.EventStreamVerificationStrategy
import kotlin.test.Ignore
import kotlin.test.Test

@Suppress("ClassName")
class Cell_divert_combo_tests {
    private fun test_initialInnerOccurrence(
        outerCellFactory: StaticCellFactory,
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        val doTriggerInner = EmitterEventStream<Unit>()

        val initialInnerEventStream = doTriggerInner.map { 20 }

        val outerCell = outerCellFactory.createExternally(initialInnerEventStream)

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
        StaticCellFactory.values.forEach { outerCellFactory ->
            test_initialInnerOccurrence(
                outerCellFactory = outerCellFactory,
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
        initialInnerEventStreamFactory: QuietEventStreamFactory,
        newInnerEventStreamFactory: QuietEventStreamFactory,
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        val doUpdateOuter = EmitterEventStream<Unit>()

        val initialInnerEventStream = initialInnerEventStreamFactory.createExternally<Int>()

        val newInnerEventStream = newInnerEventStreamFactory.createExternally<Int>()

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

    private fun test_outerUpdate(
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        QuietEventStreamFactory.values.forEach { initialInnerEventStreamFactory ->
            QuietEventStreamFactory.values.forEach { newInnerEventStreamFactory ->
                test_outerUpdate(
                    initialInnerEventStreamFactory = initialInnerEventStreamFactory,
                    newInnerEventStreamFactory = newInnerEventStreamFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
        }
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
        innerEventStreamFactory: QuietEventStreamFactory,
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        val doUpdateOuter = EmitterEventStream<Unit>()

        val innerEventStream = innerEventStreamFactory.createExternally<Int>()

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

    private fun test_outerUpdate_sameEventStream(
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        QuietEventStreamFactory.values.forEach { innerEventStreamFactory ->
            test_outerUpdate_sameEventStream(
                innerEventStreamFactory = innerEventStreamFactory,
                verificationStrategy = verificationStrategy,
            )
        }
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
        newInnerEventStreamFactory: QuietEventStreamFactory,
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        val doUpdateOuter = EmitterEventStream<Unit>()

        val doUpdateInitialInner = EmitterEventStream<Unit>()

        val initialInnerEventStream = doUpdateInitialInner.map { 11 }

        val newInnerEventStream = newInnerEventStreamFactory.createExternally<Int>()

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

    private fun test_outerUpdate_thenInitialInnerOccurrence(
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        QuietEventStreamFactory.values.forEach { newInnerEventStreamFactory ->
            test_outerUpdate_thenInitialInnerOccurrence(
                newInnerEventStreamFactory = newInnerEventStreamFactory,
                verificationStrategy = verificationStrategy,
            )
        }
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
        initialInnerEventStreamFactory: QuietEventStreamFactory,
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        val doUpdateOuter = EmitterEventStream<Unit>()

        val doTriggerNewInner = EmitterEventStream<Unit>()

        val initialInnerEventStream = initialInnerEventStreamFactory.createExternally<Int>()

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

    private fun test_outerUpdate_thenNewInnerUpdate(
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        QuietEventStreamFactory.values.forEach { initialInnerEventStreamFactory ->
            test_outerUpdate_thenNewInnerUpdate(
                initialInnerEventStreamFactory = initialInnerEventStreamFactory,
                verificationStrategy = verificationStrategy,
            )
        }
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
        newInnerEventStreamFactory: QuietEventStreamFactory,
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        val doDivert = EmitterEventStream<Unit>()

        val initialInnerEventStream = doDivert.map { 11 }

        val newInnerEventStream = newInnerEventStreamFactory.createExternally<Int>()

        val outerCell = MomentContext.execute {
            Cell.define(
                initialValue = initialInnerEventStream,
                newValues = doDivert.map { newInnerEventStream },
            )
        }

        val divertCell = Cell.divert(outerCell)

        val verifier = verificationStrategy.begin(
            subjectEventStream = divertCell,
        )

        verifier.verifyOccurrencePropagates(
            doTrigger = doDivert,
            expectedPropagatedEvent = 11,
        )
    }

    private fun test_outerUpdate_simultaneousInitialInnerOccurrence(
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        QuietEventStreamFactory.values.forEach { newInnerEventStreamFactory ->
            test_outerUpdate_simultaneousInitialInnerOccurrence(
                newInnerEventStreamFactory = newInnerEventStreamFactory,
                verificationStrategy = verificationStrategy,
            )
        }
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
        initialInnerEventStreamFactory: QuietEventStreamFactory,
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        val doDivert = EmitterEventStream<Unit>()

        val initialInnerEventStream = initialInnerEventStreamFactory.createExternally<Int>()

        val newInnerEventStream = doDivert.map { 21 }

        val outerCell = MomentContext.execute {
            Cell.define(
                initialValue = initialInnerEventStream,
                newValues = doDivert.map { newInnerEventStream },
            )
        }

        val divertCell = Cell.divert(outerCell)

        val verifier = verificationStrategy.begin(
            subjectEventStream = divertCell,
        )

        verifier.verifyOccurrenceDoesNotPropagate(
            doTrigger = doDivert,
        )
    }

    private fun test_outerUpdate_simultaneousNewInnerUpdate(
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        QuietEventStreamFactory.values.forEach { initialInnerEventStreamFactory ->
            test_outerUpdate_simultaneousNewInnerUpdate(
                initialInnerEventStreamFactory = initialInnerEventStreamFactory,
                verificationStrategy = verificationStrategy,
            )
        }
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

    private fun test_pausing_initial(
        newOuterCellsEventStreamFactory: QuietEventStreamFactory,
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val initialInnerEventStream = doTrigger.map { 21 }

        val newInnerEventStreams = newOuterCellsEventStreamFactory.createExternally<EventStream<Int>>()

        val outerCell = MomentContext.execute {
            Cell.define(
                initialValue = initialInnerEventStream,
                newValues = newInnerEventStreams,
            )
        }

        val divertCell = Cell.divert(outerCell)

        verificationStrategy.verifyPausing(
            subjectEventStream = divertCell,
            doTrigger = doTrigger,
        )
    }

    private fun test_pausing_initial(
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        QuietEventStreamFactory.values.forEach { newOuterCellsEventStreamFactory ->
            test_pausing_initial(
                newOuterCellsEventStreamFactory = newOuterCellsEventStreamFactory,
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Ignore // FIXME: Flaky test
    @Test
    fun test_pausing_initial() {
        EventStreamVerificationStrategy.values.forEach { verificationStrategy ->
            test_pausing_initial(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_pausing_afterOuterUpdate(
        innerEventStreamFactory: QuietEventStreamFactory,
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        val doPrepare = EmitterEventStream<Unit>()

        val doTrigger = EmitterEventStream<Unit>()

        val initialInnerEventStream = innerEventStreamFactory.createExternally<Int>()

        val newInnerEventStream = doTrigger.map { 21 }

        val outerCell = MomentContext.execute {
            Cell.define(
                initialValue = initialInnerEventStream,
                newValues = doPrepare.map { newInnerEventStream },
            )
        }

        val divertCell = Cell.divert(outerCell)

        doPrepare.emit()

        verificationStrategy.verifyPausing(
            subjectEventStream = divertCell,
            doTrigger = doTrigger,
        )
    }

    private fun test_pausing_afterOuterUpdate(
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        QuietEventStreamFactory.values.forEach { innerEventStreamFactory ->
            test_pausing_afterOuterUpdate(
                innerEventStreamFactory = innerEventStreamFactory,
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Ignore // FIXME: Flaky test
    @Test
    fun test_pausing_afterOuterUpdate() {
        EventStreamVerificationStrategy.values.forEach { verificationStrategy ->
            test_pausing_afterOuterUpdate(
                verificationStrategy = verificationStrategy,
            )
        }
    }
}
