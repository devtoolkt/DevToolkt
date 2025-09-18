package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.CellVertex.RetrievalMode

class CellMap2Vertex<ValueT1, ValueT2, ResultT>(
    private val sourceCell1Vertex: DependencyCellVertex<ValueT1>,
    private val sourceCell2Vertex: DependencyCellVertex<ValueT2>,
    private val transform: (ValueT1, ValueT2) -> ResultT,
) : SimpleDerivedCellVertex<ResultT>() {
    override fun process(
        context: Transaction.ProcessingContext,
        processingMode: ProcessingMode,
    ): CellVertex.Update<ResultT> {
        val sourceUpdate1 = sourceCell1Vertex.pullUpdate(
            context = context,
            processingMode = processingMode,
        )

        val sourceUpdate2 = sourceCell2Vertex.pullUpdate(
            context = context,
            processingMode = processingMode,
        )

        if (sourceUpdate1 is CellVertex.NilUpdate && sourceUpdate2 is CellVertex.NilUpdate) {
            return CellVertex.NilUpdate
        }

        val source1LatestValue = when (sourceUpdate1) {
            CellVertex.NilUpdate -> sourceCell1Vertex.sampleOldValue(
                context = context,
            )

            is CellVertex.EffectiveUpdate -> sourceUpdate1.updatedValue
        }

        val source2LatestValue = when (sourceUpdate2) {
            CellVertex.NilUpdate -> sourceCell2Vertex.sampleOldValue(
                context = context,
            )

            is CellVertex.EffectiveUpdate -> sourceUpdate2.updatedValue
        }

        return CellVertex.EffectiveUpdate(
            updatedValue = transform(
                source1LatestValue,
                source2LatestValue,
            ),
        )
    }

    override fun activate() {
        sourceCell1Vertex.observe(
            dependentVertex = this,
        )

        sourceCell2Vertex.observe(
            dependentVertex = this,
        )
    }

    override fun deactivate() {
        sourceCell1Vertex.unobserve(
            dependentVertex = this,
        )

        sourceCell2Vertex.unobserve(
            dependentVertex = this,
        )
    }

    override fun computeOldValue(
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
