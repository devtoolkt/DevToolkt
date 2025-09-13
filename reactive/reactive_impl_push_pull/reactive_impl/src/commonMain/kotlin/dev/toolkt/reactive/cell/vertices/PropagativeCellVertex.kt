package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.PropagativeVertex
import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.CellVertex.Update

abstract class PropagativeCellVertex<ValueT>() : PropagativeVertex(), CellVertex<ValueT> {
    final override fun stabilizeState(
        stabilizationContext: Transaction.StabilizationContext,
    ) {
        storedVolatileUpdate?.let { update ->
            persistNewValue(
                stabilizationContext = stabilizationContext,
                newValue = update.newValue,
            )
        }

        clear(
            stabilizationContext = stabilizationContext,
        )
    }

    /**
     * Store the new value as stable state (if this vertex maintains stable state)
     */
    abstract fun persistNewValue(
        stabilizationContext: Transaction.StabilizationContext,
        newValue: ValueT,
    )

    /**
     * Clear the stored specialized vertex-specific volatile state
     */
    abstract fun clear(
        stabilizationContext: Transaction.StabilizationContext,
    )

    /**
     * Returns a currently stored volatile update of this cell. This doesn't trigger processing, so it should be used
     * with care.
     */
    protected abstract val storedVolatileUpdate: Update<ValueT>?
}
