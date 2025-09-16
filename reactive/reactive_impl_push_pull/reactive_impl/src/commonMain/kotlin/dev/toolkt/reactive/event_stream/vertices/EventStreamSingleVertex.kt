package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.DependencyEventStreamVertex
import dev.toolkt.reactive.globalFinalizationRegistry
import dev.toolkt.reactive.registerDependent

class EventStreamSingleVertex<EventT> private constructor(
    private val sourceEventStreamVertex: DependencyEventStreamVertex<EventT>,
) : StatefulIntermediateEventStreamVertex<EventT>() {
    companion object {
        fun <ValueT> construct(
            processingContext: Transaction.ProcessingContext,
            sourceEventStreamVertex: DependencyEventStreamVertex<ValueT>,
        ): EventStreamSingleVertex<ValueT> = EventStreamSingleVertex(
            sourceEventStreamVertex = sourceEventStreamVertex,
        ).apply {
            sourceEventStreamVertex.registerDependent(
                processingContext = processingContext,
                dependentVertex = this,
            )

            ensureProcessed(
                processingContext = processingContext,
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
        processingContext: Transaction.ProcessingContext,
    ): EventStreamVertex.EmittedEvent<EventT>? {
        if (wasPropagated) {
            return null
        }

        val sourceOccurrence = sourceEventStreamVertex.pullEmittedEvent(
            processingContext = processingContext,
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
