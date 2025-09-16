package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.DependencyCellVertex

class UpdatedValuesEventStreamVertex<ValueT>(
    private val sourceCellVertex: DependencyCellVertex<ValueT>,
) : StatelessEventStreamVertex<ValueT>() {
    override fun process(
        processingContext: Transaction.ProcessingContext,
    ): EventStreamVertex.Occurrence<ValueT>? {
        val sourceUpdate = sourceCellVertex.pullUpdate(
            processingContext = processingContext,
        ) ?: return null

        return EventStreamVertex.Occurrence(
            event = sourceUpdate.newValue,
        )
    }

    override fun resume() {
        sourceCellVertex.addDependent(
            dependentVertex = this,
        )
    }

    override fun pause() {
        sourceCellVertex.removeDependent(
            dependentVertex = this,
        )
    }
}
