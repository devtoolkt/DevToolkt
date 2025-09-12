package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.Vertex
import dev.toolkt.reactive.cell.vertices.BaseCellVertex.Update

interface CellVertex<ValueT> : Vertex {
    /**
     * Returns a volatile update of this cell, triggering processing if necessary.
     */
    fun fetchVolatileUpdate(
        processingContext: Transaction.ProcessingContext,
    ): Update<ValueT>?

    /**
     * Returns a currently stored volatile update of this cell. This doesn't trigger processing, so it should be used
     * with care.
     */
    val storedVolatileUpdate: Update<ValueT>?
}
