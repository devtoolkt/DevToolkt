package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.BaseDependencyVertex
import dev.toolkt.reactive.ResettableVertex
import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.CellVertex.UpdatedValue

class MutableCellVertex<ValueT>(
    initialValue: ValueT,
) : BaseDependencyVertex(), DependencyCellVertex<ValueT>, ResettableVertex {
    private var mutableStableValue: ValueT = initialValue

    private val stableValue: ValueT
        get() = mutableStableValue

    private var setUpdatedValue: UpdatedValue<ValueT>? = null

    fun set(
        processingContext: Transaction.ProcessingContext,
        newValue: ValueT,
    ) {
        setUpdatedValue = UpdatedValue(
            value = newValue,
        )

        processingContext.enqueueDirtyVertex(
            dirtyVertex = this,
        )

        enqueueDependentsForVisiting(
            processingContext = processingContext,
        )
    }

    override fun pullStableValue(
        processingContext: Transaction.ProcessingContext,
    ): ValueT = stableValue

    override fun pullUpdatedValue(
        processingContext: Transaction.ProcessingContext,
    ): UpdatedValue<ValueT>? = setUpdatedValue

    override fun onFirstDependentAdded() {
    }

    override fun onLastDependentRemoved() {
    }

    override fun reset() {
        setUpdatedValue = null
    }
}
