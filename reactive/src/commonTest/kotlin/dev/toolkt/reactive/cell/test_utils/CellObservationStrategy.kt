package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.subscribe
import kotlin.test.assertEquals

sealed class CellObservationStrategy {
    interface Asserter<ValueT> {
        fun assertUpdatedValueEquals(
            expectedUpdatedValue: ValueT,
        )
    }

    data object Passive : CellObservationStrategy() {
        override fun <ValueT> observeForTesting(
            trigger: EventStream<*>,
            cell: Cell<ValueT>,
        ): Asserter<ValueT> = object : Asserter<ValueT> {
            override fun assertUpdatedValueEquals(
                expectedUpdatedValue: ValueT,
            ) {
                assertEquals(
                    expected = expectedUpdatedValue,
                    actual = cell.sampleExternally(),
                )
            }
        }
    }

    data class Active(
        val observationChannel: CellObservationChannel,
    ) : CellObservationStrategy() {
        override fun <ValueT> observeForTesting(
            trigger: EventStream<*>,
            cell: Cell<ValueT>,
        ): Asserter<ValueT> {
            val receivedUpdatedValues = mutableListOf<ValueT>()

            val values = MomentContext.execute {
                observationChannel.extract(
                    trigger = trigger,
                    cell = cell,
                )
            }

            values.subscribe { updatedValue ->
                receivedUpdatedValues.add(updatedValue)
            }

            return object : Asserter<ValueT> {
                override fun assertUpdatedValueEquals(
                    expectedUpdatedValue: ValueT,
                ) {
                    assertEquals(
                        expected = listOf(expectedUpdatedValue),
                        actual = receivedUpdatedValues,
                    )

                    receivedUpdatedValues.clear()

                    assertEquals(
                        expected = expectedUpdatedValue,
                        actual = cell.sampleExternally(),
                    )
                }
            }
        }
    }

    companion object {
        val ActiveUpdatedValues = Active(
            observationChannel = CellObservationChannel.UpdatedValues,
        )

        val ActiveNewValues = Active(
            observationChannel = CellObservationChannel.NewValues,
        )

        val ActiveSwitch = Active(
            observationChannel = CellObservationChannel.Switch,
        )
    }

    abstract fun <ValueT> observeForTesting(
        trigger: EventStream<*>,
        cell: Cell<ValueT>,
    ): Asserter<ValueT>
}
