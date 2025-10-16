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
class EventStream_merge2_emission_tests {
    @Test
    fun test_sameSource_sourceOccurrence() = testEventStream_initiallyEnergic(
        setup = {
            val sourceEventStream = createEnergicEventStreamExternally(
                emittedEventByTick = mapOf(
                    Tick(1) to 11,
                ),
                terminationTick = null,
            )

            EventStream.merge2(
                sourceEventStream,
                sourceEventStream,
            )
        },
        expectedNotificationByTick = mapOf(
            Tick(1) to EventStream.IntermediateEmissionNotification(
                emittedEvent = 11,
            ),
        ),
    )

    @Test
    fun test_bothSourcesExhausted() {
        fun test(
            sourceEventStreamFactory: ExhaustedEventStreamFactory,
        ) = testEventStream_immediatelyExhausted(
            setup = {
                val sourceEventStream1 = sourceEventStreamFactory.createExternally<Int>()
                val sourceEventStream2 = sourceEventStreamFactory.createExternally<Int>()

                EventStream.merge2(
                    sourceEventStream1,
                    sourceEventStream2,
                )
            },
        )

        ExhaustedEventStreamFactory.values.forEach { sourceEventStreamFactory ->
            test(
                sourceEventStreamFactory = sourceEventStreamFactory,
            )
        }
    }

    @Test
    fun test_bothSourcesEnergic_firstSourceEmits() = testEventStream_initiallyEnergic(
        setup = {
            val sourceEventStream1 = createEnergicEventStreamExternally(
                emittedEventByTick = mapOf(
                    Tick(1) to 11,
                ),
                terminationTick = null,
            )

            val sourceEventStream2 = createEnergicEventStreamExternally(
                emittedEventByTick = emptyMap(),
                terminationTick = null,
            )

            EventStream.merge2(
                sourceEventStream1,
                sourceEventStream2,
            )

        },
        expectedNotificationByTick = mapOf(
            Tick(1) to EventStream.IntermediateEmissionNotification(
                emittedEvent = 11,
            ),
        ),
    )

    @Test
    fun test_bothSourcesEnergic_secondSourceEmits() = testEventStream_initiallyEnergic(
        setup = {
            val sourceEventStream1 = createEnergicEventStreamExternally(
                emittedEventByTick = emptyMap(),
                terminationTick = null,
            )

            val sourceEventStream2 = createEnergicEventStreamExternally(
                emittedEventByTick = mapOf(
                    Tick(1) to 21,
                ),
                terminationTick = null,
            )

            EventStream.merge2(
                sourceEventStream1,
                sourceEventStream2,
            )
        },
        expectedNotificationByTick = mapOf(
            Tick(1) to EventStream.IntermediateEmissionNotification(
                emittedEvent = 21,
            ),
        ),
    )

    @Test
    fun test_bothSourcesEnergic_bothSourcesEmitSimultaneously() = testEventStream_initiallyEnergic(
        setup = {
            val sourceEventStream1 = createEnergicEventStreamExternally(
                emittedEventByTick = mapOf(
                    Tick(1) to 11,
                ),
                terminationTick = null,
            )

            val sourceEventStream2 = createEnergicEventStreamExternally(
                emittedEventByTick = mapOf(
                    Tick(1) to 21,
                ),
                terminationTick = null,
            )

            EventStream.merge2(
                sourceEventStream1,
                sourceEventStream2,
            )
        },
        expectedNotificationByTick = mapOf(
            Tick(1) to EventStream.IntermediateEmissionNotification(
                emittedEvent = 11,
            ),
        ),
    )

    @Test
    fun test_firstSourceExhaustedSecondSourceEnergic_secondSourceTerminates() {
        fun test(
            source1CellFactory: ExhaustedEventStreamFactory,
        ) = testEventStream_initiallyEnergic(
            setup = {
                val sourceEventStream1 = source1CellFactory.createExternally<Int>()

                val sourceEventStream2 = createEnergicEventStreamExternally(
                    emittedEventByTick = mapOf(
                        Tick(1) to 21,
                    ),
                    terminationTick = Tick(2),
                )

                EventStream.merge2(
                    sourceEventStream1,
                    sourceEventStream2,
                )
            },
            expectedNotificationByTick = mapOf(
                Tick(1) to EventStream.IntermediateEmissionNotification(
                    emittedEvent = 21,
                ),
                Tick(2) to EventStream.IsolatedTerminationNotification,
            ),
        )

        ExhaustedEventStreamFactory.values.forEach { source1CellFactory ->
            test(
                source1CellFactory = source1CellFactory,
            )
        }
    }

    @Test
    fun test_firstSourceEnergicSecondSourceExhausted_firstSourceTerminates() {
        fun test(
            source2CellFactory: ExhaustedEventStreamFactory,
        ) = testEventStream_initiallyEnergic(
            setup = {
                val sourceEventStream1 = createEnergicEventStreamExternally(
                    emittedEventByTick = mapOf(
                        Tick(1) to 11,
                        Tick(3) to 13,
                    ),
                    terminationTick = Tick(3),
                )

                val sourceEventStream2 = source2CellFactory.createExternally<Int>()

                EventStream.merge2(
                    sourceEventStream1,
                    sourceEventStream2,
                )
            },
            expectedNotificationByTick = mapOf(
                Tick(1) to EventStream.IntermediateEmissionNotification(
                    emittedEvent = 11,
                ),
                Tick(3) to EventStream.TerminalEmissionNotification(
                    emittedTerminalEvent = 13,
                ),
            ),
        )

        ExhaustedEventStreamFactory.values.forEach { source2CellFactory ->
            test(
                source2CellFactory = source2CellFactory,
            )
        }
    }

    @Test
    fun test_bothSourcesEnergic_firstSourceJustTerminatesLast() = testEventStream_initiallyEnergic(
        setup = {
            val sourceEventStream1 = createEnergicEventStreamExternally(
                emittedEventByTick = mapOf(
                    Tick(2) to 12,
                ),
                terminationTick = Tick(3),
            )

            val sourceEventStream2 = createEnergicEventStreamExternally(
                emittedEventByTick = mapOf(
                    Tick(1) to 21,
                ),
                terminationTick = Tick(2),
            )

            EventStream.merge2(
                sourceEventStream1,
                sourceEventStream2,
            )
        },
        expectedNotificationByTick = mapOf(
            Tick(1) to EventStream.IntermediateEmissionNotification(
                emittedEvent = 21,
            ),
            Tick(2) to EventStream.IntermediateEmissionNotification(
                emittedEvent = 12,
            ),
            Tick(3) to EventStream.IsolatedTerminationNotification,
        ),
    )

    @Test
    fun test_bothSourcesEnergic_firstSourceEmitsTerminatingLast() = testEventStream_initiallyEnergic(
        setup = {
            val sourceEventStream1 = createEnergicEventStreamExternally(
                emittedEventByTick = mapOf(
                    Tick(2) to 12,
                    Tick(3) to 13,
                ),
                terminationTick = Tick(3),
            )

            val sourceEventStream2 = createEnergicEventStreamExternally(
                emittedEventByTick = mapOf(
                    Tick(1) to 21,
                ),
                terminationTick = Tick(2),
            )

            EventStream.merge2(
                sourceEventStream1,
                sourceEventStream2,
            )
        },
        expectedNotificationByTick = mapOf(
            Tick(1) to EventStream.IntermediateEmissionNotification(
                emittedEvent = 21,
            ),
            Tick(2) to EventStream.IntermediateEmissionNotification(
                emittedEvent = 12,
            ),
            Tick(3) to EventStream.TerminalEmissionNotification(
                emittedTerminalEvent = 13,
            ),
        ),
    )

    @Test
    fun test_bothSourcesEnergic_secondSourceJustTerminatesLast() = testEventStream_initiallyEnergic(
        setup = {
            val sourceEventStream1 = createEnergicEventStreamExternally(
                emittedEventByTick = mapOf(
                    Tick(1) to 11,
                ),
                terminationTick = Tick(2),
            )

            val sourceEventStream2 = createEnergicEventStreamExternally(
                emittedEventByTick = mapOf(
                    Tick(2) to 22,
                    Tick(3) to 23,
                ),
                terminationTick = Tick(3),
            )

            EventStream.merge2(
                sourceEventStream1,
                sourceEventStream2,
            )
        },
        expectedNotificationByTick = mapOf(
            Tick(1) to EventStream.IntermediateEmissionNotification(
                emittedEvent = 11,
            ),
            Tick(2) to EventStream.IntermediateEmissionNotification(
                emittedEvent = 22,
            ),
            Tick(3) to EventStream.IsolatedTerminationNotification,
        ),
    )

    @Test
    fun test_bothSourcesEnergic_secondSourceEmitsTerminatingLast() = testEventStream_initiallyEnergic(
        setup = {
            val sourceEventStream1 = createEnergicEventStreamExternally(
                emittedEventByTick = mapOf(
                    Tick(1) to 11,
                ),
                terminationTick = Tick(2),
            )

            val sourceEventStream2 = createEnergicEventStreamExternally(
                emittedEventByTick = mapOf(
                    Tick(2) to 22,
                    Tick(3) to 23,
                ),
                terminationTick = Tick(3),
            )

            EventStream.merge2(
                sourceEventStream1,
                sourceEventStream2,
            )
        },
        expectedNotificationByTick = mapOf(
            Tick(1) to EventStream.IntermediateEmissionNotification(
                emittedEvent = 11,
            ),
            Tick(2) to EventStream.IntermediateEmissionNotification(
                emittedEvent = 21,
            ),
            Tick(3) to EventStream.TerminalEmissionNotification(
                emittedTerminalEvent = 23,
            ),
        ),
    )

    @Test
    fun test_bothSourcesEnergic_bothSourcesJustTerminateSimultaneously() = testEventStream_initiallyEnergic(
        setup = {
            val sourceEventStream1 = createEnergicEventStreamExternally(
                emittedEventByTick = mapOf(
                    Tick(1) to 11,
                ),
                terminationTick = Tick(3),
            )

            val sourceEventStream2 = createEnergicEventStreamExternally(
                emittedEventByTick = mapOf(
                    Tick(2) to 22,
                ),
                terminationTick = Tick(3),
            )

            EventStream.merge2(
                sourceEventStream1,
                sourceEventStream2,
            )
        },
        expectedNotificationByTick = mapOf(
            Tick(1) to EventStream.IntermediateEmissionNotification(
                emittedEvent = 11,
            ),
            Tick(2) to EventStream.IntermediateEmissionNotification(
                emittedEvent = 22,
            ),
            Tick(3) to EventStream.IsolatedTerminationNotification,
        ),
    )

    @Test
    fun test_bothSourcesEnergic_bothSourcesEmitTerminatingSimultaneously() = testEventStream_initiallyEnergic(
        setup = {
            val sourceEventStream1 = createEnergicEventStreamExternally(
                emittedEventByTick = mapOf(
                    Tick(1) to 11,
                    Tick(3) to 13,
                ),
                terminationTick = Tick(3),
            )

            val sourceEventStream2 = createEnergicEventStreamExternally(
                emittedEventByTick = mapOf(
                    Tick(2) to 22,
                    Tick(3) to 23,
                ),
                terminationTick = Tick(3),
            )

            EventStream.merge2(
                sourceEventStream1,
                sourceEventStream2,
            )
        },
        expectedNotificationByTick = mapOf(
            Tick(1) to EventStream.IntermediateEmissionNotification(
                emittedEvent = 11,
            ),
            Tick(2) to EventStream.IntermediateEmissionNotification(
                emittedEvent = 22,
            ),
            Tick(3) to EventStream.TerminalEmissionNotification(
                emittedTerminalEvent = 13,
            ),
        ),
    )

    @Test
    fun test_bothSourcesEnergic_firstSourceEmitsTerminatingSecondJustTerminatesSimultaneously() =
        testEventStream_initiallyEnergic(
            setup = {
                val sourceEventStream1 = createEnergicEventStreamExternally(
                    emittedEventByTick = mapOf(
                        Tick(1) to 11,
                        Tick(3) to 13,
                    ),
                    terminationTick = Tick(3),
                )

                val sourceEventStream2 = createEnergicEventStreamExternally(
                    emittedEventByTick = mapOf(
                        Tick(2) to 22,
                    ),
                    terminationTick = Tick(3),
                )

                EventStream.merge2(
                    sourceEventStream1,
                    sourceEventStream2,
                )
            },
            expectedNotificationByTick = mapOf(
                Tick(1) to EventStream.IntermediateEmissionNotification(
                    emittedEvent = 11,
                ),
                Tick(2) to EventStream.IntermediateEmissionNotification(
                    emittedEvent = 22,
                ),
                Tick(3) to EventStream.TerminalEmissionNotification(
                    emittedTerminalEvent = 13,
                ),
            ),
        )

    @Test
    fun test_bothSourcesEnergic_firstSourceJustTerminatesSecondEmitsTerminatingSimultaneously() =
        testEventStream_initiallyEnergic(
            setup = {
                val sourceEventStream1 = createEnergicEventStreamExternally(
                    emittedEventByTick = mapOf(
                        Tick(1) to 11,
                    ),
                    terminationTick = Tick(3),
                )

                val sourceEventStream2 = createEnergicEventStreamExternally(
                    emittedEventByTick = mapOf(
                        Tick(2) to 22,
                        Tick(3) to 23,
                    ),
                    terminationTick = Tick(3),
                )

                EventStream.merge2(
                    sourceEventStream1,
                    sourceEventStream2,
                )
            },
            expectedNotificationByTick = mapOf(
                Tick(1) to EventStream.IntermediateEmissionNotification(
                    emittedEvent = 11,
                ),
                Tick(2) to EventStream.IntermediateEmissionNotification(
                    emittedEvent = 22,
                ),
                Tick(3) to EventStream.TerminalEmissionNotification(
                    emittedTerminalEvent = 23,
                ),
            ),
        )
}
