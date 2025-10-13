package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.map
import dev.toolkt.reactive.cell.test_utils.GivenCellTimeline.GivenNotification
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.map
import dev.toolkt.reactive.event_stream.mapNotNull
import dev.toolkt.reactive.event_stream.mapNotNullAt
import dev.toolkt.reactive.event_stream.single
import dev.toolkt.reactive.event_stream.take

sealed class DynamicCellFactory {
    companion object {
        val values = listOf(
            Basic,
            TransformedBasic,
        )
    }

    data object Basic : DynamicCellFactory() {
        override fun <ValueT> createDynamicExternally(
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
        override fun <ValueT> createDynamicExternally(
            initialValue: ValueT,
            doUpdate: EventStream<ValueT>,
        ): Cell<ValueT> = Basic.createDynamicExternally(
            initialValue = initialValue,
            doUpdate = doUpdate,
        ).map { it }
    }

    fun <ValueT> createFilteredOutExternally(
        initialValue: ValueT,
        doTrigger: EventStream<Unit>,
    ): Cell<ValueT> = createDynamicExternally(
        initialValue = initialValue,
        doUpdate = doTrigger.mapNotNull { null },
    )

    fun <ValueT> createDynamicExternally(
        initialValue: ValueT,
    ): Cell<ValueT> = createDynamicExternally(
        initialValue = initialValue,
        doUpdate = EmitterEventStream(),
    )

    abstract fun <ValueT> createDynamicExternally(
        initialValue: ValueT,
        doUpdate: EventStream<ValueT>,
    ): Cell<ValueT>

    fun <ValueT> createFreezingExternally(
        initialValue: ValueT,
        doUpdateFreezing: EventStream<ValueT>,
    ): Cell<ValueT> {
        val newValues = MomentContext.execute {
            doUpdateFreezing.single()
        }

        return createDynamicExternally(
            initialValue = initialValue,
            doUpdate = newValues,
        )
    }

    fun <ValueT> createFreezingLaterExternally(
        initialValue: ValueT,
        doFreezeLater: EventStream<Unit>,
    ): Cell<ValueT> = createFreezingLaterExternally(
        initialValue = initialValue,
        doUpdate = EmitterEventStream(),
        doFreezeLater = doFreezeLater,
    )

    fun <ValueT> createFreezingLaterExternally(
        initialValue: ValueT,
        doUpdate: EventStream<ValueT>,
        doFreezeLater: EventStream<Unit>,
    ): Cell<ValueT> {
        val doUpdateEffectively = MomentContext.execute {
            EventStream.merge2(
                doUpdate,
                doFreezeLater.map { null },
            ).take(2).mapNotNullAt { it }
        }

        return createDynamicExternally(
            initialValue = initialValue,
            doUpdate = doUpdateEffectively,
        )
    }
}
