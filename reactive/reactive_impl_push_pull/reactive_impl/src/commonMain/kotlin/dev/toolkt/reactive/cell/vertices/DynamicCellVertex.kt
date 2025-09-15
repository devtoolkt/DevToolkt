package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.DynamicVertex
import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.CellVertex.Update

interface DynamicCellVertex<ValueT> : CellVertex<ValueT>, DynamicVertex {
    /**
     * Returns a volatile update of this cell, triggering processing if necessary.
     */
    fun pullUpdate(
        preProcessingContext: Transaction.PreProcessingContext,
    ): Update<ValueT>?
}
