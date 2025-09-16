package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.CellVertex.Update
import dev.toolkt.reactive.globalFinalizationRegistry

class HoldCellVertex<ValueT> private constructor(
    private val sourceEventStreamVertex: DependencyEventStreamVertex<ValueT>,
    initialValue: ValueT,
) : StatefulCellVertex<ValueT>() {
    companion object {
        fun <ValueT> construct(
            processingContext: Transaction.ProcessingContext,
            sourceEventStreamVertex: DependencyEventStreamVertex<ValueT>,
            initialValue: ValueT,
        ): HoldCellVertex<ValueT> = HoldCellVertex(
            sourceEventStreamVertex = sourceEventStreamVertex,
            initialValue = initialValue,
        ).apply {
            sourceEventStreamVertex.registerDependent(
                processingContext = processingContext,
                vertex = this,
            )

            ensureEffectivelyProcessed(
                processingContext = processingContext,
            )

            // TODO: Figure out weak dependents!
            globalFinalizationRegistry.register(
                target = this,
            ) {
            }
        }
    }

    private var heldStableValue: ValueT = initialValue

    override fun prepare(
        processingContext: Transaction.ProcessingContext,
    ): Update<ValueT>? {
        val sourceOccurrence = sourceEventStreamVertex.pullOccurrence(
            processingContext = processingContext,
        ) ?: return null

        return Update(
            newValue = sourceOccurrence.event,
        )
    }

    override fun stabilize(
        latePostProcessingContext: Transaction.LatePostProcessingContext,
        message: Update<ValueT>?,
    ) {
        message?.let { update ->
            heldStableValue = update.newValue
        }
    }

    override fun pullStableValue(
        processingContext: Transaction.ProcessingContext,
    ): ValueT = heldStableValue
}
