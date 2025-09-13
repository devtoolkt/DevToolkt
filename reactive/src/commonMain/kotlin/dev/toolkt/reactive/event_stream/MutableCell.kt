package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.MutableCellVertex

class MutableCell<ValueT>(
    initialValue: ValueT,
) : Cell<ValueT> {
    private val vertex = MutableCellVertex(
        initialValue = initialValue,
    )

    fun setExternally(
        newValue: ValueT,
    ) {
        vertex.prepareNewValue(
            newValue = newValue,
        )

        Transaction.execute(
            sourceVertex = vertex,
        )

    }
}
