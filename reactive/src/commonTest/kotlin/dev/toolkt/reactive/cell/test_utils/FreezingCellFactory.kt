package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.map
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.filter
import dev.toolkt.reactive.event_stream.hold
import dev.toolkt.reactive.event_stream.mapNotNull
import dev.toolkt.reactive.event_stream.single

// TODO: Nuke in favor of other factories?
sealed interface FreezingCellFactory {
    companion object {
        val values = listOf(
            Dynamic,
            TransformedDynamic,
        )
    }

    data object Dynamic : FreezingCellFactory {
        context(momentContext: MomentContext) override fun <ValueT> create(
            value: ValueT,
            doFreeze: EventStream<Unit>,
        ): Cell<ValueT> = doFreeze.single().mapNotNull { null }.hold(
            initialValue = value,
        )
    }

    data object TransformedDynamic : FreezingCellFactory {
        context(momentContext: MomentContext) override fun <ValueT> create(
            value: ValueT,
            doFreeze: EventStream<Unit>,
        ): Cell<ValueT> = Dynamic.create(
            value = value,
            doFreeze = doFreeze,
        ).map { it }
    }

    context(momentContext: MomentContext) fun <ValueT> create(
        value: ValueT,
        doFreeze: EventStream<Unit>,
    ): Cell<ValueT>
}
