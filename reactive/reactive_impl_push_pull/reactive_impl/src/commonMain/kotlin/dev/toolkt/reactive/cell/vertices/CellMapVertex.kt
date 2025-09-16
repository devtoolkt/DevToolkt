package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction

class CellMapVertex<SourceValueT, TransformedValueT>(
    private val sourceCellVertex: DependencyCellVertex<SourceValueT>,
    private val transform: (SourceValueT) -> TransformedValueT,
) : StatelessCellVertex<TransformedValueT>() {
    override fun process(
        processingContext: Transaction.ProcessingContext,
    ): CellVertex.UpdatedValue<TransformedValueT>? {
        val sourceUpdate = sourceCellVertex.pullUpdatedValue(
            processingContext = processingContext,
        )

        return sourceUpdate?.map(
            transform = transform,
        )
    }

    override fun activate() {
        sourceCellVertex.addDependent(
            dependentVertex = this,
        )
    }

    override fun deactivate() {
        sourceCellVertex.removeDependent(
            dependentVertex = this,
        )
    }

    override fun computeStableValue(
        processingContext: Transaction.ProcessingContext,
    ): TransformedValueT = transform(
        sourceCellVertex.pullStableValue(
            processingContext = processingContext,
        ),
    )
}

