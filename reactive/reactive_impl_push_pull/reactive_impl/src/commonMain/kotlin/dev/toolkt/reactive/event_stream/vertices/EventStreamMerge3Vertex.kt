package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex.NilOccurrence

class EventStreamMerge3Vertex<EventT> private constructor(
    private val sourceEventStream1Vertex: DynamicEventStreamVertex<EventT>,
    private val sourceEventStream2Vertex: DynamicEventStreamVertex<EventT>,
    private val sourceEventStream3Vertex: DynamicEventStreamVertex<EventT>,
) : SimpleDerivedEventStreamVertex<EventT>() {
    companion object {
        fun <EventT> construct0(
            uncheckedSourceEventStream1Vertex: EventStreamVertex<EventT>,
            uncheckedSourceEventStream2Vertex: EventStreamVertex<EventT>,
            uncheckedSourceEventStream3Vertex: EventStreamVertex<EventT>,
        ): EventStreamVertex<EventT> = when (val sourceEventStream1Vertex = uncheckedSourceEventStream1Vertex) {
            is SilentEventStreamVertex -> EventStreamMerge2Vertex.construct0(
                uncheckedSourceEventStream1Vertex = uncheckedSourceEventStream2Vertex,
                uncheckedSourceEventStream2Vertex = uncheckedSourceEventStream3Vertex,
            )

            is DynamicEventStreamVertex -> construct1(
                dynamicSourceEventStream1Vertex = sourceEventStream1Vertex,
                uncheckedSourceEventStream2Vertex = uncheckedSourceEventStream2Vertex,
                uncheckedSourceEventStream3Vertex = uncheckedSourceEventStream3Vertex,
            )
        }

        fun <EventT> construct1(
            dynamicSourceEventStream1Vertex: DynamicEventStreamVertex<EventT>,
            uncheckedSourceEventStream2Vertex: EventStreamVertex<EventT>,
            uncheckedSourceEventStream3Vertex: EventStreamVertex<EventT>,
        ): EventStreamVertex<EventT> = when (val sourceEventStream2Vertex = uncheckedSourceEventStream2Vertex) {
            is SilentEventStreamVertex -> EventStreamMerge2Vertex.construct1(
                dynamicSourceEventStream1Vertex = dynamicSourceEventStream1Vertex,
                uncheckedSourceEventStream2Vertex = uncheckedSourceEventStream3Vertex,
            )

            is DynamicEventStreamVertex -> construct2(
                dynamicSourceEventStream1Vertex = dynamicSourceEventStream1Vertex,
                dynamicSourceEventStream2Vertex = sourceEventStream2Vertex,
                uncheckedSourceEventStream3Vertex = uncheckedSourceEventStream3Vertex,
            )
        }

        fun <EventT> construct2(
            dynamicSourceEventStream1Vertex: DynamicEventStreamVertex<EventT>,
            dynamicSourceEventStream2Vertex: DynamicEventStreamVertex<EventT>,
            uncheckedSourceEventStream3Vertex: EventStreamVertex<EventT>,
        ): EventStreamVertex<EventT> = when (val sourceEventStream3Vertex = uncheckedSourceEventStream3Vertex) {
            is SilentEventStreamVertex -> EventStreamMerge2Vertex.construct2(
                dynamicSourceEventStream1Vertex = dynamicSourceEventStream1Vertex,
                dynamicSourceEventStream2Vertex = dynamicSourceEventStream2Vertex,
            )

            is DynamicEventStreamVertex -> construct3(
                dynamicSourceEventStream1Vertex = dynamicSourceEventStream1Vertex,
                dynamicSourceEventStream2Vertex = dynamicSourceEventStream2Vertex,
                dynamicSourceEventStream3Vertex = sourceEventStream3Vertex,
            )
        }

        fun <EventT> construct3(
            dynamicSourceEventStream1Vertex: DynamicEventStreamVertex<EventT>,
            dynamicSourceEventStream2Vertex: DynamicEventStreamVertex<EventT>,
            dynamicSourceEventStream3Vertex: DynamicEventStreamVertex<EventT>,
        ): EventStreamVertex<EventT> = EventStreamMerge3Vertex(
            sourceEventStream1Vertex = dynamicSourceEventStream1Vertex,
            sourceEventStream2Vertex = dynamicSourceEventStream2Vertex,
            sourceEventStream3Vertex = dynamicSourceEventStream3Vertex,
        )
    }

    override fun process(
        context: Transaction.ProcessingContext,
        processingMode: ProcessingMode,
    ): EventStreamVertex.Occurrence<EventT> {
        val sourceOccurrence1 = sourceEventStream1Vertex.pullOccurrence(
            context = context,
            processingMode = processingMode,
        )

        if (sourceOccurrence1 != NilOccurrence) {
            return sourceOccurrence1
        }

        val sourceOccurrence2 = sourceEventStream2Vertex.pullOccurrence(
            context = context,
            processingMode = processingMode,
        )

        if (sourceOccurrence2 != NilOccurrence) {
            return sourceOccurrence2
        }

        val sourceOccurrence3 = sourceEventStream3Vertex.pullOccurrence(
            context = context,
            processingMode = processingMode,
        )

        if (sourceOccurrence3 != NilOccurrence) {
            return sourceOccurrence3
        }

        return NilOccurrence
    }

    override fun resume() {
        sourceEventStream1Vertex.subscribe(
            dependentVertex = this,
        )

        sourceEventStream2Vertex.subscribe(
            dependentVertex = this,
        )

        sourceEventStream3Vertex.subscribe(
            dependentVertex = this,
        )
    }

    override fun pause() {
        sourceEventStream1Vertex.unsubscribe(
            dependentVertex = this,
        )

        sourceEventStream2Vertex.unsubscribe(
            dependentVertex = this,
        )

        sourceEventStream3Vertex.unsubscribe(
            dependentVertex = this,
        )
    }
}
