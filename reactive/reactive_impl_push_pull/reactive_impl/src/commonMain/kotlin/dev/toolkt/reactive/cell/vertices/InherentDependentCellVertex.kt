package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.DependentVertex
import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.globalFinalizationRegistry

abstract class InherentDependentCellVertex<ValueT>(
    initialStableValue: ValueT,
) : InherentCellVertex<ValueT>(
    initialStableValue = initialStableValue,
), DependentVertex {
    final override fun visit(
        context: Transaction.ProcessingContext,
    ) {
        ensureProcessedTriggered(
            context = context,
        )
    }

    protected fun initialize(
        context: Transaction.ProcessingContext,
    ) {
        cacheUpdate(
            context = context,
            update = processAttaching(
                context = context,
            ),
        )

        // TODO: Figure out weak dependents!
        globalFinalizationRegistry.register(
            target = this,
        ) {
            //  We can't refer this vertex in the callback
        }
    }

    final override fun reset(
        tag: BaseDynamicCellVertex.Tag,
    ) {
    }

    protected abstract fun processAttaching(
        context: Transaction.ProcessingContext,
    ): CellVertex.Update<ValueT>
}
