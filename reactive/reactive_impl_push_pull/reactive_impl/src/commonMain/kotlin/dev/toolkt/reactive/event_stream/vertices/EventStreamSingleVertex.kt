package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.DependencyEventStreamVertex
import dev.toolkt.reactive.cell.vertices.HoldCellVertex
import dev.toolkt.reactive.globalFinalizationRegistry

class EventStreamSingleVertex<EventT> private constructor(
    private val sourceEventStreamVertex: DependencyEventStreamVertex<EventT>,
) : StatefulEventStreamVertex<EventT>() {
    companion object {
        fun <ValueT> construct(
            preProcessingContext: Transaction.PreProcessingContext,
            sourceEventStreamVertex: DependencyEventStreamVertex<ValueT>,
        ): EventStreamSingleVertex<ValueT> = EventStreamSingleVertex(
            sourceEventStreamVertex = sourceEventStreamVertex,
        ).apply {
            sourceEventStreamVertex.registerDependent(
                preProcessingContext = preProcessingContext,
                vertex = this,
            )

            ensureVisited(
                preProcessingContext = preProcessingContext,
            )

            // TODO: Figure out weak dependents!
            globalFinalizationRegistry.register(
                target = this,
            ) {
            }
        }
    }

    private var wasPropagated = false

    override fun prepare(
        preProcessingContext: Transaction.PreProcessingContext,
    ): EventStreamVertex.Occurrence<EventT>? {
        if (wasPropagated) {
            return null
        }

        val sourceOccurrence = sourceEventStreamVertex.pullOccurrence(
            preProcessingContext = preProcessingContext,
        )

        return sourceOccurrence
    }

    override fun stabilize(
        postProcessingContext: Transaction.PostProcessingContext,
        message: EventStreamVertex.Occurrence<EventT>?,
    ) {
        if (message != null) {
            sourceEventStreamVertex.removeDependent(
                shrinkageContext = postProcessingContext,
                vertex = this,
            )

            wasPropagated = true
        }
    }
}
