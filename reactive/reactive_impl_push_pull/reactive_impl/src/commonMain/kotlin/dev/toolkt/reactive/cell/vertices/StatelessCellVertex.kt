package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.CellVertex.Update

abstract class StatelessCellVertex<ValueT> : PropagativeCellVertex<ValueT>() {
    private var cachedVolatileUpdate: Update<ValueT>? = null

    final override fun pullUpdate(
        processingContext: Transaction.ProcessingContext,
    ): Update<ValueT>? {
        process(
            processingContext = processingContext,
        )

        return cachedVolatileUpdate
    }

    final override val storedVolatileUpdate: Update<ValueT>?
        get() = cachedVolatileUpdate

    final override fun prepare(
        processingContext: Transaction.ProcessingContext,
    ): Boolean {
        val computedUpdate = computeUpdate(processingContext)

        cachedVolatileUpdate = computedUpdate

        return computedUpdate != null
    }

    final override fun persistNewValue(
        stabilizationContext: Transaction.StabilizationContext,
        newValue: ValueT,
    ) {
        // Stateless cell vertices do not maintain stable state
    }

    final override fun clear(
        stabilizationContext: Transaction.StabilizationContext,
    ) {
        cachedVolatileUpdate = null
    }

    protected abstract fun computeUpdate(
        processingContext: Transaction.ProcessingContext,
    ): Update<ValueT>?
}
