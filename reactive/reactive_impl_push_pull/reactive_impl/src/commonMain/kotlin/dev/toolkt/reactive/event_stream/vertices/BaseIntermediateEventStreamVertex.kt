package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.IntermediateDynamicVertex
import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.DependencyEventStreamVertex

abstract class BaseIntermediateEventStreamVertex<EventT>() :
    IntermediateDynamicVertex<EventStreamVertex.Occurrence<EventT>>(), DependencyEventStreamVertex<EventT> {

    final override fun pullOccurrence(
        processingContext: Transaction.ProcessingContext,
    ): EventStreamVertex.Occurrence<EventT>? = pullNotification(
        processingContext = processingContext,
    )
}
