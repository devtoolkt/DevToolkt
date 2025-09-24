package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.vertices.CellVertex.EffectiveUpdate
import dev.toolkt.reactive.cell.vertices.CellVertex.NilUpdate
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.vertices.BaseDerivedEventStreamVertex
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex

class DivertEventStreamVertex<ValueT>(
    private val outerEventStreamVertex: CellVertex<EventStream<ValueT>>,
) : BaseDerivedEventStreamVertex<ValueT>() {
    private var innerEventStreamVertex: EventStreamVertex<ValueT>? = null

    override fun processResuming(
        context: Transaction.ProcessingContext,
    ): EventStreamVertex.Occurrence<ValueT> {
        val oldInnerEventStream = outerEventStreamVertex.sampleOldValue(
            context = context,
        )

        val oldInnerEventStreamVertex = oldInnerEventStream.vertex

        val outerOccurrence = outerEventStreamVertex.pullUpdateObserving(
            context = context,
            dependentVertex = this,
        )

        val newInnerEventStream = when (outerOccurrence) {
            NilUpdate -> oldInnerEventStream

            is EffectiveUpdate -> outerOccurrence.updatedValue
        }

        val newInnerEventStreamVertex = newInnerEventStream.vertex

        innerEventStreamVertex = newInnerEventStreamVertex

        newInnerEventStreamVertex.subscribe(
            dependentVertex = this,
        )

        // FIXME: Not subsequent! (this path is not tested)
        return oldInnerEventStreamVertex.pullOccurrenceSubsequent(
            context = context,
        )
    }

    override fun processFollowing(
        context: Transaction.ProcessingContext,
    ): EventStreamVertex.Occurrence<ValueT> {
        val oldInnerEventStreamVertex =
            this.innerEventStreamVertex ?: throw IllegalStateException("Inner cell vertex is null in following phase")

        val outerUpdate = outerEventStreamVertex.pullUpdateSubsequent(
            context = context,
        )

        when (outerUpdate) {
            NilUpdate -> {}

            is EffectiveUpdate -> {
                val updatedInnerEventStream = outerUpdate.updatedValue
                val updatedInnerEventStreamVertex = updatedInnerEventStream.vertex

                this.innerEventStreamVertex = updatedInnerEventStreamVertex

                if (oldInnerEventStreamVertex != updatedInnerEventStreamVertex) {
                    updatedInnerEventStreamVertex.subscribe(dependentVertex = this)
                    oldInnerEventStreamVertex.unsubscribe(dependentVertex = this)
                }
            }
        }

        return oldInnerEventStreamVertex.pullOccurrenceSubsequent(
            context = context,
        )
    }

    override fun resume() {
        outerEventStreamVertex.observe(
            dependentVertex = this,
        )

        val innerEventStream = outerEventStreamVertex.fetchOldValue()
        val innerEventStreamVertex = innerEventStream.vertex

        this.innerEventStreamVertex = innerEventStreamVertex

        innerEventStreamVertex.subscribe(
            dependentVertex = this,
        )
    }

    override fun pause() {
        outerEventStreamVertex.unobserve(
            dependentVertex = this,
        )

        innerEventStreamVertex?.unsubscribe(
            dependentVertex = this,
        )

        innerEventStreamVertex = null
    }
}
