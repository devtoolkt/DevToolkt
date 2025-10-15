package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EmitterEventStream

sealed class StillCellFactory {
    companion object {
        val values by lazy {
            listOf(
                Dynamic,
            ) + InertCellFactory.values
        }
    }

    data object Dynamic : StillCellFactory() {
        override fun <ValueT> createStillExternally(
            stillValue: ValueT,
        ): Cell<ValueT> = MomentContext.execute {
            Cell.define(
                initialValue = stillValue,
                newValues = EmitterEventStream(),
            )
        }
    }

    abstract fun <ValueT> createStillExternally(
        stillValue: ValueT,
    ): Cell<ValueT>
}
