package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex.Occurrence

class EventStreamTakeVertex<EventT> private constructor(
    private val sourceEventStreamVertex: EventStreamVertex<EventT>,
    totalCount: Int,
) : FiniteEventStreamVertex<EventT>() {
    companion object {
        fun <ValueT> construct(
            context: Transaction.ProcessingContext,
            sourceEventStreamVertex: EventStreamVertex<ValueT>,
            totalCount: Int,
        ): EventStreamTakeVertex<ValueT> = EventStreamTakeVertex(
            sourceEventStreamVertex = sourceEventStreamVertex,
            totalCount = totalCount,
        ).apply {
            initialize(
                context = context,
            )
        }
    }

    init {
        require(totalCount > 1) {
            "totalCount must be greater than 1, but was $totalCount"
        }
    }

    private var remainingCount = totalCount

    override fun processAttaching(
        context: Transaction.ProcessingContext,
    ): Occurrence<EventT> = sourceEventStreamVertex.pullOccurrenceSubscribing(
        context = context,
        dependentVertex = this,
    )

    override fun process(
        context: Transaction.ProcessingContext,
    ): Occurrence<EventT> {
        if (remainingCount <= 0) {
            return EventStreamVertex.NilOccurrence
        }

        return sourceEventStreamVertex.pullOccurrenceSubsequent(
            context = context,
        )
    }

    override fun transit() {
        remainingCount -= 1

        if (remainingCount == 0) {
            sourceEventStreamVertex.unsubscribe(
                dependentVertex = this,
            )
        }
    }
}
