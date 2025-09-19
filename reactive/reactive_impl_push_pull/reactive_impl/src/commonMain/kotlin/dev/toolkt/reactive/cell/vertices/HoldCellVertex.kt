package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.CellVertex.EffectiveUpdate
import dev.toolkt.reactive.cell.vertices.CellVertex.Update
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex
import dev.toolkt.reactive.event_stream.vertices.toUpdate

class HoldCellVertex<ValueT> private constructor(
    private val sourceEventStreamVertex: EventStreamVertex<ValueT>,
    initialStableValue: ValueT,
) : InherentDependentCellVertex<ValueT>(
    initialStableValue = initialStableValue,
) {
    companion object {
        fun <ValueT> construct(
            context: Transaction.ProcessingContext,
            sourceEventStreamVertex: EventStreamVertex<ValueT>,
            initialValue: ValueT,
        ): HoldCellVertex<ValueT> = HoldCellVertex(
            sourceEventStreamVertex = sourceEventStreamVertex,
            initialStableValue = initialValue,
        ).apply {
            initialize(
                context = context,
            )
        }
    }

    override fun processAttaching(
        context: Transaction.ProcessingContext,
    ): Update<ValueT> {
        // TODO: Figure out weak dependents!
        // We can't add this vertex directly to the dependency's dependent set
        val initialSourceOccurrence = sourceEventStreamVertex.pullOccurrenceSubscribing(
            context = context,
            dependentVertex = this,
        )

        return when (initialSourceOccurrence) {
            EventStreamVertex.NilOccurrence -> CellVertex.NilUpdate

            is EventStreamVertex.EffectiveOccurrence -> EffectiveUpdate(
                updatedValue = initialSourceOccurrence.event,
            )
        }
    }

    override fun process(
        context: Transaction.ProcessingContext,
    ): Update<ValueT> {
        val sourceOccurrence = sourceEventStreamVertex.pullOccurrenceSubsequent(
            context = context,
        )

        return sourceOccurrence.toUpdate()
    }
}
