package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.CellVertex
import dev.toolkt.reactive.cell.vertices.toOccurrence

class UpdatedValuesEventStreamVertex<ValueT>(
    private val sourceCellVertex: CellVertex<ValueT>,
) : BaseDerivedEventStreamVertex<ValueT>() {
    override fun processResuming(
        context: Transaction.ProcessingContext,
    ): EventStreamVertex.Occurrence<ValueT> = sourceCellVertex.pullUpdateObserving(
        context = context,
        dependentVertex = this,
    ).toOccurrence()

    override fun processFollowing(
        context: Transaction.ProcessingContext,
    ): EventStreamVertex.Occurrence<ValueT> = sourceCellVertex.pullUpdateSubsequent(
        context = context,
    ).toOccurrence()

    override fun resume() {
        sourceCellVertex.observe(
            dependentVertex = this,
        )
    }

    override fun pause() {
        sourceCellVertex.unobserve(
            dependentVertex = this,
        )
    }
}
