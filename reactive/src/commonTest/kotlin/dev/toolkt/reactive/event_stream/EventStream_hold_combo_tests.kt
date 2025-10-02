package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.test_utils.CellVerificationStrategy
import dev.toolkt.reactive.cell.test_utils.QuietEventStreamFactory
import kotlin.test.Ignore
import kotlin.test.Test

@Suppress("ClassName")
class EventStream_hold_combo_tests {
    private fun test_initial(
        sourceEventStreamFactory: QuietEventStreamFactory,
        verificationStrategy: CellVerificationStrategy.Total,
    ) {
        val sourceEventStream = MomentContext.execute {
            sourceEventStreamFactory.create<Int>()
        }

        val holdCell = MomentContext.execute {
            sourceEventStream.hold(initialValue = 10)
        }

        verificationStrategy.begin(
            subjectCell = holdCell,
        ).verifyCurrentValue(
            expectedCurrentValue = 10,
        )
    }

    private fun test_initial(
        verificationStrategy: CellVerificationStrategy.Total,
    ) {
        QuietEventStreamFactory.values.forEach { sourceEventStreamFactory ->
            test_initial(
                sourceEventStreamFactory = sourceEventStreamFactory,
                verificationStrategy = verificationStrategy,
            )
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

    private fun test_sourceOccurrence(
        verificationStrategy: CellVerificationStrategy,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceEventStream = doTrigger.map { 11 }

        val holdCell = MomentContext.execute {
            sourceEventStream.hold(initialValue = 10)
        }

        val verifier = verificationStrategy.begin(
            subjectCell = holdCell,
        )

        verifier.verifyUpdates(
            doTrigger = doTrigger,
            expectedUpdatedValue = 11,
        )
    }

    @Test
    fun test_sourceOccurrence_passive() {
        test_sourceOccurrence(
            verificationStrategy = CellVerificationStrategy.Passive,
        )
    }

    @Test
    fun test_sourceOccurrence_active() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_sourceOccurrence(
                verificationStrategy = verificationStrategy,
            )
        }
    }

    @Test
    fun test_sourceOccurrence_quick() {
        test_sourceOccurrence(
            verificationStrategy = CellVerificationStrategy.Quick,
        )
    }

    private fun test_deactivation(
        verificationStrategy: CellVerificationStrategy.Active,
    ) {
        val doTrigger = EmitterEventStream<Unit>()

        val sourceEventStream = doTrigger.map { 11 }

        val holdCell = MomentContext.execute {
            sourceEventStream.hold(initialValue = 10)
        }

        verificationStrategy.verifyDeactivation(
            subjectCell = holdCell,
            doTrigger = doTrigger,
        )
    }

    @Test
    fun test_deactivation() {
        CellVerificationStrategy.Active.values.forEach { verificationStrategy ->
            test_deactivation(
                verificationStrategy = verificationStrategy,
            )
        }
    }
}
