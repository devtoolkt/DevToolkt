package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.PropagativeVertex
import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.DependencyEventStreamVertex

abstract class PropagativeEventStreamVertex<EventT>() : PropagativeVertex<EventStreamVertex.Occurrence<EventT>>(),
    DependencyEventStreamVertex<EventT> {

    final override fun pullOccurrence(
        preProcessingContext: Transaction.PreProcessingContext,
    ): EventStreamVertex.Occurrence<EventT>? = pullMessage(
        preProcessingContext = preProcessingContext,
    )
}
