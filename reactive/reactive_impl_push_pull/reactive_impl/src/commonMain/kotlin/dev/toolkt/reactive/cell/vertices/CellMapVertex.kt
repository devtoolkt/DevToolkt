package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction

class CellMapVertex<SourceValueT, TransformedValueT>(
    private val sourceCellVertex: BaseCellVertex<SourceValueT>,
    private val transform: (SourceValueT) -> TransformedValueT,
) : StatelessCellVertex<TransformedValueT>() {
    override fun process(
        processingContext: Transaction.ProcessingContext,
    ) {
        val sourceUpdate = sourceCellVertex.fetchVolatileUpdate(
            processingContext = processingContext,
        )

        val computedUpdate = sourceUpdate?.map(transform)

        if (computedUpdate != null) {
            cacheVolatileUpdate(
                processingContext = processingContext,
                update = computedUpdate,
            )
        }
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

}
