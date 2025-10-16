package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.cell.test_utils.ExhaustedEventStreamFactory
import dev.toolkt.reactive.cell.test_utils.Tick
import dev.toolkt.reactive.event_stream.test_utils.createEnergicEventStreamExternally
import dev.toolkt.reactive.event_stream.test_utils.testEventStream_immediatelyExhausted
import dev.toolkt.reactive.event_stream.test_utils.testEventStream_initiallyEnergic
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore // FIXME
@Suppress("ClassName")
class EventStream_map_emission_tests {
    @Test
    fun test_sourceExhausted() {
        fun test(
            sourceEventStreamFactory: ExhaustedEventStreamFactory,
        ) = testEventStream_immediatelyExhausted(
            setup = {
                val sourceEventStream = sourceEventStreamFactory.createExternally<Int>()

                sourceEventStream.map { it.toString() }
            },
        )

        ExhaustedEventStreamFactory.values.forEach { sourceEventStreamFactory ->
            test(
                sourceEventStreamFactory = sourceEventStreamFactory,
            )
        }
    }

    @Test
    fun test_sourceEnergic() = testEventStream_initiallyEnergic(
        setup = {
            val sourceEventStream = createEnergicEventStreamExternally(
                emittedEventByTick = emptyMap(),
                terminationTick = null,
            )

            sourceEventStream.map { it.toString() }
        },
        expectedNotificationByTick = emptyMap(),
    )

    @Test
    fun test_sourceEnergic_sourceEmits() = testEventStream_initiallyEnergic(
        setup = {
            val sourceEventStream = createEnergicEventStreamExternally(
                emittedEventByTick = mapOf(
                    Tick(1) to 11,
                ),
                terminationTick = null,
            )

            sourceEventStream.map { it.toString() }
        },
        expectedNotificationByTick = mapOf(
            Tick(1) to EventStream.IntermediateEmissionNotification(
                emittedEvent = "11",
            ),
        ),
    )

    @Test
    fun test_sourceEnergic_sourceJustTerminates() = testEventStream_initiallyEnergic(
        setup = {
            val sourceEventStream = createEnergicEventStreamExternally(
                emittedEventByTick = emptyMap(),
                terminationTick = Tick(1),
            )

            sourceEventStream.map { it.toString() }
        },
        expectedNotificationByTick = mapOf(
            Tick(1) to EventStream.IsolatedTerminationNotification,
        ),
    )

    @Test
    fun test_sourceEnergic_sourceEmitsTerminating() = testEventStream_initiallyEnergic(
        setup = {
            val sourceEventStream = createEnergicEventStreamExternally(
                emittedEventByTick = mapOf(
                    Tick(1) to 11,
                ),
                terminationTick = Tick(1),
            )

            sourceEventStream.map { it.toString() }
        },
        expectedNotificationByTick = mapOf(
            Tick(1) to EventStream.TerminalEmissionNotification(
                emittedTerminalEvent = 11,
            ),
        ),
    )
}
