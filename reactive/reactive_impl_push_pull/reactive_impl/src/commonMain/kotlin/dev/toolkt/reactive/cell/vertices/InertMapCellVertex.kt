package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.cell.vertices.CellVertex.RetrievalMode

class InertMapCellVertex<SourceValueT, TransformedValueT>(
    private val sourceCellVertex: InertCellVertex<SourceValueT>,
    private val transform: (SourceValueT) -> TransformedValueT,
) : BaseDerivedInertCellVertex<TransformedValueT>() {
    override fun computeInertValue(
        retrievalMode: RetrievalMode,
    ): TransformedValueT = transform(
        sourceCellVertex.retrieve(
            retrievalMode = retrievalMode,
        ),
    )
}
