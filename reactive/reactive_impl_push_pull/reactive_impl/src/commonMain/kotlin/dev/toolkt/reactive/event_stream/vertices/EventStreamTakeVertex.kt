package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.DependencyEventStreamVertex
import dev.toolkt.reactive.globalFinalizationRegistry
import dev.toolkt.reactive.registerDependent

class EventStreamTakeVertex<EventT> private constructor(
    private val sourceEventStreamVertex: DependencyEventStreamVertex<EventT>,
    totalCount: Int,
) : StatefulIntermediateEventStreamVertex<EventT>() {
    companion object {
        fun <ValueT> construct(
            processingContext: Transaction.ProcessingContext,
            sourceEventStreamVertex: DependencyEventStreamVertex<ValueT>,
            totalCount: Int,
        ): EventStreamTakeVertex<ValueT> = EventStreamTakeVertex(
            sourceEventStreamVertex = sourceEventStreamVertex,
            totalCount = totalCount,
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

    private var remainingCount = totalCount

    override fun process(
        processingContext: Transaction.ProcessingContext,
    ): EventStreamVertex.Occurrence<EventT>? {
        if (remainingCount <= 0) {
            return null
        }

        val sourceOccurrence = sourceEventStreamVertex.pullOccurrence(
            processingContext = processingContext,
        )

        return sourceOccurrence
    }

    override fun update(
        currentNotification: EventStreamVertex.Occurrence<EventT>,
    ) {
        remainingCount -= 1

        if (remainingCount <= 0) {
            sourceEventStreamVertex.removeDependent(
                dependentVertex = this,
            )
        }
    }
}
