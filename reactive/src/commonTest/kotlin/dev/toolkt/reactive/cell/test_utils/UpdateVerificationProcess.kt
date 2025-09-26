package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.updatedValues
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.subscribe
import kotlin.test.assertEquals

sealed class UpdateVerificationProcess<ValueT> {
    class Total<ValueT>(
        private val receivedUpdatedValues: List<ValueT>,
    ) : UpdateVerificationProcess<ValueT>() {
        companion object {
            fun <ValueT> observe(
                subjectCell: Cell<ValueT>,
            ): Total<ValueT> = observeVia(
                subjectCell = subjectCell,
                extract = Cell<ValueT>::updatedValues,
            )

            fun <ValueT> observeVia(
                subjectCell: Cell<ValueT>,
                extract: (Cell<ValueT>) -> EventStream<ValueT>,
            ): Total<ValueT> {
                val receivedUpdatedValues = mutableListOf<ValueT>()

                extract(subjectCell).subscribe { updatedValue ->
                    receivedUpdatedValues.add(updatedValue)
                }

                return Total(
                    receivedUpdatedValues = receivedUpdatedValues,
                )
            }
        }

        private val receivedUpdateCount: Int
            get() = receivedUpdatedValues.size

        override fun prepareVerifier(
            onTriggered: EventStream<*>,
        ): TotalUpdateVerifier<ValueT> = prepareVerifier()

        fun prepareVerifier(): TotalUpdateVerifier<ValueT> {
            val previousReceivedUpdateCount = receivedUpdateCount

            return object : TotalUpdateVerifier<ValueT>() {
                override fun verifyDidNotUpdate() {
                    val deltaReceivedUpdateCount = receivedUpdateCount - previousReceivedUpdateCount

                    assertEquals(
                        expected = 0,
                        actual = deltaReceivedUpdateCount,
                        message = "Expected no updates, but got $deltaReceivedUpdateCount updates instead."
                    )
                }

                override fun verifyUpdated(): ValueT {
                    val deltaReceivedUpdateCount = receivedUpdateCount - previousReceivedUpdateCount

                    assertEquals(
                        expected = 1,
                        actual = deltaReceivedUpdateCount,
                        message = "Expected exactly one update, but got $deltaReceivedUpdateCount updates instead."
                    )

                    return receivedUpdatedValues.last()
                }
            }
        }
    }

    abstract class Partial<ValueT> : UpdateVerificationProcess<ValueT>()

    abstract fun prepareVerifier(
        onTriggered: EventStream<*>,
    ): UpdateVerifier<ValueT>
}
