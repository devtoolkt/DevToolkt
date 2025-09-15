package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction

class CellMapVertex<SourceValueT, TransformedValueT>(
    private val sourceCellVertex: DependencyCellVertex<SourceValueT>,
    private val transform: (SourceValueT) -> TransformedValueT,
) : StatelessCellVertex<TransformedValueT>() {
    override fun prepareStateless(
        preProcessingContext: Transaction.PreProcessingContext,
    ): CellVertex.Update<TransformedValueT>? {
        val sourceUpdate = sourceCellVertex.pullUpdate(
            preProcessingContext = preProcessingContext,
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
        preProcessingContext: Transaction.PreProcessingContext,
    ): TransformedValueT = transform(
        sourceCellVertex.pullStableValue(
            preProcessingContext = preProcessingContext,
        ),
    )
}

