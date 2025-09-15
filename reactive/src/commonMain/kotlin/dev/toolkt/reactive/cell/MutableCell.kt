package dev.toolkt.reactive.cell

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.MutableCellVertex

class MutableCell<ValueT>(
    initialValue: ValueT,
) : BaseOperatedCell<ValueT> {
    override val vertex = MutableCellVertex(
        initialValue = initialValue,
    )

    fun setExternally(
        newValue: ValueT,
    ) {
        vertex.prepareNewValue(
            newValue = newValue,
        )

        Transaction.execute { preProcessingContext ->
            vertex.preProcess(
                preProcessingContext = preProcessingContext,
            )
        }

        vertex.clearNewValue()
    }
}
