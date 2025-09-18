package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.DependentVertex
import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.event_stream.vertices.DerivedEventStreamVertex
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex.Occurrence

abstract class SimpleDerivedEventStreamVertex<ValueT> : DerivedEventStreamVertex<ValueT>() {
    sealed interface ProcessingMode {
        fun <ValueT> pullOccurrence(
            context: Transaction.ProcessingContext,
            sourceVertex: DependencyEventStreamVertex<ValueT>,
            dependentVertex: DependentVertex,
        ): Occurrence<ValueT>

        data object Resuming : ProcessingMode {
            override fun <ValueT> pullOccurrence(
                context: Transaction.ProcessingContext,
                sourceVertex: DependencyEventStreamVertex<ValueT>,
                dependentVertex: DependentVertex,
            ): Occurrence<ValueT> = sourceVertex.pullOccurrenceSubscribing(
                context = context,
                dependentVertex = dependentVertex,
            )
        }

        data object Following : ProcessingMode {
            override fun <ValueT> pullOccurrence(
                context: Transaction.ProcessingContext,
                sourceVertex: DependencyEventStreamVertex<ValueT>,
                dependentVertex: DependentVertex,
            ): Occurrence<ValueT> = sourceVertex.pullOccurrenceSubsequent(
                context = context,
            )
        }
    }

    final override fun processResuming(
        context: Transaction.ProcessingContext,
    ): Occurrence<ValueT> = process(
        context = context,
        processingMode = ProcessingMode.Resuming,
    )

    final override fun processFollowing(
        context: Transaction.ProcessingContext,
    ): Occurrence<ValueT> = process(
        context = context,
        processingMode = ProcessingMode.Following,
    )

    protected fun <ValueT> DependencyEventStreamVertex<ValueT>.pullOccurrence(
        context: Transaction.ProcessingContext,
        processingMode: ProcessingMode,
    ): Occurrence<ValueT> = processingMode.pullOccurrence(
        context = context,
        sourceVertex = this@pullOccurrence,
        dependentVertex = this@SimpleDerivedEventStreamVertex,
    )

    protected abstract fun process(
        context: Transaction.ProcessingContext,
        processingMode: ProcessingMode,
    ): Occurrence<ValueT>
}
