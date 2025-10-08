package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.MutableCell
import dev.toolkt.reactive.cell.updatedValues
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.emit
import dev.toolkt.reactive.event_stream.map
import dev.toolkt.reactive.event_stream.mapAt
import dev.toolkt.reactive.event_stream.subscribe
import kotlin.test.assertEquals

sealed class CellVerifier<ValueT> {
    abstract class Total<ValueT>() : CellVerifier<ValueT>() {
        abstract fun verifyDoesNotUpdate(
            doTriggerPotentialUpdate: EmitterEventStream<Unit>,
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

        override fun verifyCurrentValue(
            expectedCurrentValue: ValueT,
        ) {
            verifyCurrentValueActively(
                expectedCurrentValue = expectedCurrentValue,
            )
        }

        final override fun verifyUpdates(
            doTriggerUpdate: EmitterEventStream<Unit>,
            expectedUpdatedValue: ValueT,
        ) {
            verifyUpdatePropagates(
                doTrigger = doTriggerUpdate,
                expectedPropagatedUpdatedValue = expectedUpdatedValue,
            )

            verifyCurrentValueActively(
                expectedCurrentValue = expectedUpdatedValue,
            )
        }

        fun verifyUpdatePropagates(
            doTrigger: EmitterEventStream<Unit>,
            expectedPropagatedUpdatedValue: ValueT,
        ) {
            val previousReceivedUpdateCount = receivedUpdateCount

            doTrigger.emit()

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
                message = "Expected the single update to be $expectedPropagatedUpdatedValue, but got $singleUpdatedValue instead."
            )
        }

        final override fun verifyDoesNotUpdate(
            doTriggerPotentialUpdate: EmitterEventStream<Unit>,
            expectedNonUpdatedValue: ValueT,
        ) {
            verifyUpdateDoesNotPropagate(
                doTrigger = doTriggerPotentialUpdate,
            )

            verifyCurrentValueActively(
                expectedCurrentValue = expectedNonUpdatedValue,
            )
        }

        fun verifyUpdateDoesNotPropagate(
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

        private fun verifyCurrentValueActively(
            expectedCurrentValue: ValueT,
        ) {
            val activelySampledValue = subjectCell.sampleExternally()

            assertEquals(
                expected = expectedCurrentValue,
                actual = activelySampledValue,
            )
        }

        abstract fun deactivate()
    }

    abstract class Partial<ValueT> : CellVerifier<ValueT>()

    companion object {
        fun <ValueT> observePassively(
            subjectCell: Cell<ValueT>,
        ): Passive<ValueT> = object : Passive<ValueT>() {
            override fun verifyUpdates(
                doTriggerUpdate: EmitterEventStream<Unit>,
                expectedUpdatedValue: ValueT,
            ) {
                doTriggerUpdate.emit()

                verifyCurrentValuePassively(
                    expectedCurrentValue = expectedUpdatedValue,
                )
            }

            override fun verifyCurrentValue(
                expectedCurrentValue: ValueT,
            ) {
                verifyCurrentValuePassively(
                    expectedCurrentValue = expectedCurrentValue,
                )
            }

            override fun verifyDoesNotUpdate(
                doTriggerPotentialUpdate: EmitterEventStream<Unit>,
                expectedNonUpdatedValue: ValueT,
            ) {
                doTriggerPotentialUpdate.emit()

                verifyCurrentValuePassively(
                    expectedCurrentValue = expectedNonUpdatedValue,
                )
            }

            private fun verifyCurrentValuePassively(
                expectedCurrentValue: ValueT,
            ) {
                val passivelySampledValue = subjectCell.sampleExternally()

                assertEquals(
                    expected = expectedCurrentValue,
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
                override fun verifyCurrentValue(
                    expectedCurrentValue: ValueT,
                ) {
                    val sampledValue = subjectCell.sampleExternally()

                    assertEquals(
                        expected = expectedCurrentValue,
                        actual = sampledValue,
                    )
                }

                override fun deactivate() {
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
                override fun verifyCurrentValue(
                    expectedCurrentValue: ValueT,
                ) {
                    val sampledValue = helperSwitchCell.sampleExternally()

                    assertEquals(
                        expected = expectedCurrentValue,
                        actual = sampledValue,
                    )
                }

                override fun deactivate() {
                    helperOuterCell.set(
                        Cell.of(subjectCell.sampleExternally()),
                    )
                }
            }
        }

        private sealed class ValueWrapper<out ValueT> {
            data object None : ValueWrapper<Nothing>()

            data class Some<ValueT>(
                val value: ValueT,
            ) : ValueWrapper<ValueT>()
        }

        /**
         * A tricky update verifier that triggers a corner case path, where the subject cell might be activated and pulled
         * at the same time by a dependent `switch` cell.
         */
        fun <ValueT> observeQuick(
            subjectCell: Cell<ValueT>,
        ): Total<ValueT> = object : CellVerifier.Total<ValueT>() {
            private fun verifyQuick(
                doTriggerUpdate: EmitterEventStream<Unit>,
                verifyHelper: (CellVerifier.Active<ValueWrapper<ValueT>>) -> Unit,
            ) {
                val doReset = EmitterEventStream<Unit>()

                val helperOuterCell = MomentContext.execute {
                    val helperInnerCell = Cell.define(
                        initialValue = ValueWrapper.None,
                        newValues = subjectCell.updatedValues.map {
                            ValueWrapper.Some(it)
                        },
                    )

                    Cell.define(
                        initialValue = Cell.of(ValueWrapper.None),
                        newValues = EventStream.merge2(
                            doTriggerUpdate.map { helperInnerCell },
                            doReset.mapAt { Cell.of(ValueWrapper.None) },
                        ),
                    )
                }

                val helperSwitchCell = Cell.switch(helperOuterCell)

                val helperUpdateVerifier = CellVerifier.observeActively(
                    subjectCell = helperSwitchCell,
                )

                verifyHelper(helperUpdateVerifier)

                doReset.emit()
            }

            override fun verifyUpdates(
                doTriggerUpdate: EmitterEventStream<Unit>,
                expectedUpdatedValue: ValueT,
            ) {
                verifyQuick(
                    doTriggerUpdate = doTriggerUpdate,
                ) { helperUpdateVerifier ->
                    helperUpdateVerifier.verifyUpdates(
                        doTriggerUpdate = doTriggerUpdate,
                        expectedUpdatedValue = ValueWrapper.Some(expectedUpdatedValue),
                    )
                }
            }

            override fun verifyDoesNotUpdate(
                doTriggerPotentialUpdate: EmitterEventStream<Unit>,
                expectedNonUpdatedValue: ValueT,
            ) {
                verifyQuick(
                    doTriggerUpdate = doTriggerPotentialUpdate,
                ) { helperUpdateVerifier ->
                    helperUpdateVerifier.verifyUpdates(
                        doTriggerUpdate = doTriggerPotentialUpdate,
                        expectedUpdatedValue = ValueWrapper.None,
                    )
                }
            }

            override fun verifyCurrentValue(
                expectedCurrentValue: ValueT,
            ) {
                val sampledValue = subjectCell.sampleExternally()

                assertEquals(
                    expected = expectedCurrentValue,
                    actual = sampledValue,
                )
            }
        }
    }
//    /*
//          abstract fun verifyDoesNotUpdate(
//            doTriggerPotentialUpdate: EmitterEventStream<Unit>,
//            expectedNonUpdatedValue: ValueT,
//        )
//     */
//
//    fun verifyDoesNotUpdate(
//        doTriggerPotentialUpdate: EmitterEventStream<Unit>,
//        expectedNonUpdatedValue: ValueT,
//    ) {
//        doTriggerPotentialUpdate.emit()
//
//        verifyCurrentValue(
//            expectedCurrentValue = expectedNonUpdatedValue,
//        )
//    }

    abstract fun verifyUpdates(
        doTriggerUpdate: EmitterEventStream<Unit>,
        expectedUpdatedValue: ValueT,
    )

    abstract fun verifyCurrentValue(
        expectedCurrentValue: ValueT,
    )
}
