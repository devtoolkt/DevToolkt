package dev.toolkt.reactive.test_utils

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.hold
import dev.toolkt.reactive.event_stream.mapNotNull

abstract class ReactiveTest<StimulationT : Any> {
    abstract class Input<StimulationEventT : Any> {
        abstract fun <EventT : Any> extractEventStream(
            selector: (StimulationEventT) -> EventT?,
        ): EventStream<EventT>

        context(momentContext: MomentContext) fun <ValueT : Any> extractCell(
            initialValue: ValueT,
            selector: (StimulationEventT) -> ValueT?,
        ): Cell<ValueT> = extractEventStream(
            selector = selector,
        ).hold(
            initialValue = initialValue,
        )

        fun formEventStream(): EventStream<StimulationEventT> = extractEventStream { it }

        context(momentContext: MomentContext) fun formCell(
            initialValue: StimulationEventT,
        ): Cell<StimulationEventT> = extractCell(
            initialValue = initialValue,
            selector = { it },
        )
    }

    companion object {
        fun <StimulationEventT : Any, SystemT : Any> setup(
            block: context(MomentContext) Input<StimulationEventT>.() -> SystemT,
        ): Pair<SystemT, ReactiveTest<StimulationEventT>> {
            val emitter = EmitterEventStream<StimulationEventT>()

            val system = MomentContext.execute {
                block(
                    object : Input<StimulationEventT>() {
                        override fun <EventT : Any> extractEventStream(
                            selector: (StimulationEventT) -> EventT?,
                        ): EventStream<EventT> = emitter.mapNotNull {
                            selector(it)
                        }
                    },
                )
            }

            val reactiveTest = object : ReactiveTest<StimulationEventT>() {
                override fun stimulate(event: StimulationEventT) {
                    emitter.emit(event)
                }
            }

            return Pair(system, reactiveTest)
        }
    }

    abstract fun stimulate(
        event: StimulationT,
    )
}
