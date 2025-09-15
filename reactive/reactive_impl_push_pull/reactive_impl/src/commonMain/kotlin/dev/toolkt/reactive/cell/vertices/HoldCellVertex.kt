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
            preProcessingContext: Transaction.PreProcessingContext,
            sourceEventStreamVertex: DependencyEventStreamVertex<ValueT>,
            initialValue: ValueT,
        ): HoldCellVertex<ValueT> = HoldCellVertex(
            sourceEventStreamVertex = sourceEventStreamVertex,
            initialValue = initialValue,
        ).apply {
            sourceEventStreamVertex.registerDependent(
                preProcessingContext = preProcessingContext,
                vertex = this,
            )

            preProcess(
                preProcessingContext = preProcessingContext,
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
        preProcessingContext: Transaction.PreProcessingContext,
    ): Update<ValueT>? {
        val sourceOccurrence = sourceEventStreamVertex.pullOccurrence(
            preProcessingContext = preProcessingContext,
        ) ?: return null

        return Update(
            newValue = sourceOccurrence.event,
        )
    }

    override fun stabilize(
        postProcessingContext: Transaction.PostProcessingContext,
        message: Update<ValueT>?,
    ) {
        message?.let { update ->
            heldStableValue = update.newValue
        }
    }

    override fun pullStableValue(
        preProcessingContext: Transaction.PreProcessingContext,
    ): ValueT = heldStableValue
}
