package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.CellVertex.UpdatedValue
import dev.toolkt.reactive.globalFinalizationRegistry
import dev.toolkt.reactive.registerDependent

class HoldCellVertex<ValueT> private constructor(
    private val sourceEventStreamVertex: DependencyEventStreamVertex<ValueT>,
    initialValue: ValueT,
) : InherentCellVertex<ValueT>() {
    companion object {
        fun <ValueT> construct(
            context: Transaction.Context,
            sourceEventStreamVertex: DependencyEventStreamVertex<ValueT>,
            initialValue: ValueT,
        ): HoldCellVertex<ValueT> = HoldCellVertex(
            sourceEventStreamVertex = sourceEventStreamVertex,
            initialValue = initialValue,
        ).apply {
            sourceEventStreamVertex.registerDependent(
                context = context,
                this,
            )

            ensureProcessed(
                context = context,
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
        context: Transaction.Context,
    ): UpdatedValue<ValueT>? {
        val sourceOccurrence = sourceEventStreamVertex.pullEmittedEvent(
            context = context,
        ) ?: return null

        return UpdatedValue(
            value = sourceOccurrence.event,
        )
    }

    override fun pullStableValue(
        context: Transaction.Context,
    ): ValueT = heldStableValue

    override fun persist(
        newValue: ValueT,
    ) {
        heldStableValue = newValue
    }
}
