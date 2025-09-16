package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.DependencyCellVertex

class UpdatedValuesEventStreamVertex<ValueT>(
    private val sourceCellVertex: DependencyCellVertex<ValueT>,
) : StatelessEventStreamVertex<ValueT>() {
    override fun process(
        processingContext: Transaction.ProcessingContext,
    ): EventStreamVertex.EmittedEvent<ValueT>? {
        val sourceUpdate = sourceCellVertex.pullUpdatedValue(
            processingContext = processingContext,
        ) ?: return null

        return EventStreamVertex.EmittedEvent(
            event = sourceUpdate.value,
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
