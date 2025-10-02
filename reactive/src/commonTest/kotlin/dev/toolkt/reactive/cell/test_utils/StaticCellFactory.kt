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
            PotentiallyDynamic,
            TransformedPotentiallyDynamic,
//            Freezing,
        )
    }

    /**
     * A factory that creates a constant cell that is guaranteed to never change.
     */
    data object Const : StaticCellFactory {
        override fun <ValueT> createExternally(
            value: ValueT,
        ): Cell<ValueT> = Cell.of(value)
    }

    /**
     * A factory that creates a transformed constant cell that is guaranteed to never change.
     */
    data object TransformedConst : StaticCellFactory {
        override fun <ValueT> createExternally(
            value: ValueT,
        ): Cell<ValueT> = Cell.of(value).map { it }
    }

    /**
     * A factory that creates a cell that has a potential to change, but doesn't utilize that potential.
     */
    data object PotentiallyDynamic : StaticCellFactory {
        override fun <ValueT> createExternally(
            value: ValueT,
        ): Cell<ValueT> = MomentContext.execute {
            QuietEventStreamFactory.PotentiallyVocal.createExternally<ValueT>().hold(
                initialValue = value,
            )
        }
    }

    /**
     * A factory that creates a transformed cell that has a potential to change, but doesn't utilize that potential.
     */
    data object TransformedPotentiallyDynamic : StaticCellFactory {
        override fun <ValueT> createExternally(
            value: ValueT,
        ): Cell<ValueT> = PotentiallyDynamic.createExternally(value).map { it }
    }

    /**
     * A factory that creates a cell freezing right after its construction.
     */
    data object Freezing : StaticCellFactory {
        override fun <ValueT> createExternally(value: ValueT): Cell<ValueT> {
            TODO("Not yet implemented")
        }
    }

    fun <ValueT> createExternally(
        value: ValueT,
    ): Cell<ValueT>
}
