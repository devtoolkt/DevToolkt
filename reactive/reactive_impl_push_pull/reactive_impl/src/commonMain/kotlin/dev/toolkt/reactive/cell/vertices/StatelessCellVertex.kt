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
        processingContext: Transaction.ProcessingContext,
    ): CellVertex.Update<ValueT>? = prepareStateless(
        processingContext = processingContext,
    )

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

    final override fun onFirstDependentAdded(
        expansionContext: Transaction.ExpansionContext,
    ) {
        activate(
            expansionContext = expansionContext,
        )
    }

    final override fun onLastDependentRemoved(
        shrinkageContext: Transaction.ShrinkageContext,
    ) {
        deactivate(
            shrinkageContext = shrinkageContext,
        )
    }

    final override fun postProcessLatePv(
        latePostProcessingContext: Transaction.LatePostProcessingContext,
        message: CellVertex.Update<ValueT>?,
    ) {
        cachedStableValue = null
    }

    abstract fun computeStableValue(
        processingContext: Transaction.ProcessingContext,
    ): ValueT

    abstract fun prepareStateless(
        processingContext: Transaction.ProcessingContext,
    ): CellVertex.Update<ValueT>?

    protected abstract fun activate(
        expansionContext: Transaction.ExpansionContext,
    )

    protected abstract fun deactivate(
        shrinkageContext: Transaction.ShrinkageContext,
    )
}
