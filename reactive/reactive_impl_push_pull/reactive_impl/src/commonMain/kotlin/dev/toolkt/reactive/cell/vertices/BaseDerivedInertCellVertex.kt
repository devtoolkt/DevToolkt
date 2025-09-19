package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.BaseVertex
import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.CellVertex.RetrievalMode

abstract class BaseDerivedInertCellVertex<ValueT> : BaseInertCellVertex<ValueT>() {
    private var cachedInertValue: CellVertex.StableValue<ValueT>? = null

    final override fun fetchOldValue(): ValueT = computeInertValue(
        retrievalMode = RetrievalMode.Fetch,
    )

    final override fun reset(
        tag: BaseVertex.Tag,
    ) {
        cachedInertValue = null
    }

    final override fun sampleOldValue(
        context: Transaction.ProcessingContext,
    ): ValueT = sampleValueCaching(
        context = context,
        getCachedValue = this::cachedInertValue,
        setCachedValue = this::cachedInertValue::set,
        computeValue = ::computeInertValue,
    )

    protected abstract fun computeInertValue(
        retrievalMode: RetrievalMode,
    ): ValueT
}
