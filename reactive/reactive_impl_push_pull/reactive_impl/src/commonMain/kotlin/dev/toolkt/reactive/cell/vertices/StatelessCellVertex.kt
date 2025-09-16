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
        context: Transaction.Context,
    ): ValueT = when {
        isStableValueCached -> @Suppress("UNCHECKED_CAST") (cachedStableValue as ValueT)

        else -> {
            val computeStableValue = computeStableValue(
                context,
            )

            mutableIsStableValueCached = true
            mutableCachedStableValue = computeStableValue

            if (!isProcessed) {
                // If the vertex is processed, it means that the vertex is already enqueued for resetting
                context.enqueueDirtyVertex(
                    dirtyVertex = this,
                )
            }

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
        context: Transaction.Context,
    ): ValueT

    protected abstract fun activate()

    protected abstract fun deactivate()
}
