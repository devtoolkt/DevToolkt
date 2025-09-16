package dev.toolkt.reactive

import dev.toolkt.reactive.cell.vertices.CellVertex
import dev.toolkt.reactive.cell.vertices.DependencyCellVertex

abstract class IntermediateDynamicCellVertex<ValueT> : BaseDependencyVertex(), DependencyCellVertex<ValueT>,
    DependentVertex, ResettableVertex {
    private var mutableIsProcessed = false

    protected val isProcessed: Boolean
        get() = mutableIsProcessed

    private var mutableCachedUpdatedValue: CellVertex.UpdatedValue<ValueT>? = null

    private val cachedUpdatedValue: CellVertex.UpdatedValue<ValueT>?
        get() = mutableCachedUpdatedValue

    final override fun visit(
        context: Transaction.Context,
    ) {
        // Thought: Possibly use "immediate visitation mode"
        ensureProcessed(
            context = context,
        )
    }

    final override fun pullUpdatedValue(
        context: Transaction.Context,
    ): CellVertex.UpdatedValue<ValueT>? = ensureProcessed(
        context = context,
    )

    protected fun ensureProcessed(
        context: Transaction.Context,
    ): CellVertex.UpdatedValue<ValueT>? {
        if (isProcessed) {
            return cachedUpdatedValue
        }

        val computedUpdatedValue = process(
            context = context,
        )

        mutableIsProcessed = true
        mutableCachedUpdatedValue = computedUpdatedValue

        if (!isStableValueCached) {
            // If the stable value is cached, it means that the vertex is already enqueued for resetting
            context.enqueueDirtyVertex(
                dirtyVertex = this,
            )
        }

        if (computedUpdatedValue != null) {
            enqueueDependentsForVisiting(
                context = context,
            )
        }

        return computedUpdatedValue
    }

    final override fun reset() {
        val currentUpdate = this.cachedUpdatedValue

        clearUpdatedValueCache()
        clearStableValueCache()

        if (currentUpdate != null) {
            persist(
                newValue = currentUpdate.value,
            )
        }
    }

    protected fun clearUpdatedValueCache() {
        mutableIsProcessed = false
        mutableCachedUpdatedValue = null
    }

    protected abstract fun process(
        context: Transaction.Context,
    ): CellVertex.UpdatedValue<ValueT>?

    protected abstract fun persist(
        newValue: ValueT,
    )

    protected abstract val isStableValueCached: Boolean

    protected abstract fun clearStableValueCache()
}
