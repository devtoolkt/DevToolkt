package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.DependentVertex
import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex
import dev.toolkt.reactive.event_stream.vertices.InherentEventStreamVertex
import dev.toolkt.reactive.globalFinalizationRegistry

abstract class InherentDependentEventStreamVertex<EventT>() : InherentEventStreamVertex<EventT>(), DependentVertex {
    final override fun visit(
        context: Transaction.ProcessingContext,
    ) {
        ensureProcessedSubsequently(
            context = context,
        )
    }

    protected fun initialize(
        context: Transaction.ProcessingContext,
    ) {
        cacheOccurrence(
            context = context,
            occurrence = processAttaching(
                context = context,
            ),
        )

        // TODO: Figure out weak dependents!
        globalFinalizationRegistry.register(
            target = this,
        ) {
            //  We can't refer this vertex in the callback
        }
    }

    protected abstract fun processAttaching(
        context: Transaction.ProcessingContext,
    ): EventStreamVertex.Occurrence<EventT>
}
