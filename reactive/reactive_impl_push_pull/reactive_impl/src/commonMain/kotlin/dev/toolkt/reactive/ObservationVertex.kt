package dev.toolkt.reactive

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.vertices.CellVertex

class ObservationVertex<EventT>(
    private val sourceCellVertex: CellVertex<EventT>,
    private val observer: Cell.Observer<EventT>,
) : DependentVertex {
    private var receivedUpdate: CellVertex.Update<EventT>? = null

    override fun visit(
        context: Transaction.ProcessingContext,
    ) {
        val sourceUpdate = sourceCellVertex.pullUpdateSubsequent(
            context = context,
        )

        when (sourceUpdate) {
            CellVertex.NilUpdate -> {}

            is CellVertex.EffectiveUpdate -> {
                context.markDirty(
                    dirtyVertex = this,
                )
            }
        }

        receivedUpdate = sourceUpdate
    }

    override fun commit() {
        // TODO: Support freezing
        when (val receivedUpdate = this.receivedUpdate) {
            is CellVertex.EffectiveUpdate -> {
                observer.handleNotification(
                    notification = Cell.IntermediateUpdateNotification(
                        updatedValue = receivedUpdate.updatedValue,
                    ),
                )
            }

            else -> {}
        }
    }

    override fun reset() {
        receivedUpdate = null
    }
}
