package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.BaseVertex
import dev.toolkt.reactive.DependentVertex
import dev.toolkt.reactive.Transaction

abstract class BaseInertCellVertex<ValueT> : BaseVertex(), InertCellVertex<ValueT> {
    final override fun pullUpdateObserving(
        context: Transaction.ProcessingContext,
        dependentVertex: DependentVertex,
    ): CellVertex.NilUpdate = CellVertex.NilUpdate

    final override fun pullUpdateSubsequent(
        context: Transaction.ProcessingContext,
    ): CellVertex.NilUpdate = CellVertex.NilUpdate

    final override fun observe(
        dependentVertex: DependentVertex,
    ) {
    }

    final override fun unobserve(
        dependentVertex: DependentVertex,
    ) {
    }

    final override fun commit() {
    }
}
