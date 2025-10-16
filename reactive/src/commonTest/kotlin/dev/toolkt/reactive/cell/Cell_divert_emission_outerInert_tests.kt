package dev.toolkt.reactive.cell

import dev.toolkt.core.utils.iterable.mapOfNotNull
import dev.toolkt.reactive.cell.test_utils.ExhaustedEventStreamFactory
import dev.toolkt.reactive.cell.test_utils.InertCellFactory
import dev.toolkt.reactive.cell.test_utils.Tick
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.test_utils.createEnergicEventStreamExternally
import dev.toolkt.reactive.event_stream.test_utils.testEventStream_immediatelyExhausted
import dev.toolkt.reactive.event_stream.test_utils.testEventStream_initiallyEnergic
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore // FIXME
@Suppress("ClassName")
class Cell_divert_emission_outerInert_tests {
    /**
     * Inner event stream: immediately inert
     */
    @Test
    fun test_state_innerExhausted() {
        fun test(
            outerCellFactory: InertCellFactory,
            innerEventStreamFactory: ExhaustedEventStreamFactory,
        ) = testEventStream_immediatelyExhausted(
            setup = {
                val innerEventStream = innerEventStreamFactory.createExternally<Int>()

                val outerCell = outerCellFactory.createInertExternally(
                    inertValue = innerEventStream,
                )

                Cell.divert(outerCell)
            },
        )

        InertCellFactory.values.forEach { outerCellFactory ->
            ExhaustedEventStreamFactory.values.forEach { innerEventStreamFactory ->
                test(
                    outerCellFactory = outerCellFactory,
                    innerEventStreamFactory = innerEventStreamFactory,
                )
            }
        }
    }

    /**
     * Inner event stream: initially energic
     */
    @Test
    fun test_state_innerEnergic() {
        fun test(
            outerCellFactory: InertCellFactory,
        ) {
            testEventStream_initiallyEnergic(
                setup = {
                    val innerEventStream = createEnergicEventStreamExternally(
                        emittedEventByTick = emptyMap(),
                        terminationTick = null,
                    )

                    val outerCell = outerCellFactory.createInertExternally(
                        inertValue = innerEventStream,
                    )

                    Cell.divert(outerCell)
                },
                expectedNotificationByTick = emptyMap(),
            )
        }

        InertCellFactory.values.forEach { outerCellFactory ->
            test(
                outerCellFactory = outerCellFactory,
            )
        }
    }

    /**
     * Inner event stream: initially energic
     *
     * - The inner event stream emits
     */
    @Test
    fun test_state_innerEnergic_innerUpdates() {
        fun test(
            outerCellFactory: InertCellFactory,
            shouldInnerTerminateSimultaneously: Boolean,
        ) {
            testEventStream_initiallyEnergic(
                setup = {
                    val innerEventStream = createEnergicEventStreamExternally(
                        emittedEventByTick = mapOf(
                            Tick(1) to 11,
                        ),
                        terminationTick = Tick(1).takeIf { shouldInnerTerminateSimultaneously },
                    )

                    val outerCell = outerCellFactory.createInertExternally(
                        inertValue = innerEventStream,
                    )

                    Cell.divert(outerCell)
                },
                expectedNotificationByTick = mapOf(
                    Tick(1) to EventStream.EmissionNotification.of(
                        emittedEvent = 11,
                        isTerminal = shouldInnerTerminateSimultaneously,
                    ),
                ),
            )
        }

        InertCellFactory.values.forEach { outerCellFactory ->
            test(
                outerCellFactory = outerCellFactory,
                shouldInnerTerminateSimultaneously = false,
            )

            test(
                outerCellFactory = outerCellFactory,
                shouldInnerTerminateSimultaneously = true,
            )
        }
    }

    /**
     * Inner event stream: initially energic
     *
     * - The inner event stream terminates
     */
    @Test
    fun test_state_innerEnergic_innerTerminates() {
        fun test(
            outerCellFactory: InertCellFactory,
            shouldInnerUpdateSimultaneously: Boolean,
        ) {
            val finalEmittedEvent = when {
                shouldInnerUpdateSimultaneously -> 11
                else -> null
            }

            testEventStream_initiallyEnergic(
                setup = {
                    val innerEventStream = createEnergicEventStreamExternally(
                        emittedEventByTick = mapOfNotNull(
                            finalEmittedEvent?.let {
                                Tick(1) to it
                            },
                        ),
                        terminationTick = Tick(1),
                    )

                    val outerCell = outerCellFactory.createInertExternally(
                        inertValue = innerEventStream,
                    )

                    Cell.divert(outerCell)
                },
                expectedNotificationByTick = mapOf(
                    Tick(1) to EventStream.TerminationNotification.of(
                        emittedEvent = finalEmittedEvent,
                    ),
                ),
            )
        }

        InertCellFactory.values.forEach { outerCellFactory ->
            test(
                outerCellFactory = outerCellFactory,
                shouldInnerUpdateSimultaneously = false,
            )

            test(
                outerCellFactory = outerCellFactory,
                shouldInnerUpdateSimultaneously = true,
            )
        }
    }
}
