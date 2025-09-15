package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.CellVertex.Update

class MutableCellVertex<ValueT>(
    initialValue: ValueT,
) : StatefulCellVertex<ValueT>() {
    private var mutableStableValue: ValueT = initialValue

    private var preparedVolatileUpdate: Update<ValueT>? = null

    override fun prepare(
        processingContext: Transaction.ProcessingContext,
    ): Update<ValueT>? = preparedVolatileUpdate

    fun prepareNewValue(
        newValue: ValueT,
    ) {
        if (preparedVolatileUpdate != null) {
            throw IllegalStateException("There is already a pending prepared update $preparedVolatileUpdate")
        }

        preparedVolatileUpdate = Update(
            newValue = newValue,
        )
    }

    fun clearNewValue() {
        preparedVolatileUpdate = null
    }

    override fun stabilizeState(
        stabilizationContext: Transaction.StabilizationContext,
        message: Update<ValueT>?,
    ) {
        message?.let { update ->
            mutableStableValue = update.newValue
        }
    }

    override fun pullStableValue(
        processingContext: Transaction.ProcessingContext,
    ): ValueT = mutableStableValue
}
