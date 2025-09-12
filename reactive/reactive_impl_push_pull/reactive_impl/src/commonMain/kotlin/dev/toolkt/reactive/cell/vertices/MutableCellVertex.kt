package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction

class MutableCellVertex<ValueT>(
    initialValue: ValueT,
) : StatefulCellVertex<ValueT>() {
    private var mutableValue: ValueT = initialValue

    private var preparedVolatileUpdate: Update<ValueT>? = null

    override fun fetchVolatileUpdate(
        processingContext: Transaction.ProcessingContext,
    ): Update<ValueT>? = preparedVolatileUpdate

    override val storedVolatileUpdate: Update<ValueT>?
        get() = preparedVolatileUpdate

    fun prepareNewValue(
        newValue: ValueT,
    ) {
        if (preparedVolatileUpdate != null) {
            throw IllegalStateException("There is already a pending prepared update $preparedVolatileUpdate")
        }

        preparedVolatileUpdate = Update(
            newValue = newValue,
        )
    }

    override fun persistNewValue(
        stabilizationContext: Transaction.StabilizationContext,
        newValue: ValueT,
    ) {
        mutableValue = newValue
    }

    override fun process(
        processingContext: Transaction.ProcessingContext,
    ) {
        // Mutable cell vertices don't need extra processing
    }

    override fun activate(
        expansionContext: Transaction.ExpansionContext,
    ) {
        // The mutable cell vertex doesn't have dependencies
    }

    override fun deactivate(
        shrinkageContext: Transaction.ShrinkageContext,
    ) {
        // The mutable cell vertex doesn't have dependencies
    }

    override fun clear(
        stabilizationContext: Transaction.StabilizationContext,
    ) {
        preparedVolatileUpdate = null
    }
}
