package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.DependencyEventStreamVertex
import dev.toolkt.reactive.globalFinalizationRegistry

class EventStreamTakeVertex<EventT> private constructor(
    private val sourceEventStreamVertex: DependencyEventStreamVertex<EventT>,
    totalCount: Int,
) : StatefulEventStreamVertex<EventT>() {
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
                vertex = this,
            )

            ensureVisited(
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

    override fun prepare(
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

    override fun stabilize(
        latePostProcessingContext: Transaction.LatePostProcessingContext,
        message: EventStreamVertex.Occurrence<EventT>?,
    ) {
        if (message != null) {
            remainingCount -= 1

            if (remainingCount <= 0) {
                sourceEventStreamVertex.removeDependent(
                    shrinkageContext = latePostProcessingContext,
                    vertex = this,
                )
            }
        }
    }
}
