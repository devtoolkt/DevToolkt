package dev.toolkt.reactive.test_utils

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.event_stream.Cell
import dev.toolkt.reactive.event_stream.EventStream

class ReactiveTest<StimulationT : Any> {
    class Input<StimulationEventT : Any> {
        fun <EventT : Any> extractEventStream(
            selector: (StimulationEventT) -> EventT?,
        ): EventStream<EventT> {
            TODO()
        }

        fun formEventStream(): EventStream<StimulationEventT> {
            TODO()
        }

        fun formCell(
            initialValue: StimulationEventT,
        ): Cell<StimulationEventT> {
            TODO()
        }

        fun <ValueT : Any> extractCell(
            initialValue: ValueT,
            selector: (StimulationEventT) -> ValueT?,
        ): Cell<ValueT> {
            TODO()
        }
    }

    companion object {
        fun <StimulationEventT : Any, SystemT : Any> setup(
            block: context(MomentContext) Input<StimulationEventT>.() -> SystemT,
        ): Pair<SystemT, ReactiveTest<StimulationEventT>> {
            TODO()
        }
    }

    fun stimulate(
        event: StimulationT,
    ) {
        TODO()
    }
}
