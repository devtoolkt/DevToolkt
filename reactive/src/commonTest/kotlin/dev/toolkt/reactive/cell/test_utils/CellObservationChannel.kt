package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.newValues
import dev.toolkt.reactive.cell.updatedValues
import dev.toolkt.reactive.event_stream.EventStream

sealed class CellObservationChannel {
    data object UpdatedValues : CellObservationChannel() {
        override fun <ValueT> extract(cell: Cell<ValueT>): EventStream<ValueT> = cell.updatedValues
    }

    data object NewValues : CellObservationChannel() {
        override fun <ValueT> extract(cell: Cell<ValueT>): EventStream<ValueT> = cell.newValues
    }


    data object Switch : CellObservationChannel() {
        override fun <ValueT> extract(cell: Cell<ValueT>): EventStream<ValueT> = cell.updatedValues
    }


    abstract fun <ValueT> extract(
        cell: Cell<ValueT>,
    ): EventStream<ValueT>
}