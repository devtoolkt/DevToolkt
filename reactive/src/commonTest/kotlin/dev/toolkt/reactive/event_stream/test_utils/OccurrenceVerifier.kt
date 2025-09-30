package dev.toolkt.reactive.event_stream.test_utils

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.MutableCell
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.NeverEventStream
import dev.toolkt.reactive.event_stream.emit
import dev.toolkt.reactive.event_stream.subscribe
import kotlin.test.assertEquals

abstract class OccurrenceVerifier<EventT> {
    companion object {
        fun <EventT> observeDirectly(
            subjectEventStream: EventStream<EventT>,
        ): OccurrenceVerifier<EventT> {
            val receivedOccurredEvents = mutableListOf<EventT>()

            val subscription = subjectEventStream.subscribe { occurredEvent ->
                receivedOccurredEvents.add(occurredEvent)
            } ?: throw IllegalStateException("Subscription should not be null.")

            return object : OccurrenceVerifier<EventT>() {
                private val receivedOccurrenceCount: Int
                    get() = receivedOccurredEvents.size

                override fun end() {
                    subscription.cancel()
                }

                override fun verifyOccurrencePropagated(
                    doTrigger: EmitterEventStream<Unit>,
                    expectedPropagatedEvent: EventT,
                ) {
                    val previousReceivedOccurrenceCount = receivedOccurrenceCount

                    doTrigger.emit()

                    val deltaReceivedOccurrenceCount = receivedOccurrenceCount - previousReceivedOccurrenceCount

                    assertEquals(
                        expected = 1,
                        actual = deltaReceivedOccurrenceCount,
                        message = "Expected a single event occurrence, but got $deltaReceivedOccurrenceCount occurrences instead."
                    )

                    val singleOccurredEvent = receivedOccurredEvents.last()

                    assertEquals(
                        expected = expectedPropagatedEvent,
                        actual = singleOccurredEvent,
                        message = "Expected the single update to be $singleOccurredEvent, but got $singleOccurredEvent instead."
                    )
                }

                override fun verifyOccurrenceDidNotPropagate(
                    doTrigger: EmitterEventStream<Unit>,
                ) {
                    val previousReceivedOccurrenceCount = receivedOccurrenceCount

                    doTrigger.emit()

                    val deltaReceivedOccurrenceCount = receivedOccurrenceCount - previousReceivedOccurrenceCount

                    assertEquals(
                        expected = 0,
                        actual = deltaReceivedOccurrenceCount,
                        message = "Expected no event occurrences, but got $deltaReceivedOccurrenceCount occurrences instead."
                    )
                }
            }
        }

        fun <EventT> observeViaDivert(
            subjectEventStream: EventStream<EventT>,
        ): OccurrenceVerifier<EventT> {
            val helperOuterCell = MutableCell(
                initialValue = subjectEventStream,
            )

            val helperDivertEventStream = Cell.divert(helperOuterCell)

            val helperOccurrenceVerifier = observeDirectly(
                subjectEventStream = helperDivertEventStream,
            )

            return object : OccurrenceVerifier<EventT>() {
                override fun end() {
                    helperOuterCell.set(NeverEventStream)
                }

                override fun verifyOccurrencePropagated(
                    doTrigger: EmitterEventStream<Unit>,
                    expectedPropagatedEvent: EventT,
                ) {
                    helperOccurrenceVerifier.verifyOccurrencePropagated(
                        doTrigger = doTrigger,
                        expectedPropagatedEvent = expectedPropagatedEvent,
                    )
                }

                override fun verifyOccurrenceDidNotPropagate(
                    doTrigger: EmitterEventStream<Unit>,
                ) {
                    helperOccurrenceVerifier.verifyOccurrenceDidNotPropagate(
                        doTrigger = doTrigger,
                    )
                }
            }
        }
    }

    abstract fun verifyOccurrencePropagated(
        doTrigger: EmitterEventStream<Unit>,
        expectedPropagatedEvent: EventT,
    )

    abstract fun verifyOccurrenceDidNotPropagate(
        doTrigger: EmitterEventStream<Unit>,
    )

    abstract fun end()
}
