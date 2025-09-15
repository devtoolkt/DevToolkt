package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction

abstract class StatelessCellVertex<ValueT> : PropagativeCellVertex<ValueT>() {
    data class StableValueCache<ValueT>(
        val stableValue: ValueT,
    )

    private var cachedStableValue: StableValueCache<ValueT>? = null

    final override fun pullStableValue(
        processingContext: Transaction.ProcessingContext,
    ): ValueT = when (val foundCachedStableValue = this.cachedStableValue) {
        null -> {
            val computedCachedStableValue = computeStableValue(
                processingContext,
            )

            this.cachedStableValue = StableValueCache(
                stableValue = computedCachedStableValue,
            )

            computedCachedStableValue
        }

        else -> foundCachedStableValue.stableValue
    }

    final override fun stabilizeState(
        stabilizationContext: Transaction.StabilizationContext,
        message: CellVertex.Update<ValueT>?,
    ) {
        cachedStableValue = null
    }

    abstract fun computeStableValue(
        processingContext: Transaction.ProcessingContext,
    ): ValueT
}
