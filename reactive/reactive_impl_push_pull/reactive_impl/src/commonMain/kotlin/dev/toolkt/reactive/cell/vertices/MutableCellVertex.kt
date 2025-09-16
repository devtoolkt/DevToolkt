package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.CellVertex.Update

class MutableCellVertex<ValueT>(
    initialValue: ValueT,
) : StatefulCellVertex<ValueT>() {
    private var mutableStableValue: ValueT = initialValue

    private var preparedUpdate: Update<ValueT>? = null

    fun preProcess(
        processingContext: Transaction.ProcessingContext,
        newValue: ValueT,
    ) {
        preparedUpdate = Update(
            newValue = newValue,
        )

        ensureEffectivelyProcessed(
            processingContext = processingContext,
        )
    }

    override fun prepare(
        processingContext: Transaction.ProcessingContext,
    ): Update<ValueT>? = preparedUpdate

    override fun pullStableValue(
        processingContext: Transaction.ProcessingContext,
    ): ValueT = mutableStableValue

    override fun stabilize(
        latePostProcessingContext: Transaction.LatePostProcessingContext,
        message: Update<ValueT>?,
    ) {
        message?.let { update ->
            mutableStableValue = update.newValue
        }

        preparedUpdate = null
    }
}
