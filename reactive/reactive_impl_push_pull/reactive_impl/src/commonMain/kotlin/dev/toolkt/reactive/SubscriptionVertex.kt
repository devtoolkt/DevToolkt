package dev.toolkt.reactive

import dev.toolkt.reactive.cell.vertices.DynamicEventStreamVertex
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex

class SubscriptionVertex<EventT>(
    private val sourceEventStreamVertex: DynamicEventStreamVertex<EventT>,
    private val handle: (EventT) -> Unit,
) : OperativeVertex() {
    private var receivedEventOccurrence: EventStreamVertex.Occurrence<EventT>? = null

    override fun processOperative(
        processingContext: Transaction.ProcessingContext,
    ) {
        val occurrence = sourceEventStreamVertex.pullOccurrence(
            processingContext = processingContext,
        ) ?: return

        receivedEventOccurrence = occurrence
    }

    override fun expand(
        expansionContext: Transaction.ExpansionContext,
    ) {
    }

    override fun shrink(
        shrinkageContext: Transaction.ShrinkageContext,
    ) {
    }

    override fun registerDependent(
        processingContext: Transaction.ProcessingContext,
        vertex: DynamicVertex,
    ) {
        TODO("Not yet implemented")
    }

    override fun unregisterDependent(
        processingContext: Transaction.ProcessingContext,
        vertex: DynamicVertex,
    ) {
        TODO("Not yet implemented")
    }

    /**
     * Adds [vertex] to the stable dependents. If this is the first stable dependent, activate this vertex.
     */
    override fun addDependent(
        expansionContext: Transaction.ExpansionContext,
        vertex: DynamicVertex,
    ) {
    }

    /**
     * Removes [vertex] from the stable dependents. If this was the last stable dependent, deactivate this vertex.
     */
    override fun removeDependent(
        shrinkageContext: Transaction.ShrinkageContext,
        vertex: DynamicVertex,
    ) {
    }

    override fun invokeEffects(
        mutationContext: Transaction.MutationContext,
    ) {
        receivedEventOccurrence?.let {
            handle(it.event)
        }
    }

    override fun stabilizeOperative(
        stabilizationContext: Transaction.StabilizationContext,
    ) {
        receivedEventOccurrence = null
    }
}
