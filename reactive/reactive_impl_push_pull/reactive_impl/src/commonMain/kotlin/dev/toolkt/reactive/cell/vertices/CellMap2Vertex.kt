package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction

class CellMap2Vertex<ValueT1, ValueT2, ResultT>(
    private val sourceCell1Vertex: DependencyCellVertex<ValueT1>,
    private val sourceCell2Vertex: DependencyCellVertex<ValueT2>,
    private val transform: (ValueT1, ValueT2) -> ResultT,
) : StatelessCellVertex<ResultT>() {
    override fun prepareStateless(
        processingContext: Transaction.ProcessingContext,
    ): CellVertex.Update<ResultT>? {
        val sourceUpdate1 = sourceCell1Vertex.pullUpdate(
            processingContext = processingContext,
        )

        val sourceUpdate2 = sourceCell2Vertex.pullUpdate(
            processingContext = processingContext,
        )

        if (sourceUpdate1 == null && sourceUpdate2 == null) {
            return null
        }

        val source1LatestValue = when (sourceUpdate1) {
            null -> sourceCell1Vertex.pullStableValue(
                processingContext = processingContext,
            )

            else -> sourceUpdate1.newValue
        }

        val source2LatestValue = when (sourceUpdate2) {
            null -> sourceCell2Vertex.pullStableValue(
                processingContext = processingContext,
            )

            else -> sourceUpdate2.newValue
        }

        return CellVertex.Update(
            newValue = transform(
                source1LatestValue,
                source2LatestValue,
            ),
        )
    }

    override fun activate(
        expansionContext: Transaction.ExpansionContext,
    ) {
        sourceCell1Vertex.addDependent(
            expansionContext = expansionContext,
            vertex = this,
        )

        sourceCell2Vertex.addDependent(
            expansionContext = expansionContext,
            vertex = this,
        )
    }

    override fun deactivate(
        shrinkageContext: Transaction.ShrinkageContext,
    ) {
        sourceCell1Vertex.removeDependent(
            shrinkageContext = shrinkageContext,
            vertex = this,
        )

        sourceCell2Vertex.removeDependent(
            shrinkageContext = shrinkageContext,
            vertex = this,
        )
    }

    override fun computeStableValue(
        processingContext: Transaction.ProcessingContext,
    ): ResultT = transform(
        sourceCell1Vertex.pullStableValue(
            processingContext = processingContext,
        ),
        sourceCell2Vertex.pullStableValue(
            processingContext = processingContext,
        ),
    )
}
