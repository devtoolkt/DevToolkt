package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.BaseDynamicVertex
import dev.toolkt.reactive.BaseVertex
import dev.toolkt.reactive.DependentVertex
import dev.toolkt.reactive.Transaction

abstract class BaseDynamicCellVertex<ValueT> : BaseDynamicVertex(), DynamicCellVertex<ValueT> {
    data object Tag

    private var cachedUpdate: CellVertex.Update<ValueT>? = null

    final override fun pullUpdateObserving(
        context: Transaction.ProcessingContext,
        dependentVertex: DependentVertex,
    ): CellVertex.Update<ValueT> {
        val wasFirst = addDependent(
            dependentVertex = dependentVertex,
        )

        return ensureProcessed(
            context = context,
        ) { context ->
            processObserved(
                context = context,
                wasFirst = wasFirst,
            )
        }
    }

    final override fun pullUpdateSubsequent(
        context: Transaction.ProcessingContext,
    ): CellVertex.Update<ValueT> = ensureProcessed(
        context = context,
    ) { context ->
        processTriggered(
            context = context,
        )
    }

    final override fun commit() {
        when (val cachedUpdate = this.cachedUpdate) {
            is CellVertex.EffectiveUpdate -> persist(
                updatedValue = cachedUpdate.updatedValue,
            )

            else -> {
            }
        }
    }

    final override fun observe(
        dependentVertex: DependentVertex,
    ) {
        val wasFirst = addDependent(
            dependentVertex = dependentVertex,
        )

        if (wasFirst) {
            onFirstObserverAdded()
        }
    }

    final override fun unobserve(
        dependentVertex: DependentVertex,
    ) {
        val wasLast = removeDependent(
            dependentVertex = dependentVertex,
        )

        if (wasLast) {
            onLastObserverRemoved()
        }
    }

    protected abstract fun persist(
        updatedValue: ValueT,
    )

    final override fun reset(
        tag: BaseVertex.Tag,
    ) {
        cachedUpdate = null

        reset(tag = Tag)
    }

    private inline fun ensureProcessed(
        context: Transaction.ProcessingContext,
        processSpecifically: (context: Transaction.ProcessingContext) -> CellVertex.Update<ValueT>,
    ): CellVertex.Update<ValueT> {
        val foundCachedUpdate = cachedUpdate

        if (foundCachedUpdate != null) {
            return foundCachedUpdate
        }

        val update = processSpecifically(context)

        cacheUpdate(
            context = context,
            update = update,
        )

        if (update !is CellVertex.NilUpdate) {
            enqueueDependentsForVisiting(
                context = context,
            )
        }

        return update
    }

    protected fun cacheUpdate(
        context: Transaction.ProcessingContext,
        update: CellVertex.Update<ValueT>,
    ) {
        cachedUpdate = update

        ensureMarkedDirty(
            context = context,
        )
    }

    protected fun ensureProcessedTriggered(
        context: Transaction.ProcessingContext,
    ) {
        ensureProcessed(
            context = context,
            processSpecifically = ::processTriggered,
        )
    }

    /**
     * Process the vertex in response to being observed.
     *
     * @param context The transaction context.
     * @param wasFirst Whether this is the first observer.
     */
    protected abstract fun processObserved(
        context: Transaction.ProcessingContext,
        wasFirst: Boolean,
    ): CellVertex.Update<ValueT>

    /**
     * Process the vertex in response to being pulled by a dependent or being visited in consequence of a push from
     * a dependency.
     *
     * @param context The transaction context.
     */
    protected abstract fun processTriggered(
        context: Transaction.ProcessingContext,
    ): CellVertex.Update<ValueT>

    protected abstract fun reset(
        tag: Tag,
    )

    protected abstract fun onFirstObserverAdded()

    protected abstract fun onLastObserverRemoved()
}
