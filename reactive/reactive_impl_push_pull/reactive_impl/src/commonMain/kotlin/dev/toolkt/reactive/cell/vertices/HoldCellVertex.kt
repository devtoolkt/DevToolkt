package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.CellVertex.UpdatedValue
import dev.toolkt.reactive.globalFinalizationRegistry
import dev.toolkt.reactive.registerDependent

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
                this,
            )

            ensureProcessed(
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

    override fun process(
        processingContext: Transaction.ProcessingContext,
    ): UpdatedValue<ValueT>? {
        val sourceOccurrence = sourceEventStreamVertex.pullEmittedEvent(
            processingContext = processingContext,
        ) ?: return null

        return UpdatedValue(
            value = sourceOccurrence.event,
        )
    }

    override fun pullStableValue(
        processingContext: Transaction.ProcessingContext,
    ): ValueT = heldStableValue

    override fun persist(
        newValue: ValueT,
    ) {
        heldStableValue = newValue
    }
}
