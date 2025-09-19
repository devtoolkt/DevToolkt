package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.DependentVertex
import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.Vertex
import dev.toolkt.reactive.cell.vertices.CellVertex
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex.EffectiveOccurrence
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex.NilOccurrence
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex.Occurrence
import kotlin.jvm.JvmInline

// Thought: Merge with `DependentEventStreamVertex`?
interface EventStreamVertex<EventT> : Vertex {
    sealed interface Occurrence<out EventT>

    data object NilOccurrence : Occurrence<Nothing>

    @JvmInline
    value class EffectiveOccurrence<out EventT>(
        val event: EventT,
    ) : Occurrence<EventT> {
        fun <TransformedEventT> map(
            transform: (EventT) -> TransformedEventT,
        ): EffectiveOccurrence<TransformedEventT> = EffectiveOccurrence(
            event = transform(event),
        )

        fun <TransformedEventT : Any> mapNotNull(
            transform: (EventT) -> TransformedEventT?,
        ): EffectiveOccurrence<TransformedEventT>? {
            val transformedEvent = transform(event) ?: return null

            return EffectiveOccurrence(
                event = transformedEvent,
            )
        }
    }

    /**
     * Subscribe the [dependentVertex] to this vertex and pull an occurrence.
     *
     * If [dependentVertex] is the first dependent and this vertex supports resuming/pausing, this vertex will be
     * resumed.
     */
    fun pullOccurrenceSubscribing(
        context: Transaction.ProcessingContext,
        dependentVertex: DependentVertex,
    ): Occurrence<EventT>

    /**
     * Pull an occurrence from this vertex.
     *
     * It's required that this vertex has at least one dependent (if it supports resuming/pausing, it's required
     * that this vertex is resumed).
     */
    fun pullOccurrenceSubsequent(
        context: Transaction.ProcessingContext,
    ): Occurrence<EventT>

    fun subscribe(
        dependentVertex: DependentVertex,
    )

    /**
     * Unsubscribe the [dependentVertex] from this vertex.
     *
     * If [dependentVertex] was the last dependent and this vertex supports resuming/pausing, this vertex will be
     * deactivated.
     */
    fun unsubscribe(
        dependentVertex: DependentVertex,
    )
}

fun <EventT> Occurrence<EventT>.toUpdate(): CellVertex.Update<EventT> = when (this) {
    is NilOccurrence -> CellVertex.NilUpdate

    is EffectiveOccurrence -> CellVertex.EffectiveUpdate(
        updatedValue = event,
    )
}

fun <EventT, TransformedEventT> Occurrence<EventT>.map(
    transform: (EventT) -> TransformedEventT,
): Occurrence<TransformedEventT> = when (this) {
    is NilOccurrence -> NilOccurrence

    is EffectiveOccurrence -> this.map(transform)
}

fun <EventT, TransformedEventT : Any> Occurrence<EventT>.mapNotNull(
    transform: (EventT) -> TransformedEventT?,
): Occurrence<TransformedEventT> = when (this) {
    is NilOccurrence -> NilOccurrence

    is EffectiveOccurrence -> this.mapNotNull(transform) ?: NilOccurrence
}
