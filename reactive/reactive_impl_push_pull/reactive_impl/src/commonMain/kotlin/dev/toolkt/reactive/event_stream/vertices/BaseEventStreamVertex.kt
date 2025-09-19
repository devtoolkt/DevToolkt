package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.BaseDynamicVertex
import dev.toolkt.reactive.BaseVertex
import dev.toolkt.reactive.DependentVertex
import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.DependencyEventStreamVertex

abstract class BaseEventStreamVertex<EventT> : BaseDynamicVertex(), DependencyEventStreamVertex<EventT> {
    data object Tag

    private var cachedOccurrence: EventStreamVertex.Occurrence<EventT>? = null

    final override fun pullOccurrenceSubscribing(
        context: Transaction.ProcessingContext,
        dependentVertex: DependentVertex,
    ): EventStreamVertex.Occurrence<EventT> {
        val wasFirst = addDependent(
            dependentVertex = dependentVertex,
        )

        return ensureProcessed(
            context = context,
        ) { context ->
            processSubscribed(
                context = context,
                wasFirst = wasFirst,
            )
        }
    }

    final override fun pullOccurrenceSubsequent(
        context: Transaction.ProcessingContext,
    ): EventStreamVertex.Occurrence<EventT> = ensureProcessed(
        context = context,
    ) { context ->
        processSubsequent(
            context = context,
        )
    }

    final override fun commit() {
        if (cachedOccurrence is EventStreamVertex.EffectiveOccurrence) {
            transit()
        }
    }

    final override fun subscribe(
        dependentVertex: DependentVertex,
    ) {
        val wasFirst = addDependent(
            dependentVertex = dependentVertex,
        )

        if (wasFirst) {
            onFirstSubscriberAdded()
        }
    }

    final override fun unsubscribe(
        dependentVertex: DependentVertex,
    ) {
        val wasLast = removeDependent(
            dependentVertex = dependentVertex,
        )

        if (wasLast) {
            onLastSubscriberRemoved()
        }
    }

    final override fun reset(
        tag: BaseVertex.Tag,
    ) {
        cachedOccurrence = null

        reset(tag = Tag)
    }

    private inline fun ensureProcessed(
        context: Transaction.ProcessingContext,
        processSpecifically: (context: Transaction.ProcessingContext) -> EventStreamVertex.Occurrence<EventT>,
    ): EventStreamVertex.Occurrence<EventT> {
        val foundCachedOccurrence = cachedOccurrence

        if (foundCachedOccurrence != null) {
            return foundCachedOccurrence
        }

        val occurrence = processSpecifically(context)

        cacheOccurrence(
            context = context,
            occurrence = occurrence,
        )

        if (occurrence !is EventStreamVertex.NilOccurrence) {
            enqueueDependentsForVisiting(
                context = context,
            )
        }

        return occurrence
    }

    protected fun cacheOccurrence(
        context: Transaction.ProcessingContext,
        occurrence: EventStreamVertex.Occurrence<EventT>,
    ) {
        cachedOccurrence = occurrence

        ensureMarkedDirty(
            context = context,
        )
    }

    protected fun ensureProcessedSubsequently(
        context: Transaction.ProcessingContext,
    ) {
        ensureProcessed(
            context = context,
            processSpecifically = ::processSubsequent,
        )
    }

    /**
     * Process the vertex in response to being subscribed.
     *
     * @param context The transaction context.
     * @param wasFirst Whether this is the first subscriber.
     */
    protected abstract fun processSubscribed(
        context: Transaction.ProcessingContext,
        wasFirst: Boolean,
    ): EventStreamVertex.Occurrence<EventT>

    /**
     * Process the vertex in response to being pulled by a dependent or being visited in consequence of a push from
     * a dependency.
     *
     * @param context The transaction context.
     */
    protected abstract fun processSubsequent(
        context: Transaction.ProcessingContext,
    ): EventStreamVertex.Occurrence<EventT>

    protected abstract fun reset(
        tag: Tag,
    )

    protected abstract fun onFirstSubscriberAdded()

    protected abstract fun onLastSubscriberRemoved()

    protected abstract fun transit()
}
