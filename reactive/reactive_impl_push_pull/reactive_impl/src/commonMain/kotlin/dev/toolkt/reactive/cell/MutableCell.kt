package dev.toolkt.reactive.cell

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.MutableCellVertex

class MutableCell<ValueT>(
    initialValue: ValueT,
) : Cell<ValueT> {
    override val vertex = MutableCellVertex(
        initialValue = initialValue,
    )

    /**
     * Sets a new value to this cell.
     *
     * This method must be called from outside the reactive system.
     */
    fun set(
        newValue: ValueT,
    ) {
        Transaction.execute { context ->
            vertex.set(
                context = context,
                newValue = newValue,
            )
        }
    }
}
