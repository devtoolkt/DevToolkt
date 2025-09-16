package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction

class CellMapVertex<SourceValueT, TransformedValueT>(
    private val sourceCellVertex: DependencyCellVertex<SourceValueT>,
    private val transform: (SourceValueT) -> TransformedValueT,
) : StatelessCellVertex<TransformedValueT>() {
    override fun process(
        context: Transaction.Context,
    ): CellVertex.UpdatedValue<TransformedValueT>? {
        val sourceUpdate = sourceCellVertex.pullUpdatedValue(
            context = context,
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
        context: Transaction.Context,
    ): TransformedValueT = transform(
        sourceCellVertex.pullStableValue(
            processingContext = context,
        ),
    )
}

