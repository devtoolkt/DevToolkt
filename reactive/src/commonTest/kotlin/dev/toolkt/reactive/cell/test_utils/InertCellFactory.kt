package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.map
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.emit
import dev.toolkt.reactive.event_stream.mapNotNull
import dev.toolkt.reactive.event_stream.single

sealed class InertCellFactory : StillCellFactory() {
    companion object {
        val values by lazy {
            listOf(
                Const,
                TransformedConst,
                Frozen,
                TransformedFrozen,
            )
        }
    }

    /**
     * A factory that creates a constant cell that is guaranteed to never change.
     */
    data object Const : InertCellFactory() {
        override fun <ValueT> createInertExternally(
            inertValue: ValueT,
        ): Cell<ValueT> = Cell.of(inertValue)
    }

    /**
     * A factory that creates a transformed constant cell that is guaranteed to never change.
     */
    data object TransformedConst : InertCellFactory() {
        override fun <ValueT> createInertExternally(
            inertValue: ValueT,
        ): Cell<ValueT> = Cell.of(inertValue).map { it }
    }

    /**
     * A factory that creates a cell freezing right after its construction.
     */
    data object Frozen : InertCellFactory() {
        override fun <ValueT> createInertExternally(
            inertValue: ValueT,
        ): Cell<ValueT> {
            val doFreeze = EmitterEventStream<Unit>()

            return MomentContext.execute {
                Cell.define(
                    initialValue = inertValue,
                    newValues = doFreeze.single().mapNotNull { null },
                )
            }.also {
                doFreeze.emit()
            }
        }
    }

    data object TransformedFrozen : InertCellFactory() {
        override fun <ValueT> createInertExternally(
            inertValue: ValueT,
        ): Cell<ValueT> = Frozen.createInertExternally(
            inertValue = inertValue,
        ).map { it }
    }

    final override fun <ValueT> createStillExternally(
        stillValue: ValueT,
    ): Cell<ValueT> = createInertExternally(
        inertValue = stillValue,
    )

    abstract fun <ValueT> createInertExternally(
        inertValue: ValueT,
    ): Cell<ValueT>
}
