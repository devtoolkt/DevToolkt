package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction

abstract class StatelessCellVertex<ValueT> : IntermediateCellVertex<ValueT>() {
    data class CachedStableValue<ValueT>(
        val stableValue: ValueT,
    )

    // TODO: Is the cached stable value cleared if the cell wasn't actually pre-processed?
    // Is this tested?
    private var mutableCachedStableValue: CachedStableValue<ValueT>? = null

    private val cachedStableValue: CachedStableValue<ValueT>?
        get() = mutableCachedStableValue

    final override fun pullStableValue(
        processingContext: Transaction.ProcessingContext,
    ): ValueT = when (val foundCachedStableValue = this.cachedStableValue) {
        null -> {
            val computedCachedStableValue = computeStableValue(
                processingContext,
            )

            mutableCachedStableValue = CachedStableValue(
                stableValue = computedCachedStableValue,
            )

            ensureMarkedDirty(
                processingContext = processingContext,
            )

            computedCachedStableValue
        }

        else -> foundCachedStableValue.stableValue
    }

    final override fun onFirstDependentAdded() {
        activate()
    }

    final override fun onLastDependentRemoved() {
        deactivate()
    }

    final override fun update(
        currentNotification: CellVertex.Update<ValueT>,
    ) {
        mutableCachedStableValue = null
    }

    protected abstract fun computeStableValue(
        processingContext: Transaction.ProcessingContext,
    ): ValueT

    protected abstract fun activate()

    protected abstract fun deactivate()
}
