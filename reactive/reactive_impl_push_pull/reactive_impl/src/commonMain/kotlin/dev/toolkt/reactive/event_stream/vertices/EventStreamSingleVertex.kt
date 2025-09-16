package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.DependencyEventStreamVertex
import dev.toolkt.reactive.globalFinalizationRegistry
import dev.toolkt.reactive.registerDependent

class EventStreamSingleVertex<EventT> private constructor(
    private val sourceEventStreamVertex: DependencyEventStreamVertex<EventT>,
) : FiniteEventStreamVertex<EventT>() {
    companion object {
        fun <ValueT> construct(
            context: Transaction.Context,
            sourceEventStreamVertex: DependencyEventStreamVertex<ValueT>,
        ): EventStreamSingleVertex<ValueT> = EventStreamSingleVertex(
            sourceEventStreamVertex = sourceEventStreamVertex,
        ).apply {
            sourceEventStreamVertex.registerDependent(
                context = context,
                dependentVertex = this,
            )

            ensureProcessed(
                context = context,
            )

            // TODO: Figure out weak dependents!
            globalFinalizationRegistry.register(
                target = this,
            ) {
            }
        }
    }

    private var wasPropagated = false

    override fun process(
        context: Transaction.Context,
    ): EventStreamVertex.EmittedEvent<EventT>? {
        if (wasPropagated) {
            return null
        }

        val sourceOccurrence = sourceEventStreamVertex.pullEmittedEvent(
            context = context,
        )

        return sourceOccurrence
    }

    override fun transit() {
        sourceEventStreamVertex.removeDependent(
            dependentVertex = this,
        )

        wasPropagated = true
    }
}
