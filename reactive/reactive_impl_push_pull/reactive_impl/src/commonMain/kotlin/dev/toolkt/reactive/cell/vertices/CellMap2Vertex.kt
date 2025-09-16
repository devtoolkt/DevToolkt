package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction

class CellMap2Vertex<ValueT1, ValueT2, ResultT>(
    private val sourceCell1Vertex: DependencyCellVertex<ValueT1>,
    private val sourceCell2Vertex: DependencyCellVertex<ValueT2>,
    private val transform: (ValueT1, ValueT2) -> ResultT,
) : StatelessCellVertex<ResultT>() {
    override fun process(
        context: Transaction.Context,
    ): CellVertex.UpdatedValue<ResultT>? {
        val sourceUpdate1 = sourceCell1Vertex.pullUpdatedValue(
            context = context,
        )

        val sourceUpdate2 = sourceCell2Vertex.pullUpdatedValue(
            context = context,
        )

        if (sourceUpdate1 == null && sourceUpdate2 == null) {
            return null
        }

        val source1LatestValue = when (sourceUpdate1) {
            null -> sourceCell1Vertex.pullStableValue(
                processingContext = context,
            )

            else -> sourceUpdate1.value
        }

        val source2LatestValue = when (sourceUpdate2) {
            null -> sourceCell2Vertex.pullStableValue(
                processingContext = context,
            )

            else -> sourceUpdate2.value
        }

        return CellVertex.UpdatedValue(
            value = transform(
                source1LatestValue,
                source2LatestValue,
            ),
        )
    }

    override fun activate() {
        sourceCell1Vertex.addDependent(
            dependentVertex = this,
        )

        sourceCell2Vertex.addDependent(
            dependentVertex = this,
        )
    }

    override fun deactivate() {
        sourceCell1Vertex.removeDependent(
            dependentVertex = this,
        )

        sourceCell2Vertex.removeDependent(
            dependentVertex = this,
        )
    }

    override fun computeStableValue(
        context: Transaction.Context,
    ): ResultT = transform(
        sourceCell1Vertex.pullStableValue(
            processingContext = context,
        ),
        sourceCell2Vertex.pullStableValue(
            processingContext = context,
        ),
    )
}
