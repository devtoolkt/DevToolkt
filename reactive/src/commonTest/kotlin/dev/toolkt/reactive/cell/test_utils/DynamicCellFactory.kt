package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.map
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.hold
import dev.toolkt.reactive.event_stream.mapNotNull
import dev.toolkt.reactive.event_stream.single

sealed class DynamicCellFactory {
    companion object {
        val values = listOf(
            Basic,
            TransformedBasic,
            FreezingSimultaneously,
//            FreezingLater,
        )
    }

    data object Basic : DynamicCellFactory() {
        override fun <ValueT> createExternally(
            initialValue: ValueT,
            doUpdate: EventStream<ValueT>,
        ): Cell<ValueT> = MomentContext.execute {
            Cell.define(
                initialValue = initialValue,
                newValues = doUpdate,
            )
        }
    }

    data object TransformedBasic : DynamicCellFactory() {
        override fun <ValueT> createExternally(
            initialValue: ValueT,
            doUpdate: EventStream<ValueT>,
        ): Cell<ValueT> = Basic.createExternally(
            initialValue = initialValue,
            doUpdate = doUpdate,
        ).map { it }
    }

    data object FreezingSimultaneously : DynamicCellFactory() {
        override fun <ValueT> createExternally(
            initialValue: ValueT,
            doUpdate: EventStream<ValueT>,
        ): Cell<ValueT> = MomentContext.execute {
            doUpdate.single().hold(
                initialValue = initialValue,
            )
        }
    }

    data object FreezingLater : DynamicCellFactory() {
        override fun <ValueT> createExternally(
            initialValue: ValueT,
            doUpdate: EventStream<ValueT>,
        ): Cell<ValueT> {
            TODO("Not yet implemented")
        }
    }

    fun <ValueT> createFilteredOutExternally(
        initialValue: ValueT,
        doTrigger: EventStream<Unit>,
    ): Cell<ValueT> = createExternally(
        initialValue = initialValue,
        doUpdate = doTrigger.mapNotNull { null },
    )

    abstract fun <ValueT> createExternally(
        initialValue: ValueT,
        doUpdate: EventStream<ValueT>,
    ): Cell<ValueT>
}
