package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.IntermediateDynamicCellVertex
import dev.toolkt.reactive.Transaction

abstract class StatelessCellVertex<ValueT> : IntermediateDynamicCellVertex<ValueT>() {
    private var mutableIsStableValueCached = false

    override val isStableValueCached: Boolean
        get() = mutableIsStableValueCached

    private var mutableCachedStableValue: ValueT? = null

    private val cachedStableValue: ValueT?
        get() = mutableCachedStableValue

    final override fun pullStableValue(
        processingContext: Transaction.ProcessingContext,
    ): ValueT = when {
        isStableValueCached -> @Suppress("UNCHECKED_CAST") (cachedStableValue as ValueT)

        else -> {
            val computeStableValue = computeStableValue(
                processingContext,
            )

            mutableIsStableValueCached = true
            mutableCachedStableValue = computeStableValue

            processingContext.enqueueDirtyVertex(
                dirtyVertex = this,
            )

            computeStableValue
        }
    }

    final override fun onFirstDependentAdded() {
        activate()
    }

    final override fun onLastDependentRemoved() {
        deactivate()
    }

    final override fun persist(
        newValue: ValueT,
    ) {
        // Stateless cells currently don't maintain persistent stable value
    }

    final override fun clearStableValueCache() {
        mutableIsStableValueCached = false
        mutableCachedStableValue = null
    }

    protected abstract fun computeStableValue(
        processingContext: Transaction.ProcessingContext,
    ): ValueT

    protected abstract fun activate()

    protected abstract fun deactivate()
}
