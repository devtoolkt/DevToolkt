package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction

abstract class InherentCellVertex<ValueT>(
    initialStableValue: ValueT,
) : BaseDynamicCellVertex<ValueT>() {
    private var stableValue = initialStableValue

    final override fun processObserved(
        context: Transaction.ProcessingContext,
        wasFirst: Boolean,
    ): CellVertex.Update<ValueT> = process(
        context = context,
    )

    final override fun processTriggered(
        context: Transaction.ProcessingContext,
    ): CellVertex.Update<ValueT> = process(
        context = context,
    )

    final override fun persist(
        updatedValue: ValueT,
    ) {
        stableValue = updatedValue
    }

    final override fun fetchOldValue(): ValueT = stableValue

    final override fun sampleOldValue(
        context: Transaction.ProcessingContext,
    ): ValueT = stableValue

    final override fun onFirstObserverAdded() {
    }

    final override fun onLastObserverRemoved() {
    }

    protected abstract fun process(
        context: Transaction.ProcessingContext,
    ): CellVertex.Update<ValueT>
}
