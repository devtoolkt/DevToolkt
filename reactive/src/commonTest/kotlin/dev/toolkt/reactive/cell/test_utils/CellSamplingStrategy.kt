package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.updatedValues
import dev.toolkt.reactive.event_stream.subscribe
import kotlin.test.assertEquals

sealed class CellSamplingStrategy {
    interface Asserter<ValueT> {
        fun assertCurrentValueEquals(
            expectedCurrentValue: ValueT,
        )
    }

    data object Passive : CellSamplingStrategy() {
        override fun <ValueT> prepare(
            cell: Cell<ValueT>,
        ) {
        }
    }

    data object Active : CellSamplingStrategy() {
        override fun <ValueT> prepare(
            cell: Cell<ValueT>,
        ) {
            cell.updatedValues.subscribe {}
        }
    }

    abstract fun <ValueT> prepare(
        cell: Cell<ValueT>,
    )

    fun <ValueT> perceive(
        cell: Cell<ValueT>,
    ): Asserter<ValueT> {
        prepare(cell = cell)

        return object : Asserter<ValueT> {
            override fun assertCurrentValueEquals(
                expectedCurrentValue: ValueT,
            ) {
                assertEquals(
                    expected = expectedCurrentValue,
                    actual = cell.sampleExternally(),
                )
            }
        }
    }
}
