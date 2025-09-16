package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.BaseDependencyVertex
import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.CellVertex.UpdatedValue

class MutableCellVertex<ValueT>(
    initialValue: ValueT,
) : BaseDependencyVertex(), DependencyCellVertex<ValueT> {
    private var mutableStableValue: ValueT = initialValue

    private val stableValue: ValueT
        get() = mutableStableValue

    private var sourceUpdatedValue: UpdatedValue<ValueT>? = null

    override fun pullStableValue(
        context: Transaction.Context,
    ): ValueT = stableValue

    override fun pullUpdatedValue(
        context: Transaction.Context,
    ): UpdatedValue<ValueT>? = sourceUpdatedValue

    override fun onFirstDependentAdded() {
    }

    override fun onLastDependentRemoved() {
    }

    fun set(
        context: Transaction.Context,
        newValue: ValueT,
    ) {
        sourceUpdatedValue = UpdatedValue(
            value = newValue,
        )

        enqueueDependentsForVisiting(
            context = context,
        )
    }

    fun reset() {
        sourceUpdatedValue = null
    }
}
