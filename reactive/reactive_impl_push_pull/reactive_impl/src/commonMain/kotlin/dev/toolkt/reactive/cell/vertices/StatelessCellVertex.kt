package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction

abstract class StatelessCellVertex<ValueT> : BaseCellVertex<ValueT>() {
    private var cachedVolatileUpdate: Update<ValueT>? = null

    final override fun fetchVolatileUpdate(
        processingContext: Transaction.ProcessingContext,
    ): Update<ValueT>? {
        ensureProcessed(
            processingContext = processingContext,
        )

        return cachedVolatileUpdate
    }

    final override val storedVolatileUpdate: Update<ValueT>?
        get() = cachedVolatileUpdate

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

    protected fun cacheVolatileUpdate(
        @Suppress("unused") processingContext: Transaction.ProcessingContext,
        update: Update<ValueT>,
    ) {
        if (cachedVolatileUpdate != null) {
            throw IllegalStateException("There is already a pending update $cachedVolatileUpdate")
        }

        cachedVolatileUpdate = update
    }
}
