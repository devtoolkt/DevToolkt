package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction

abstract class StatelessCellVertex<ValueT> : PropagativeCellVertex<ValueT>() {
    data class StableValueCache<ValueT>(
        val stableValue: ValueT,
    )

    private var cachedStableValue: StableValueCache<ValueT>? = null

    final override fun pullStableValue(
        preProcessingContext: Transaction.PreProcessingContext,
    ): ValueT = when (val foundCachedStableValue = this.cachedStableValue) {
        null -> {
            val computedCachedStableValue = computeStableValue(
                preProcessingContext,
            )

            this.cachedStableValue = StableValueCache(
                stableValue = computedCachedStableValue,
            )

            computedCachedStableValue
        }

        else -> foundCachedStableValue.stableValue
    }

    final override fun stabilize(
        postProcessingContext: Transaction.PostProcessingContext,
        message: CellVertex.Update<ValueT>?,
    ) {
        cachedStableValue = null
    }

    abstract fun computeStableValue(
        preProcessingContext: Transaction.PreProcessingContext,
    ): ValueT
}
