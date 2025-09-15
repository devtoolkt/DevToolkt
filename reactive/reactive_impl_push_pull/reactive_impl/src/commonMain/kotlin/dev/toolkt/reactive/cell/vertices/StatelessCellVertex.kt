package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction

abstract class StatelessCellVertex<ValueT> : PropagativeCellVertex<ValueT>() {
    data class StableValueCache<ValueT>(
        val stableValue: ValueT,
    )

    // TODO: Is the cached stable value cleared if the cell wasn't actually pre-processed?
    // Is this tested?
    private var cachedStableValue: StableValueCache<ValueT>? = null

    final override fun prepare(
        preProcessingContext: Transaction.PreProcessingContext,
    ): CellVertex.Update<ValueT>? = prepareStateless(
        preProcessingContext = preProcessingContext,
    )

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

    final override fun onFirstDependentAdded(
        expansionContext: Transaction.ExpansionContext,
    ) {
        activateStateless(
            expansionContext = expansionContext,
        )
    }

    final override fun onLastDependentRemoved(
        shrinkageContext: Transaction.ShrinkageContext,
    ) {
        deactivateStateless(
            shrinkageContext = shrinkageContext,
        )
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

    abstract fun prepareStateless(
        preProcessingContext: Transaction.PreProcessingContext,
    ): CellVertex.Update<ValueT>?

    protected abstract fun activateStateless(
        expansionContext: Transaction.ExpansionContext,
    )

    protected abstract fun deactivateStateless(
        shrinkageContext: Transaction.ShrinkageContext,
    )
}
