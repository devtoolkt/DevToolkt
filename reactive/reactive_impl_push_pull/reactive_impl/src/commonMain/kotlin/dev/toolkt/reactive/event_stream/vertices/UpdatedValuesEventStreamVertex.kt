package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.DependencyCellVertex
import dev.toolkt.reactive.cell.vertices.DynamicCellVertex

class UpdatedValuesEventStreamVertex<ValueT>(
    private val sourceCellVertex: DependencyCellVertex<ValueT>,
) : StatelessEventStreamVertex<ValueT>() {
    override fun prepare(
        preProcessingContext: Transaction.PreProcessingContext,
    ): EventStreamVertex.Occurrence<ValueT>? {
        val sourceUpdate = sourceCellVertex.pullUpdate(
            preProcessingContext = preProcessingContext,
        ) ?: return null

        return EventStreamVertex.Occurrence(
            event = sourceUpdate.newValue,
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
}
