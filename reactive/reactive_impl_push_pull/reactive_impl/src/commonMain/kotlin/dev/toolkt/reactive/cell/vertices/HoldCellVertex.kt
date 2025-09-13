package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.CellVertex.Update

class HoldCellVertex<ValueT>(
    initialValue: ValueT,
) : StatefulCellVertex<ValueT>() {
    private var heldStableValue: ValueT = initialValue

    override fun pullUpdate(
        processingContext: Transaction.ProcessingContext,
    ): Update<ValueT>? = TODO()

    override val storedVolatileUpdate: Update<ValueT>?
        get() = TODO()

    override fun prepare(
        processingContext: Transaction.ProcessingContext,
    ): Boolean {

        TODO()
    }

    override fun persistNewValue(
        stabilizationContext: Transaction.StabilizationContext,
        newValue: ValueT,
    ) {
        heldStableValue = newValue
    }

    override fun activate(
        expansionContext: Transaction.ExpansionContext,
    ) {
        TODO()
    }

    override fun deactivate(
        shrinkageContext: Transaction.ShrinkageContext,
    ) {
        TODO()
    }

    // Thought: Move `clear` to PropagativeCellVertex ?
    override fun clear(
        stabilizationContext: Transaction.StabilizationContext,
    ) {
    }
}
