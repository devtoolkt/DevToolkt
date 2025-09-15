package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.PropagativeVertex
import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.CellVertex.Update

abstract class PropagativeCellVertex<ValueT>() : PropagativeVertex<Update<ValueT>>(), DependencyCellVertex<ValueT> {
    final override fun pullUpdate(
        preProcessingContext: Transaction.PreProcessingContext,
    ): Update<ValueT>? = pullMessage(
        preProcessingContext = preProcessingContext,
    )
}
