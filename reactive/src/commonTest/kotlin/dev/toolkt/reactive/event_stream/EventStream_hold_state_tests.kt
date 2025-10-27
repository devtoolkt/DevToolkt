package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.test_utils.ExhaustedEventStreamFactory
import dev.toolkt.reactive.cell.test_utils.Tick
import dev.toolkt.reactive.cell.test_utils.testCell_immediatelyInert
import dev.toolkt.reactive.cell.test_utils.testCell_initiallyDynamic
import dev.toolkt.reactive.event_stream.test_utils.createEnergicEventStreamExternally
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore // FIXME
@Suppress("ClassName")
class EventStream_hold_state_tests {
    private data class Configuration<SourceValueT>(
        val sourceEventStream: EventStream<SourceValueT>,
    )

    @Test
    fun test_sourceExhausted() {
        fun test(
            sourceEventStreamFactory: ExhaustedEventStreamFactory,
        ) = testCell_immediatelyInert(
            spawnSubject = {
                val sourceEventStream = sourceEventStreamFactory.createExternally<Int>()

                MomentContext.execute {
                    sourceEventStream.hold(initialValue = 10)
                }
            },
            expectedValue = 10,
        )

        ExhaustedEventStreamFactory.values.forEach { sourceEventStreamFactory ->
            test(
                sourceEventStreamFactory = sourceEventStreamFactory,
            )
        }
    }

    @Test
    fun test_sourceEnergic() = testCell_initiallyDynamic(
        configure = {
            val sourceEventStream = createEnergicEventStreamExternally(
                emittedEventByTick = emptyMap(),
                terminationTick = null,
            )

            Configuration(
                sourceEventStream = sourceEventStream,
            )
        },
        spawnCell = {
            sourceEventStream.hold(
                initialValue = 10,
            )
        },
        expectedInitialValue = 10,
        expectedNotificationByTick = emptyMap(),
    )

    @Test
    fun test_sourceEnergic_sourceEmits() {
        testCell_initiallyDynamic(
            configure = {
                val sourceEventStream = createEnergicEventStreamExternally(
                    emittedEventByTick = mapOf(
                        Tick(1) to 11,
                    ),
                    terminationTick = null,
                )

                Configuration(
                    sourceEventStream = sourceEventStream,
                )
            },
            spawnCell = {
                sourceEventStream.hold(initialValue = 10)
            },
            expectedInitialValue = 10,
            expectedNotificationByTick = mapOf(
                Tick(1) to Cell.IntermediateUpdateNotification(
                    updatedValue = 11,
                ),
            ),
        )
    }

    @Test
    fun test_sourceEnergic_sourceEmitsSimultaneously() = testCell_initiallyDynamic(
        configure = {
            val sourceEventStream = createEnergicEventStreamExternally(
                emittedEventByTick = mapOf(
                    Tick(0) to 11,
                ),
                terminationTick = null,
            )

            Configuration(
                sourceEventStream = sourceEventStream,
            )
        },
        spawnCell = {
            sourceEventStream.hold(initialValue = 10)
        },
        expectedInitialValue = 10,
        expectedNotificationByTick = emptyMap(),
    )

    @Test
    fun test_sourceEnergic_sourceJustTerminates() = testCell_initiallyDynamic(
        configure = {
            val sourceEventStream = createEnergicEventStreamExternally(
                emittedEventByTick = emptyMap(),
                terminationTick = null,
            )

            Configuration(
                sourceEventStream = sourceEventStream,
            )
        },
        spawnCell = {
            sourceEventStream.hold(initialValue = 10)
        },
        expectedInitialValue = 10,
        expectedNotificationByTick = mapOf(
            Tick(1) to Cell.LateFreezeNotification,
        ),
    )

    @Test
    fun test_sourceEnergic_sourceEmitsTerminating() = testCell_initiallyDynamic(
        configure = {
            val sourceEventStream = createEnergicEventStreamExternally(
                emittedEventByTick = mapOf(
                    Tick(1) to 11,
                ),
                terminationTick = null,
            )

            Configuration(
                sourceEventStream = sourceEventStream,
            )
        },
        spawnCell = {
            sourceEventStream.hold(initialValue = 10)
        },
        expectedInitialValue = 10,
        expectedNotificationByTick = mapOf(
            Tick(1) to Cell.FreezingUpdateNotification(
                updatedFrozenValue = 11,
            ),
        ),
    )
}
