package dev.toolkt.reactive.cell.vertices

class InertMap3CellVertex<ValueT1, ValueT2, ValueT3, ResultT>(
    private val sourceCell1Vertex: InertCellVertex<ValueT1>,
    private val sourceCell2Vertex: InertCellVertex<ValueT2>,
    private val sourceCell3Vertex: InertCellVertex<ValueT3>,
    private val transform: (ValueT1, ValueT2, ValueT3) -> ResultT,
) : BaseDerivedInertCellVertex<ResultT>() {
    override fun computeInertValue(
        retrievalMode: CellVertex.RetrievalMode,
    ): ResultT = transform(
        sourceCell1Vertex.retrieve(
            retrievalMode = retrievalMode,
        ),
        sourceCell2Vertex.retrieve(
            retrievalMode = retrievalMode,
        ),
        sourceCell3Vertex.retrieve(
            retrievalMode = retrievalMode,
        ),
    )
}
