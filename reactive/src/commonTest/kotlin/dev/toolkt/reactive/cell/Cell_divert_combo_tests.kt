package dev.toolkt.reactive.cell

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.test_utils.DynamicCellFactory
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
        outerCellFactory: DynamicCellFactory,
        initialInnerEventStreamFactory: QuietEventStreamFactory,
        newInnerEventStreamFactory: QuietEventStreamFactory,
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        val doUpdateOuter = EmitterEventStream<Unit>()

        val initialInnerEventStream = initialInnerEventStreamFactory.createExternally<Int>()

        val newInnerEventStream = newInnerEventStreamFactory.createExternally<Int>()

        val outerCell = outerCellFactory.createExternally(
            initialValue = initialInnerEventStream,
            doUpdate = doUpdateOuter.map { newInnerEventStream },
        )

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
        DynamicCellFactory.values.forEach { outerCellFactory ->
            QuietEventStreamFactory.values.forEach { initialInnerEventStreamFactory ->
                QuietEventStreamFactory.values.forEach { newInnerEventStreamFactory ->
                    test_outerUpdate(
                        outerCellFactory = outerCellFactory,
                        initialInnerEventStreamFactory = initialInnerEventStreamFactory,
                        newInnerEventStreamFactory = newInnerEventStreamFactory,
                        verificationStrategy = verificationStrategy,
                    )
                }
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
        outerCellFactory: DynamicCellFactory,
        innerEventStreamFactory: QuietEventStreamFactory,
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        val doUpdateOuter = EmitterEventStream<Unit>()

        val innerEventStream = innerEventStreamFactory.createExternally<Int>()

        // Replaced Cell.define with outerCellFactory
        val outerCell = outerCellFactory.createExternally(
            initialValue = innerEventStream,
            doUpdate = doUpdateOuter.map { innerEventStream },
        )

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
        DynamicCellFactory.values.forEach { outerCellFactory ->
            QuietEventStreamFactory.values.forEach { innerEventStreamFactory ->
                test_outerUpdate_sameEventStream(
                    outerCellFactory = outerCellFactory,
                    innerEventStreamFactory = innerEventStreamFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
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
        outerCellFactory: DynamicCellFactory,
        newInnerEventStreamFactory: QuietEventStreamFactory,
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        val doUpdateOuter = EmitterEventStream<Unit>()

        val doUpdateInitialInner = EmitterEventStream<Unit>()

        val initialInnerEventStream = doUpdateInitialInner.map { 11 }

        val newInnerEventStream = newInnerEventStreamFactory.createExternally<Int>()

        val outerCell = outerCellFactory.createExternally(
            initialValue = initialInnerEventStream,
            doUpdate = doUpdateOuter.map { newInnerEventStream },
        )

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
        DynamicCellFactory.values.forEach { outerCellFactory ->
            QuietEventStreamFactory.values.forEach { newInnerEventStreamFactory ->
                test_outerUpdate_thenInitialInnerOccurrence(
                    outerCellFactory = outerCellFactory,
                    newInnerEventStreamFactory = newInnerEventStreamFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
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
        outerCellFactory: DynamicCellFactory,
        initialInnerEventStreamFactory: QuietEventStreamFactory,
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        val doUpdateOuter = EmitterEventStream<Unit>()

        val doTriggerNewInner = EmitterEventStream<Unit>()

        val initialInnerEventStream = initialInnerEventStreamFactory.createExternally<Int>()

        val newInnerEventStream = doTriggerNewInner.map { 21 }

        val outerCell = outerCellFactory.createExternally(
            initialValue = initialInnerEventStream,
            doUpdate = doUpdateOuter.map { newInnerEventStream },
        )

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
        DynamicCellFactory.values.forEach { outerCellFactory ->
            QuietEventStreamFactory.values.forEach { initialInnerEventStreamFactory ->
                test_outerUpdate_thenNewInnerUpdate(
                    outerCellFactory = outerCellFactory,
                    initialInnerEventStreamFactory = initialInnerEventStreamFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
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
        outerCellFactory: DynamicCellFactory,
        newInnerEventStreamFactory: QuietEventStreamFactory,
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        val doDivert = EmitterEventStream<Unit>()

        val initialInnerEventStream = doDivert.map { 11 }

        val newInnerEventStream = newInnerEventStreamFactory.createExternally<Int>()

        val outerCell = outerCellFactory.createExternally(
            initialValue = initialInnerEventStream,
            doUpdate = doDivert.map { newInnerEventStream },
        )

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
        DynamicCellFactory.values.forEach { outerCellFactory ->
            QuietEventStreamFactory.values.forEach { newInnerEventStreamFactory ->
                test_outerUpdate_simultaneousInitialInnerOccurrence(
                    outerCellFactory = outerCellFactory,
                    newInnerEventStreamFactory = newInnerEventStreamFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
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
        outerCellFactory: DynamicCellFactory,
        initialInnerEventStreamFactory: QuietEventStreamFactory,
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        val doDivert = EmitterEventStream<Unit>()

        val initialInnerEventStream = initialInnerEventStreamFactory.createExternally<Int>()

        val newInnerEventStream = doDivert.map { 21 }

        val outerCell = outerCellFactory.createExternally(
            initialValue = initialInnerEventStream,
            doUpdate = doDivert.map { newInnerEventStream },
        )

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
        DynamicCellFactory.values.forEach { outerCellFactory ->
            QuietEventStreamFactory.values.forEach { initialInnerEventStreamFactory ->
                test_outerUpdate_simultaneousNewInnerUpdate(
                    outerCellFactory = outerCellFactory,
                    initialInnerEventStreamFactory = initialInnerEventStreamFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
        }
    }

    @Test
    fun test_outerUpdate_simultaneousNewInnerUpdate() {
        EventStreamVerificationStrategy.values.forEach { verificationStrategy ->
            test_outerUpdate_simultaneousNewInnerUpdate(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_outerUpdate_simultaneousBothInnerUpdates(
        outerCellFactory: DynamicCellFactory,
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        val doDivert = EmitterEventStream<Unit>()

        val initialInnerEventStream = doDivert.map { 11 }

        val newInnerEventStream = doDivert.map { 21 }

        val outerCell = outerCellFactory.createExternally(
            initialValue = initialInnerEventStream,
            doUpdate = doDivert.map { newInnerEventStream },
        )

        val divertCell = Cell.divert(outerCell)

        val verifier = verificationStrategy.begin(
            subjectEventStream = divertCell,
        )

        verifier.verifyOccurrencePropagates(
            doTrigger = doDivert,
            expectedPropagatedEvent = 11,
        )
    }

    private fun test_outerUpdate_simultaneousBothInnerUpdates(
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { outerCellFactory ->
            test_outerUpdate_simultaneousBothInnerUpdates(
                outerCellFactory = outerCellFactory,
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_outerUpdate_simultaneousBothInnerUpdates() {
        EventStreamVerificationStrategy.values.forEach { verificationStrategy ->
            test_outerUpdate_simultaneousBothInnerUpdates(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    private fun test_pausing_initial(
        // Added
        outerCellFactory: DynamicCellFactory,
        newOuterCellsEventStreamFactory: QuietEventStreamFactory,
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val initialInnerEventStream = doTrigger.map { 21 }

        val newInnerEventStreams = newOuterCellsEventStreamFactory.createExternally<EventStream<Int>>()

        // Replaced Cell.define with outerCellFactory
        val outerCell = outerCellFactory.createExternally(
            initialValue = initialInnerEventStream,
            doUpdate = newInnerEventStreams,
        )

        val divertCell = Cell.divert(outerCell)

        verificationStrategy.verifyPausing(
            subjectEventStream = divertCell,
            doTrigger = doTrigger,
        )
    }

    private fun test_pausing_initial(
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        DynamicCellFactory.values.forEach { outerCellFactory ->
            QuietEventStreamFactory.values.forEach { newOuterCellsEventStreamFactory ->
                test_pausing_initial(
                    outerCellFactory = outerCellFactory,
                    newOuterCellsEventStreamFactory = newOuterCellsEventStreamFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
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
        outerCellFactory: DynamicCellFactory,
        innerEventStreamFactory: QuietEventStreamFactory,
        verificationStrategy: EventStreamVerificationStrategy,
    ) {
        val doPrepare = EmitterEventStream<Unit>()

        val doTrigger = EmitterEventStream<Unit>()

        val initialInnerEventStream = innerEventStreamFactory.createExternally<Int>()

        val newInnerEventStream = doTrigger.map { 21 }

        val outerCell = outerCellFactory.createExternally(
            initialValue = initialInnerEventStream,
            doUpdate = doPrepare.map { newInnerEventStream },
        )

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
        DynamicCellFactory.values.forEach { outerCellFactory ->
            QuietEventStreamFactory.values.forEach { innerEventStreamFactory ->
                test_pausing_afterOuterUpdate(
                    outerCellFactory = outerCellFactory,
                    innerEventStreamFactory = innerEventStreamFactory,
                    verificationStrategy = verificationStrategy,
                )
            }
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
