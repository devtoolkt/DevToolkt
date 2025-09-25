package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.newValues
import dev.toolkt.reactive.cell.sample
import dev.toolkt.reactive.cell.updatedValues
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.map

sealed class CellObservationChannel {
    data object UpdatedValues : CellObservationChannel() {
        context(momentContext: MomentContext) override fun <ValueT> extract(
            doTrigger: EventStream<*>,
            cell: Cell<ValueT>,
        ): EventStream<ValueT> = cell.updatedValues
    }

    data object NewValues : CellObservationChannel() {
        context(momentContext: MomentContext) override fun <ValueT> extract(
            doTrigger: EventStream<*>,
            cell: Cell<ValueT>,
        ): EventStream<ValueT> = cell.newValues
    }

    data object Switch : CellObservationChannel() {
        context(momentContext: MomentContext) override fun <ValueT> extract(
            doTrigger: EventStream<*>,
            cell: Cell<ValueT>,
        ): EventStream<ValueT> {
            val placeholderCell = Cell.of(cell.sample())

            val outerCell = Cell.define(
                initialValue = placeholderCell,
                newValues = doTrigger.map { cell },
            )

            val switchCell = Cell.switch(outerCell)

            return switchCell.updatedValues
        }
    }

    companion object {
        val values = listOf(
            UpdatedValues,
            NewValues,
            Switch,
        )
    }

    context(momentContext: MomentContext) abstract fun <ValueT> extract(
        doTrigger: EventStream<*>,
        cell: Cell<ValueT>,
    ): EventStream<ValueT>
}
