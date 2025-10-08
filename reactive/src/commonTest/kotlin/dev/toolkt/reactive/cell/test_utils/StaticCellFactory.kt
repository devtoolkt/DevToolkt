package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EmitterEventStream

sealed class StaticCellFactory {
    companion object {
        val values by lazy {
            listOf(
                Dynamic,
            ) + InertCellFactory.values
        }
    }

    data object Dynamic : StaticCellFactory() {
        override fun <ValueT> createStaticExternally(
            staticValue: ValueT,
        ): Cell<ValueT> = MomentContext.Companion.execute {
            Cell.Companion.define(
                initialValue = staticValue,
                newValues = EmitterEventStream(),
            )
        }
    }

    abstract fun <ValueT> createStaticExternally(
        staticValue: ValueT,
    ): Cell<ValueT>
}
