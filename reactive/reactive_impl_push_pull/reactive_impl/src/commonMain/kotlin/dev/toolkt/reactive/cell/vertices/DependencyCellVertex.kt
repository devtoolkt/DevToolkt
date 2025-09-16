package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.DependencyVertex
import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.CellVertex.UpdatedValue

interface DependencyCellVertex<ValueT> : CellVertex<ValueT>, DependencyVertex {
    /**
     * Returns a volatile update of this cell, triggering processing if necessary.
     */
    fun pullUpdatedValue(
        processingContext: Transaction.ProcessingContext,
    ): UpdatedValue<ValueT>?
}
