package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.DependencyEventStreamVertex
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex.Occurrence

class EventStreamSingleVertex<EventT> private constructor(
    private val sourceEventStreamVertex: DependencyEventStreamVertex<EventT>,
) : FiniteEventStreamVertex<EventT>() {
    companion object {
        fun <ValueT> construct(
            context: Transaction.ProcessingContext,
            sourceEventStreamVertex: DependencyEventStreamVertex<ValueT>,
        ): EventStreamSingleVertex<ValueT> = EventStreamSingleVertex(
            sourceEventStreamVertex = sourceEventStreamVertex,
        ).apply {
            initialize(
                context = context,
            )
        }
    }

    override fun processAttaching(
        context: Transaction.ProcessingContext,
    ): Occurrence<EventT> = sourceEventStreamVertex.pullOccurrenceSubscribing(
        context = context,
        dependentVertex = this,
    )

    override fun process(
        context: Transaction.ProcessingContext,
    ): Occurrence<EventT> = sourceEventStreamVertex.pullOccurrenceSubsequent(
        context = context,
    )

    override fun transit() {
        sourceEventStreamVertex.unsubscribe(
            dependentVertex = this,
        )
    }
}
