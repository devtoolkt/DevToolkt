package dev.toolkt.reactive.event_stream.test_utils

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.MutableCell
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.NeverEventStream
import dev.toolkt.reactive.event_stream.emit
import dev.toolkt.reactive.event_stream.subscribe
import kotlin.test.assertEquals

abstract class EventStreamVerifier<EventT> {
    companion object {
        fun <EventT> observeDirectly(
            subjectEventStream: EventStream<EventT>,
        ): EventStreamVerifier<EventT> {
            val receivedOccurredEvents = mutableListOf<EventT>()

            val subscription = subjectEventStream.subscribe { occurredEvent ->
                receivedOccurredEvents.add(occurredEvent)
            } ?: throw IllegalStateException("Subscription should not be null.")

            return object : EventStreamVerifier<EventT>() {
                private val receivedOccurrenceCount: Int
                    get() = receivedOccurredEvents.size

                override fun pause() {
                    subscription.cancel()
                }

                override fun verifyOccurrencePropagates(
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

                override fun verifyOccurrenceDoesNotPropagate(
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
        ): EventStreamVerifier<EventT> {
            val helperOuterCell = MutableCell(
                initialValue = subjectEventStream,
            )

            val helperDivertEventStream = Cell.divert(helperOuterCell)

            val helperOccurrenceVerifier = observeDirectly(
                subjectEventStream = helperDivertEventStream,
            )

            return object : EventStreamVerifier<EventT>() {
                override fun pause() {
                    helperOuterCell.set(NeverEventStream)
                }

                override fun verifyOccurrencePropagates(
                    doTrigger: EmitterEventStream<Unit>,
                    expectedPropagatedEvent: EventT,
                ) {
                    helperOccurrenceVerifier.verifyOccurrencePropagates(
                        doTrigger = doTrigger,
                        expectedPropagatedEvent = expectedPropagatedEvent,
                    )
                }

                override fun verifyOccurrenceDoesNotPropagate(
                    doTrigger: EmitterEventStream<Unit>,
                ) {
                    helperOccurrenceVerifier.verifyOccurrenceDoesNotPropagate(
                        doTrigger = doTrigger,
                    )
                }
            }
        }
    }

    abstract fun verifyOccurrencePropagates(
        doTrigger: EmitterEventStream<Unit>,
        expectedPropagatedEvent: EventT,
    )

    abstract fun verifyOccurrenceDoesNotPropagate(
        doTrigger: EmitterEventStream<Unit>,
    )

    abstract fun pause()
}
