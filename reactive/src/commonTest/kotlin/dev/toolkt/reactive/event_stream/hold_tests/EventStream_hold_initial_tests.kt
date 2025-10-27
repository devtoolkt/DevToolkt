package dev.toolkt.reactive.event_stream.hold_tests

import dev.toolkt.reactive.cell.test_utils.Tick
import dev.toolkt.reactive.cell.test_utils.testStatefulCell_observedInstantly_verifyInitial
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.NeverEventStream
import dev.toolkt.reactive.event_stream.hold
import dev.toolkt.reactive.event_stream.test_utils.createEnergicEventStreamExternally
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore // FIXME
@Suppress("ClassName")
class EventStream_hold_initial_tests {
    private data class Input<ValueT>(
        val sourceEventStream: EventStream<ValueT>,
    )

    @Test
    fun test_sourceNever() {
        testStatefulCell_observedInstantly_verifyInitial(
            configure = {
                Input(
                    sourceEventStream = NeverEventStream,
                )
            },
            spawnTick = Tick(0),
            spawn = {
                sourceEventStream.hold(
                    initialValue = 10,
                )
            },
            expectedInitialObservedValue = 10,
        )
    }

    @Test
    fun test_sourceExhausted() {
        testStatefulCell_observedInstantly_verifyInitial(
            configure = {
                val sourceEventStream = createEnergicEventStreamExternally(
                    emittedEventByTick = emptyMap(),
                    terminationTick = Tick(0),
                )

                Input(
                    sourceEventStream = sourceEventStream,
                )
            },
            spawnTick = Tick(1),
            spawn = {
                sourceEventStream.hold(
                    initialValue = 10,
                )
            },
            expectedInitialObservedValue = 10,
        )
    }

    @Test
    fun test_sourceTerminatesSimultaneously() {
        testStatefulCell_observedInstantly_verifyInitial(
            configure = {
                val sourceEventStream = createEnergicEventStreamExternally(
                    emittedEventByTick = emptyMap(),
                    terminationTick = Tick(0),
                )

                Input(
                    sourceEventStream = sourceEventStream,
                )
            },
            spawnTick = Tick(0),
            spawn = {
                sourceEventStream.hold(
                    initialValue = 10,
                )
            },
            expectedInitialObservedValue = 10,
        )
    }

    @Test
    fun test_sourceEnergic() {
        testStatefulCell_observedInstantly_verifyInitial(
            configure = {
                val sourceEventStream = createEnergicEventStreamExternally(
                    emittedEventByTick = emptyMap(),
                    terminationTick = null,
                )

                Input(
                    sourceEventStream = sourceEventStream,
                )
            },
            spawnTick = Tick(1),
            spawn = {
                sourceEventStream.hold(
                    initialValue = 10,
                )
            },
            expectedInitialObservedValue = 10,
        )
    }

    @Test
    fun test_sourceEnergic_sourceEmitsSimultaneously() {
        testStatefulCell_observedInstantly_verifyInitial(
            configure = {
                val sourceEventStream = createEnergicEventStreamExternally(
                    emittedEventByTick = mapOf(
                        Tick(0) to 11,
                    ),
                    terminationTick = null,
                )

                Input(
                    sourceEventStream = sourceEventStream,
                )
            },
            spawnTick = Tick(0),
            spawn = {
                sourceEventStream.hold(initialValue = 10)
            },
            expectedInitialObservedValue = 10,
        )
    }
}
