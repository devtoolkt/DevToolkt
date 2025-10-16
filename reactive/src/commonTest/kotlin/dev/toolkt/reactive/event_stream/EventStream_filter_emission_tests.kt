package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.cell.test_utils.ExhaustedEventStreamFactory
import dev.toolkt.reactive.cell.test_utils.Tick
import dev.toolkt.reactive.event_stream.test_utils.EventStreamVerificationStrategy
import dev.toolkt.reactive.event_stream.test_utils.createEnergicEventStreamExternally
import dev.toolkt.reactive.event_stream.test_utils.testEventStream_immediatelyExhausted
import dev.toolkt.reactive.event_stream.test_utils.testEventStream_initiallyEnergic
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore // FIXME
@Suppress("ClassName")
class EventStream_filter_emission_tests {
    @Test
    fun test_sourceExhausted() {
        fun test(
            sourceEventStreamFactory: ExhaustedEventStreamFactory,
        ) = testEventStream_immediatelyExhausted(
            setup = {
                val sourceEventStream = sourceEventStreamFactory.createExternally<Int>()

                sourceEventStream.filter { false }
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

            sourceEventStream.filter { false }
        },
        expectedNotificationByTick = emptyMap(),
    )

    // FIXME: Ensure enough ticks are simulates
    @Test
    fun test_sourceEnergic_sourceEmits_eventRejected() = testEventStream_initiallyEnergic(
        setup = {
            val sourceEventStream = createEnergicEventStreamExternally(
                emittedEventByTick = mapOf(
                    Tick(1) to 11,
                ),
                terminationTick = null,
            )

            sourceEventStream.filter { false }
        },
        expectedNotificationByTick = emptyMap(),
    )


    // FIXME: Ensure enough ticks are simulates
    @Test
    fun test_sourceEnergic_sourceEmits_eventAccepted() = testEventStream_initiallyEnergic(
        setup = {
            val sourceEventStream = createEnergicEventStreamExternally(
                emittedEventByTick = mapOf(
                    Tick(1) to 11,
                ),
                terminationTick = null,
            )

            sourceEventStream.filter { true }
        },
        expectedNotificationByTick = mapOf(
            Tick(1) to EventStream.IntermediateEmissionNotification(
                emittedEvent = 11,
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

            sourceEventStream.filter { false }
        },
        expectedNotificationByTick = mapOf(
            Tick(1) to EventStream.IsolatedTerminationNotification,
        ),
    )

    @Test
    fun test_sourceEnergic_sourceEmitsTerminating_eventRejected() = testEventStream_initiallyEnergic(
        setup = {
            val sourceEventStream = createEnergicEventStreamExternally(
                emittedEventByTick = mapOf(
                    Tick(1) to 11,
                ),
                terminationTick = Tick(1),
            )


            sourceEventStream.filter { false }
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

            sourceEventStream.filter { true }
        },
        expectedNotificationByTick = mapOf(
            Tick(1) to EventStream.TerminalEmissionNotification(
                emittedTerminalEvent = 11,
            ),
        ),
    )
}
