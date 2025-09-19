package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.CellVertex.RetrievalMode

class DynamicMapCellVertex<SourceValueT, TransformedValueT>(
    private val sourceCellVertex: DependencyCellVertex<SourceValueT>,
    private val transform: (SourceValueT) -> TransformedValueT,
) : BaseSimpleDerivedCellVertex<TransformedValueT>() {
    override fun process(
        context: Transaction.ProcessingContext,
        processingMode: ProcessingMode,
    ): CellVertex.Update<TransformedValueT> {
        val sourceUpdate = sourceCellVertex.pullUpdate(
            context = context,
            processingMode = processingMode,
        )

        return sourceUpdate.map(
            transform = transform,
        )
    }

    override fun activate() {
        sourceCellVertex.observe(
            dependentVertex = this,
        )
    }

    override fun deactivate() {
        sourceCellVertex.unobserve(
            dependentVertex = this,
        )
    }

    override fun computeOldValue(
        retrievalMode: RetrievalMode,
    ): TransformedValueT = transform(
        sourceCellVertex.retrieve(
            retrievalMode = retrievalMode,
        ),
    )
}
