package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.IntermediateDynamicVertex
import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.CellVertex.Update

abstract class IntermediateCellVertex<ValueT>() : IntermediateDynamicVertex<Update<ValueT>>(),
    DependencyCellVertex<ValueT> {
    final override fun pullUpdate(
        processingContext: Transaction.ProcessingContext,
    ): Update<ValueT>? = pullNotification(
        processingContext = processingContext,
    )
}
