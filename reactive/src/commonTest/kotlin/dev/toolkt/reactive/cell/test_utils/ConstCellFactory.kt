package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.cell.Cell

sealed interface ConstCellFactory {
    data object Inert : ConstCellFactory {
        override fun <ValueT> create(value: ValueT): Cell<ValueT> = Cell.of(value)
    }

    data object Dynamic : ConstCellFactory {
        override fun <ValueT> create(value: ValueT): Cell<ValueT> = Cell.of(value)
    }

    fun <ValueT> create(
        value: ValueT,
    ): Cell<ValueT>
}
