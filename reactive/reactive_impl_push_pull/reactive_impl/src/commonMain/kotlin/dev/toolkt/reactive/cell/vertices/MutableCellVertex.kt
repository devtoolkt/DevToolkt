package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction

class MutableCellVertex<ValueT>(
    initialValue: ValueT,
) : InherentCellVertex<ValueT>(
    initialStableValue = initialValue,
) {
    private var sourceUpdate: CellVertex.Update<ValueT> = CellVertex.NilUpdate

    override fun process(
        context: Transaction.ProcessingContext,
    ): CellVertex.Update<ValueT> = sourceUpdate

    fun set(
        context: Transaction.ProcessingContext,
        newValue: ValueT,
    ) {
        sourceUpdate = CellVertex.EffectiveUpdate(
            updatedValue = newValue,
        )

        ensureProcessedSubsequently(
            context = context,
        )
    }

    override fun reset(
        tag: BaseDynamicCellVertex.Tag,
    ) {
        sourceUpdate = CellVertex.NilUpdate
    }
}
