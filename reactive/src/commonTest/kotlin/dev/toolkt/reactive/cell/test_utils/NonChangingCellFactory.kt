package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.map
import dev.toolkt.reactive.event_stream.hold

sealed interface NonChangingCellFactory {
    companion object {
        val values = listOf(
            Const,
            TransformedConst,
            Dynamic,
            TransformedDynamic,
        )
    }

    data object Const : NonChangingCellFactory {
        context(momentContext: MomentContext) override fun <ValueT> create(
            value: ValueT,
        ): Cell<ValueT> = Cell.of(value)
    }

    data object TransformedConst : NonChangingCellFactory {
        context(momentContext: MomentContext) override fun <ValueT> create(
            value: ValueT,
        ): Cell<ValueT> = Cell.of(value).map { it }
    }

    data object Dynamic : NonChangingCellFactory {
        context(momentContext: MomentContext) override fun <ValueT> create(
            value: ValueT,
        ): Cell<ValueT> = NonEmittingEventStreamFactory.Dynamic.create<ValueT>().hold(
            initialValue = value,
        )
    }

    data object TransformedDynamic : NonChangingCellFactory {
        context(momentContext: MomentContext) override fun <ValueT> create(
            value: ValueT,
        ): Cell<ValueT> = Dynamic.create(value).map {
            it
        }
    }

    context(momentContext: MomentContext) fun <ValueT> create(
        value: ValueT,
    ): Cell<ValueT>
}
