package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.DependentVertex
import dev.toolkt.reactive.Transaction

interface InertCellVertex<ValueT> : CellVertex<ValueT> {
    override fun pullUpdateObserving(
        context: Transaction.ProcessingContext,
        dependentVertex: DependentVertex,
    ): CellVertex.NilUpdate

    override fun pullUpdateSubsequent(
        context: Transaction.ProcessingContext,
    ): CellVertex.NilUpdate
}
