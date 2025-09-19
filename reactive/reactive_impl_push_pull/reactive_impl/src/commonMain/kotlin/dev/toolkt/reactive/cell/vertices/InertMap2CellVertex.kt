package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.cell.vertices.CellVertex.RetrievalMode

class InertMap2CellVertex<ValueT1, ValueT2, ResultT>(
    private val sourceCell1Vertex: InertCellVertex<ValueT1>,
    private val sourceCell2Vertex: InertCellVertex<ValueT2>,
    private val transform: (ValueT1, ValueT2) -> ResultT,
) : BaseDerivedInertCellVertex<ResultT>() {
    override fun computeInertValue(
        retrievalMode: RetrievalMode,
    ): ResultT = transform(
        sourceCell1Vertex.retrieve(
            retrievalMode = retrievalMode,
        ),
        sourceCell2Vertex.retrieve(
            retrievalMode = retrievalMode,
        ),
    )
}
