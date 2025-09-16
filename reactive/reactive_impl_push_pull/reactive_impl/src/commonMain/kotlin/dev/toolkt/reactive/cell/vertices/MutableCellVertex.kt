package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.BaseDependencyVertex
import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.CellVertex.Update

class MutableCellVertex<ValueT>(
    initialValue: ValueT,
) : BaseDependencyVertex(), DependencyCellVertex<ValueT> {
    private var mutableStableValue: ValueT = initialValue

    private val stableValue: ValueT
        get() = mutableStableValue

    private var setUpdate: Update<ValueT>? = null

    fun set(
        processingContext: Transaction.ProcessingContext,
        newValue: ValueT,
    ) {
        setUpdate = Update(
            newValue = newValue,
        )

        ensureMarkedDirty(
            processingContext = processingContext,
        )

        enqueueDependentsForVisiting(
            processingContext = processingContext,
        )
    }

    override fun pullStableValue(
        processingContext: Transaction.ProcessingContext,
    ): ValueT = stableValue

    override fun pullUpdate(
        processingContext: Transaction.ProcessingContext,
    ): Update<ValueT>? = setUpdate

    override fun onFirstDependentAdded() {
    }

    override fun onLastDependentRemoved() {
    }

    override fun clean() {
        setUpdate = null
    }
}
