package dev.toolkt.reactive.event_stream.hold_tests

import dev.toolkt.reactive.cell.test_utils.Tick
import dev.toolkt.reactive.cell.test_utils.testStatefulCell_observedInstantly_verifyFreezesLater
import dev.toolkt.reactive.cell.test_utils.testStatefulCell_observedLater_verifyInitialInert
import dev.toolkt.reactive.cell.test_utils.testStatefulCell_verifyFreezesInstantly
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.hold
import dev.toolkt.reactive.event_stream.test_utils.createEnergicEventStreamExternally
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore // TODO: Port to test scenarios
@Suppress("ClassName")
class EventStream_hold_freeze_tests {
    private data class Input<ValueT>(
        val sourceEventStream: EventStream<ValueT>,
    )

    @Test
    fun test_postSpawn() {
        testStatefulCell_observedInstantly_verifyFreezesLater(
            configure = {
                val sourceEventStream = createEnergicEventStreamExternally(
                    emittedEventByTick = emptyMap(),
                    terminationTick = Tick(1),
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
            expectedFreezeTick = Tick(1),
        )
    }

    @Test
    fun test_postSpawn_observedLater() {
        testStatefulCell_observedLater_verifyInitialInert(
            configure = {
                val sourceEventStream = createEnergicEventStreamExternally(
                    emittedEventByTick = emptyMap(),
                    terminationTick = Tick(1),
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
            observationTick = Tick(2),
            expectedObservedValue = 10,
        )
    }

    /**
     * The scenario happens at the moment the result cell is spawned.
     */
    @Test
    fun test_atSpawn() {
        testStatefulCell_verifyFreezesInstantly(
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
        )
    }
}
