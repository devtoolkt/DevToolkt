package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.CellVertex.Update

class MutableCellVertex<ValueT>(
    initialValue: ValueT,
) : StatefulCellVertex<ValueT>() {
    private var mutableStableValue: ValueT = initialValue

    private var preparedVolatileUpdate: Update<ValueT>? = null

    fun preProcess(
        processingContext: Transaction.ProcessingContext,
        newValue: ValueT,
    ) {
        preparedVolatileUpdate = Update(
            newValue = newValue,
        )

        ensureVisited(
            processingContext = processingContext,
        )
    }

    override fun prepare(
        processingContext: Transaction.ProcessingContext,
    ): Update<ValueT>? = preparedVolatileUpdate

    override fun pullStableValue(
        processingContext: Transaction.ProcessingContext,
    ): ValueT = mutableStableValue

    override fun stabilize(
        postProcessingContext: Transaction.PostProcessingContext,
        message: Update<ValueT>?,
    ) {
        message?.let { update ->
            mutableStableValue = update.newValue
        }

        preparedVolatileUpdate = null
    }
}
