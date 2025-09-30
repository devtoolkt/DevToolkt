package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.MutableCell

sealed interface NonChangingCellFactory {
    companion object {
        val values = listOf(Const, Dynamic)
    }

    data object Const : NonChangingCellFactory {
        context(momentContext: MomentContext) override fun <ValueT> create(
            value: ValueT,
        ): Cell<ValueT> = MutableCell(value)
    }

    data object TransformedInert : NonChangingCellFactory {
        context(momentContext: MomentContext) override fun <ValueT> create(
            value: ValueT,
        ): Cell<ValueT> = MutableCell(value)
    }

    data object Dynamic : NonChangingCellFactory {
        context(momentContext: MomentContext) override fun <ValueT> create(
            value: ValueT,
        ): Cell<ValueT> = Cell.of(value)
    }

    context(momentContext: MomentContext) fun <ValueT> create(
        value: ValueT,
    ): Cell<ValueT>
}
