package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction

class DynamicMap3CellVertex<ValueT1, ValueT2, ValueT3, ResultT>(
    private val sourceCell1Vertex: CellVertex<ValueT1>,
    private val sourceCell2Vertex: CellVertex<ValueT2>,
    private val sourceCell3Vertex: CellVertex<ValueT3>,
    private val transform: (ValueT1, ValueT2, ValueT3) -> ResultT,
) : BaseSimpleDerivedCellVertex<ResultT>() {
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

        val sourceUpdate3 = sourceCell3Vertex.pullUpdate(
            context = context,
            processingMode = processingMode,
        )

        if (sourceUpdate1 is CellVertex.NilUpdate && sourceUpdate2 is CellVertex.NilUpdate && sourceUpdate3 is CellVertex.NilUpdate) {
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

        val source3LatestValue = when (sourceUpdate3) {
            CellVertex.NilUpdate -> sourceCell3Vertex.sampleOldValue(
                context = context,
            )

            is CellVertex.EffectiveUpdate -> sourceUpdate3.updatedValue
        }

        return CellVertex.EffectiveUpdate(
            updatedValue = transform(
                source1LatestValue,
                source2LatestValue,
                source3LatestValue,
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

        sourceCell3Vertex.observe(
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

        sourceCell3Vertex.unobserve(
            dependentVertex = this,
        )
    }

    override fun computeOldValue(
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
