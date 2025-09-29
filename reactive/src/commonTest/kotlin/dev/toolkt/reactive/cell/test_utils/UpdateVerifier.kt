package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.MutableCell
import dev.toolkt.reactive.cell.updatedValues
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.emit
import dev.toolkt.reactive.event_stream.subscribe
import kotlin.test.assertEquals

sealed class UpdateVerifier<ValueT> {
    abstract class Total<ValueT>() : UpdateVerifier<ValueT>() {
        abstract fun verifyDoesNotUpdate(
            doTrigger: EmitterEventStream<Unit>,
            expectedNonUpdatedValue: ValueT,
        )
    }

    abstract class Passive<ValueT> : Total<ValueT>()

    abstract class Active<ValueT>(
        private val subjectCell: Cell<ValueT>,
        private val receivedUpdatedValues: List<ValueT>,
    ) : Total<ValueT>() {
        private val receivedUpdateCount: Int
            get() = receivedUpdatedValues.size

        final override fun verifyUpdates(
            doUpdate: EmitterEventStream<Unit>,
            expectedUpdatedValue: ValueT,
        ) {
            verifyUpdatePropagated(
                doUpdate = doUpdate,
                expectedPropagatedUpdatedValue = expectedUpdatedValue,
            )

            val activelySampledValue = subjectCell.sampleExternally()

            assertEquals(
                expected = expectedUpdatedValue,
                actual = activelySampledValue,
            )
        }

        fun verifyUpdatePropagated(
            doUpdate: EmitterEventStream<Unit>,
            expectedPropagatedUpdatedValue: ValueT,
        ) {
            val previousReceivedUpdateCount = receivedUpdateCount

            doUpdate.emit()

            val deltaReceivedUpdateCount = receivedUpdateCount - previousReceivedUpdateCount

            assertEquals(
                expected = 1,
                actual = deltaReceivedUpdateCount,
                message = "Expected a single update, but got $deltaReceivedUpdateCount updates instead."
            )

            val singleUpdatedValue = receivedUpdatedValues.last()

            assertEquals(
                expected = expectedPropagatedUpdatedValue,
                actual = singleUpdatedValue,
                message = "Expected the single update to be $singleUpdatedValue, but got $singleUpdatedValue instead."
            )
        }

        final override fun verifyDoesNotUpdate(
            doTrigger: EmitterEventStream<Unit>,
            expectedNonUpdatedValue: ValueT,
        ) {
            verifyUpdateDidNotPropagate(
                doTrigger = doTrigger,
            )

            val activelySampledValue = subjectCell.sampleExternally()

            assertEquals(
                expected = expectedNonUpdatedValue,
                actual = activelySampledValue,
            )
        }

        fun verifyUpdateDidNotPropagate(
            doTrigger: EmitterEventStream<Unit>,
        ) {
            val previousReceivedUpdateCount = receivedUpdateCount

            doTrigger.emit()

            val deltaReceivedUpdateCount = receivedUpdateCount - previousReceivedUpdateCount

            assertEquals(
                expected = 0,
                actual = deltaReceivedUpdateCount,
                message = "Expected no updates, but got $deltaReceivedUpdateCount updates instead."
            )
        }

        abstract fun end()
    }

    abstract class Partial<ValueT> : UpdateVerifier<ValueT>()

    companion object {
        fun <ValueT> observePassively(
            subjectCell: Cell<ValueT>,
        ): Passive<ValueT> = object : Passive<ValueT>() {
            override fun verifyUpdates(
                doUpdate: EmitterEventStream<Unit>,
                expectedUpdatedValue: ValueT,
            ) {
                doUpdate.emit()

                val passivelySampledValue = subjectCell.sampleExternally()

                assertEquals(
                    expected = expectedUpdatedValue,
                    actual = passivelySampledValue,
                )
            }

            override fun verifyDoesNotUpdate(
                doTrigger: EmitterEventStream<Unit>,
                expectedNonUpdatedValue: ValueT,
            ) {
                doTrigger.emit()

                val passivelySampledValue = subjectCell.sampleExternally()

                assertEquals(
                    expected = expectedNonUpdatedValue,
                    actual = passivelySampledValue,
                )
            }
        }

        fun <ValueT> observeActively(
            subjectCell: Cell<ValueT>,
        ): Active<ValueT> = observeActivelyViaEventStream(
            subjectCell = subjectCell,
            extract = Cell<ValueT>::updatedValues,
        )

        fun <ValueT> observeActivelyViaEventStream(
            subjectCell: Cell<ValueT>,
            extract: (Cell<ValueT>) -> EventStream<ValueT>,
        ): Active<ValueT> {
            val receivedUpdatedValues = mutableListOf<ValueT>()

            val subscription = extract(subjectCell).subscribe { updatedValue ->
                receivedUpdatedValues.add(updatedValue)
            } ?: throw IllegalStateException("Subscription should not be null.")

            return object : Active<ValueT>(
                subjectCell = subjectCell,
                receivedUpdatedValues = receivedUpdatedValues,
            ) {
                override fun end() {
                    subscription.cancel()
                }
            }
        }

        fun <ValueT> observeActivelyViaSwitch(
            subjectCell: Cell<ValueT>,
        ): Active<ValueT> {
            val helperOuterCell = MutableCell(
                initialValue = subjectCell,
            )

            val helperSwitchCell = Cell.switch(helperOuterCell)

            val receivedUpdatedValues = mutableListOf<ValueT>()

            helperSwitchCell.updatedValues.subscribe { updatedValue ->
                receivedUpdatedValues.add(updatedValue)
            }

            return object : Active<ValueT>(
                subjectCell = subjectCell,
                receivedUpdatedValues = receivedUpdatedValues,
            ) {
                override fun end() {
                    helperOuterCell.set(
                        Cell.of(subjectCell.sampleExternally()),
                    )
                }
            }
        }
    }

    abstract fun verifyUpdates(
        doUpdate: EmitterEventStream<Unit>,
        expectedUpdatedValue: ValueT,
    )
}
