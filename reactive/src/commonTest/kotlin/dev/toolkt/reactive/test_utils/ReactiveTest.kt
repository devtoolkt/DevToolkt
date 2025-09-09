package dev.toolkt.reactive.test_utils

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.event_stream.EventStream

class ReactiveTest<StimulationEventT : Any> {
    class Input<StimulationEventT : Any> {
        fun <EventT : Any> extractEventStream(
            selector: (StimulationEventT) -> EventT?,
        ): EventStream<EventT> {
            TODO()
        }
    }

    companion object {
        fun <StimulationEventT : Any, SystemT : Any> setup(
            block: context(MomentContext) Input<StimulationEventT>.() -> SystemT,
        ): Pair<SystemT, ReactiveTest<StimulationEventT>> {
            TODO()
        }

        fun <EventT : Any, SystemT : Any> setupWithSingleInputEventStream(
            block: context(MomentContext) (EventStream<EventT>) -> SystemT,
        ): Pair<SystemT, ReactiveTest<EventT>> {
            TODO()
        }
    }

    fun stimulate(
        event: StimulationEventT,
    ) {
        TODO()
    }
}
