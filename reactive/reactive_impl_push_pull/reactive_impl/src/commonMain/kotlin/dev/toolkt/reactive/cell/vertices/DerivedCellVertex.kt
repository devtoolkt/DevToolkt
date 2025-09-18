package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.DependentVertex
import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.Vertex
import dev.toolkt.reactive.cell.vertices.CellVertex.RetrievalMode

abstract class DerivedCellVertex<ValueT> : BaseCellVertex<ValueT>(), DependencyCellVertex<ValueT>, DependentVertex,
    Vertex {
    private var cachedOldValue: CellVertex.StableValue<ValueT>? = null

    override fun fetchOldValue(): ValueT {
        return computeOldValue(
            retrievalMode = RetrievalMode.Fetch,
        )
    }

    final override fun sampleOldValue(
        context: Transaction.ProcessingContext,
    ): ValueT {
        when (val cachedOldValue = this.cachedOldValue) {
            null -> {
                val computedOldValue = computeOldValue(
                    retrievalMode = RetrievalMode.Sample(
                        context = context,
                    ),
                )

                this.cachedOldValue = CellVertex.StableValue(
                    value = computedOldValue,
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

    final override fun visit(
        context: Transaction.ProcessingContext,
    ) {
        ensureProcessedSubsequently(
            context = context,
        )
    }

    final override fun processObserved(
        context: Transaction.ProcessingContext,
        wasFirst: Boolean,
    ): CellVertex.Update<ValueT> = when {
        wasFirst -> processActivating(
            context = context,
        )

        else -> processFollowing(
            context = context,
        )
    }

    final override fun processSubsequent(
        context: Transaction.ProcessingContext,
    ): CellVertex.Update<ValueT> = processFollowing(
        context = context,
    )

    override fun onFirstObserverAdded() {
        activate()
    }

    final override fun onLastObserverRemoved() {
        deactivate()
    }

    final override fun persist(updatedValue: ValueT) {
    }

    final override fun reset(
        tag: BaseCellVertex.Tag,
    ) {
        cachedOldValue = null
    }

    protected abstract fun computeOldValue(
        retrievalMode: RetrievalMode,
    ): ValueT

    /**
     * Process this vertex and activate it at the same time.
     *
     * This method is called only if the vertex is inactive.
     */
    protected abstract fun processActivating(
        context: Transaction.ProcessingContext,
    ): CellVertex.Update<ValueT>

    /**
     * Process this vertex.
     *
     * This method is called only if the vertex is active.
     */
    protected abstract fun processFollowing(
        context: Transaction.ProcessingContext,
    ): CellVertex.Update<ValueT>

    /**
     * Activate this vertex.
     */
    protected abstract fun activate()

    /**
     * Deactivate this vertex.
     */
    protected abstract fun deactivate()
}
