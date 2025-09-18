package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.DependentVertex
import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.CellVertex.Update

abstract class SimpleDerivedCellVertex<ValueT> : DerivedCellVertex<ValueT>() {
    sealed interface ProcessingMode {
        fun <ValueT> pullUpdate(
            context: Transaction.ProcessingContext,
            sourceVertex: DependencyCellVertex<ValueT>,
            dependentVertex: DependentVertex,
        ): Update<ValueT>

        data object Activating : ProcessingMode {
            override fun <ValueT> pullUpdate(
                context: Transaction.ProcessingContext,
                sourceVertex: DependencyCellVertex<ValueT>,
                dependentVertex: DependentVertex,
            ): Update<ValueT> = sourceVertex.pullUpdateObserving(
                context = context,
                dependentVertex = dependentVertex,
            )
        }

        data object Following : ProcessingMode {
            override fun <ValueT> pullUpdate(
                context: Transaction.ProcessingContext,
                sourceVertex: DependencyCellVertex<ValueT>,
                dependentVertex: DependentVertex,
            ): Update<ValueT> = sourceVertex.pullUpdateSubsequent(
                context = context,
            )
        }
    }

    final override fun processActivating(
        context: Transaction.ProcessingContext,
    ): Update<ValueT> = process(
        context = context,
        processingMode = ProcessingMode.Activating,
    )

    final override fun processFollowing(
        context: Transaction.ProcessingContext,
    ): Update<ValueT> = process(
        context = context,
        processingMode = ProcessingMode.Following,
    )

    protected fun <ValueT> DependencyCellVertex<ValueT>.pullUpdate(
        context: Transaction.ProcessingContext,
        processingMode: ProcessingMode,
    ): Update<ValueT> = processingMode.pullUpdate(
        context = context,
        sourceVertex = this@pullUpdate,
        dependentVertex = this@SimpleDerivedCellVertex,
    )

    protected abstract fun process(
        context: Transaction.ProcessingContext,
        processingMode: ProcessingMode,
    ): Update<ValueT>
}
