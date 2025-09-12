package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.DependentVertex
import dev.toolkt.reactive.Transaction
import kotlin.jvm.JvmInline

abstract class BaseCellVertex<ValueT>() : DependentVertex(), CellVertex<ValueT> {
    @JvmInline
    value class Update<ValueT>(
        val newValue: ValueT,
    ) {
        fun <TransformedValueT> map(
            transform: (ValueT) -> TransformedValueT,
        ): Update<TransformedValueT> = Update(
            newValue = transform(newValue),
        )
    }

    final override fun invokeEffects(
        mutationContext: Transaction.MutationContext,
    ) {
        // Cell vertices do not have external side effects
    }

    final override fun stabilize(
        stabilizationContext: Transaction.StabilizationContext,
        tag: SpecializedVertexTag,
    ) {
        storedVolatileUpdate?.let { update ->
            persistNewValue(
                stabilizationContext = stabilizationContext,
                newValue = update.newValue,
            )
        }

        clear(
            stabilizationContext = stabilizationContext,
        )
    }

    /**
     * Store the new value as stable state (if this vertex maintains stable state)
     */
    abstract fun persistNewValue(
        stabilizationContext: Transaction.StabilizationContext,
        newValue: ValueT,
    )

    /**
     * Clear the stored specialized vertex-specific volatile state
     */
    abstract fun clear(
        stabilizationContext: Transaction.StabilizationContext,
    )
}
