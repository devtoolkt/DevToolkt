package dev.toolkt.reactive

import dev.toolkt.reactive.cell.vertices.CellVertex
import dev.toolkt.reactive.cell.vertices.DependencyCellVertex

abstract class IntermediateDynamicCellVertex<ValueT> : BaseDependencyVertex(), DependencyCellVertex<ValueT>,
    DependentVertex, ResettableVertex {
    private var mutableIsProcessed = false

    private val isProcessed: Boolean
        get() = mutableIsProcessed

    private var mutableCachedUpdatedValue: CellVertex.UpdatedValue<ValueT>? = null

    private val cachedUpdatedValue: CellVertex.UpdatedValue<ValueT>?
        get() = mutableCachedUpdatedValue

    final override fun visit(
        processingContext: Transaction.ProcessingContext,
    ) {
        // Thought: Possibly use "immediate visitation mode"
        ensureProcessed(
            processingContext = processingContext,
        )
    }

    final override fun pullUpdatedValue(
        processingContext: Transaction.ProcessingContext,
    ): CellVertex.UpdatedValue<ValueT>? = ensureProcessed(
        processingContext = processingContext,
    )

    protected fun ensureProcessed(
        processingContext: Transaction.ProcessingContext,
    ): CellVertex.UpdatedValue<ValueT>? {
        if (isProcessed) {
            return cachedUpdatedValue
        }

        val computedUpdatedValue = process(
            processingContext = processingContext,
        )

        mutableIsProcessed = true
        mutableCachedUpdatedValue = computedUpdatedValue

        if (!isStableValueCached) {
            processingContext.enqueueDirtyVertex(
                dirtyVertex = this,
            )
        }

        if (computedUpdatedValue != null) {
            enqueueDependentsForVisiting(
                processingContext = processingContext,
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
        processingContext: Transaction.ProcessingContext,
    ): CellVertex.UpdatedValue<ValueT>?

    protected abstract fun persist(
        newValue: ValueT,
    )

    protected abstract val isStableValueCached: Boolean

    protected abstract fun clearStableValueCache()
}
