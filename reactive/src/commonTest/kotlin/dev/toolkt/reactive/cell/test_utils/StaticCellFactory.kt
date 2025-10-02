package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.map
import dev.toolkt.reactive.event_stream.hold

sealed interface StaticCellFactory {
    companion object {
        val values = listOf(
            Const,
            TransformedConst,
            Dynamic,
            TransformedDynamic,
//            FreezingDynamic,
        )
    }

    data object Const : StaticCellFactory {
        override fun <ValueT> createExternally(
            value: ValueT,
        ): Cell<ValueT> = Cell.of(value)
    }

    data object TransformedConst : StaticCellFactory {
        override fun <ValueT> createExternally(
            value: ValueT,
        ): Cell<ValueT> = Cell.of(value).map { it }
    }

    data object Dynamic : StaticCellFactory {
        override fun <ValueT> createExternally(
            value: ValueT,
        ): Cell<ValueT> = MomentContext.execute {
            NonEmittingEventStreamFactory.Dynamic.create<ValueT>().hold(
                initialValue = value,
            )
        }
    }

    data object TransformedDynamic : StaticCellFactory {
        override fun <ValueT> createExternally(
            value: ValueT,
        ): Cell<ValueT> = Dynamic.createExternally(value).map { it }
    }

    data object FreezingDynamic : StaticCellFactory {
        override fun <ValueT> createExternally(value: ValueT): Cell<ValueT> {
            TODO("Not yet implemented")
        }
    }

    fun <ValueT> createExternally(
        value: ValueT,
    ): Cell<ValueT>
}
