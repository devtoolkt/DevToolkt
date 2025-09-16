package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction

class CellMapVertex<SourceValueT, TransformedValueT>(
    private val sourceCellVertex: DependencyCellVertex<SourceValueT>,
    private val transform: (SourceValueT) -> TransformedValueT,
) : StatelessCellVertex<TransformedValueT>() {
    override fun prepareStateless(
        processingContext: Transaction.ProcessingContext,
    ): CellVertex.Update<TransformedValueT>? {
        val sourceUpdate = sourceCellVertex.pullUpdate(
            processingContext = processingContext,
        )

        return sourceUpdate?.map(
            transform = transform,
        )
    }

    override fun activate(
        expansionContext: Transaction.ExpansionContext,
    ) {
        sourceCellVertex.addDependent(
            expansionContext = expansionContext,
            vertex = this,
        )
    }

    override fun deactivate(
        shrinkageContext: Transaction.ShrinkageContext,
    ) {
        sourceCellVertex.removeDependent(
            shrinkageContext = shrinkageContext,
            vertex = this,
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

