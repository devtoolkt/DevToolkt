package dev.toolkt.reactive

import dev.toolkt.reactive.cell.vertices.CellVertex
import dev.toolkt.reactive.cell.vertices.CellVertex.RetrievalMode

abstract class BaseVertex : Vertex {
    data object Tag

    private var isMarkedDirty = false

    protected fun ensureMarkedDirty(
        context: Transaction.ProcessingContext,
    ) {
        if (isMarkedDirty) {
            return
        }

        context.markDirty(
            dirtyVertex = this,
        )

        isMarkedDirty = true
    }

    final override fun reset() {
        isMarkedDirty = false

        reset(
            tag = Tag,
        )
    }

    protected abstract fun reset(
        tag: Tag,
    )

    /**
     * Samples the old value, caching it for future calls within the same transaction.
     *
     * Suitable for [CellVertex] subclasses.
     *
     * ```kotlin
     *     private var cachedOldValue: CellVertex.StableValue<ValueT>? = null
     *
     *     final override fun sampleOldValue(
     *         context: Transaction.ProcessingContext,
     *     ): ValueT = sampleOldValueCaching(
     *         context = context,
     *         getCachedOldValue = this::cachedOldValue,
     *         setCachedOldValue = this::cachedOldValue::set,
     *         computeOldValue = ::computeOldValue,
     *     )
     * ```
     */
    protected inline fun <ValueT> sampleValueCaching(
        context: Transaction.ProcessingContext,
        getCachedValue: () -> CellVertex.StableValue<ValueT>?,
        setCachedValue: (CellVertex.StableValue<ValueT>) -> Unit,
        computeValue: (retrievalMode: RetrievalMode) -> ValueT,
    ): ValueT {
        when (val cachedOldValue = getCachedValue()) {
            null -> {
                val computedOldValue = computeValue(
                    RetrievalMode.Sample(
                        context = context,
                    ),
                )

                setCachedValue(
                    CellVertex.StableValue(
                        value = computedOldValue,
                    )
                )

                ensureMarkedDirty(
                    context = context,
                )

                return computedOldValue
            }

            else -> {
                return cachedOldValue.value
            }
        }
    }
}
