package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.PropagativeVertex
import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.DependencyEventStreamVertex

abstract class PropagativeEventStreamVertex<EventT>() : PropagativeVertex<EventStreamVertex.Occurrence<EventT>>(),
    DependencyEventStreamVertex<EventT> {

    final override fun pullOccurrence(
        processingContext: Transaction.ProcessingContext,
    ): EventStreamVertex.Occurrence<EventT>? = pullMessage(
        processingContext = processingContext,
    )
}
