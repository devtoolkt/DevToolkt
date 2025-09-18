package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.DependentVertex
import dev.toolkt.reactive.Transaction

interface DependencyCellVertex<ValueT> : CellVertex<ValueT> {
    fun pullUpdateObserving(
        context: Transaction.ProcessingContext,
        dependentVertex: DependentVertex,
    ): CellVertex.Update<ValueT>

    fun observe(
        dependentVertex: DependentVertex,
    )

    fun pullUpdateSubsequent(
        context: Transaction.ProcessingContext,
    ): CellVertex.Update<ValueT>

    fun unobserve(
        dependentVertex: DependentVertex,
    )
}
