package dev.toolkt.reactive.cell

import dev.toolkt.core.utils.iterable.mapOfNotNull
import dev.toolkt.reactive.cell.test_utils.ExhaustedEventStreamFactory
import dev.toolkt.reactive.cell.test_utils.Tick
import dev.toolkt.reactive.cell.test_utils.createDynamicCellExternally
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.test_utils.createEnergicEventStreamExternally
import dev.toolkt.reactive.event_stream.test_utils.testEventStream_initiallyEnergic
import kotlin.test.Ignore
import kotlin.test.Test

/**
 * Outer cell: initially dynamic
 */
@Ignore // FIXME
@Suppress("ClassName")
class Cell_divert_emission_outerDynamic_tests {
    /**
     * Initial inner event stream: immediately exhausted
     */
    @Test
    fun test_initialInnerExhausted() {
        fun test(
            innerEventStreamFactory: ExhaustedEventStreamFactory,
        ) {
            testEventStream_initiallyEnergic(
                spawn = {
                    val innerCell = innerEventStreamFactory.createExternally<Int>()

                    val outerCell = createDynamicCellExternally(
                        initialValue = innerCell,
                        updatedValueByTick = emptyMap(),
                        freezeTick = null,
                    )

                    Cell.divert(outerCell)
                },
                expectedNotificationByTick = emptyMap(),
            )
        }

        ExhaustedEventStreamFactory.values.forEach { innerEventStreamFactory ->
            test(
                innerEventStreamFactory = innerEventStreamFactory,
            )
        }
    }

    /**
     * Initial inner event stream: initially energic
     */
    @Test
    fun test_initialInnerEnergic() = testEventStream_initiallyEnergic(
        spawn = {
            val innerCell = createEnergicEventStreamExternally(
                emittedEventByTick = emptyMap(),
                terminationTick = null,
            )

            val outerCell = createDynamicCellExternally(
                initialValue = innerCell,
                updatedValueByTick = emptyMap(),
                freezeTick = null,
            )

            Cell.divert(outerCell)
        },
        expectedNotificationByTick = emptyMap(),
    )

    /**
     * Initial inner event stream: initially energic
     *
     * - The initial inner event stream emits
     */
    @Test
    fun test_initialInnerEnergic_initialInnerEmits() {
        fun test(
            shouldInitialInnerTerminateSimultaneously: Boolean,
        ) = testEventStream_initiallyEnergic(
            spawn = {
                val innerCell = createEnergicEventStreamExternally(
                    emittedEventByTick = mapOf(
                        Tick(1) to 11,
                    ),
                    terminationTick = if (shouldInitialInnerTerminateSimultaneously) Tick(1) else null,
                )

                val outerCell = createDynamicCellExternally(
                    initialValue = innerCell,
                    updatedValueByTick = emptyMap(),
                    freezeTick = null,
                )

                Cell.divert(outerCell)
            },
            expectedNotificationByTick = mapOf(
                Tick(1) to EventStream.IntermediateEmissionNotification(
                    emittedEvent = 11,
                ),
            ),
        )

        test(
            shouldInitialInnerTerminateSimultaneously = false,
        )

        test(
            shouldInitialInnerTerminateSimultaneously = true,
        )
    }

    @Test
    fun test_outerUpdates_newInnerExhausted() {
        fun test(
            newInnerEventStreamFactory: ExhaustedEventStreamFactory,
            shouldOuterFreezeSimultaneously: Boolean,
            shouldOldInnerEmitSimultaneously: Boolean,
        ) = testEventStream_initiallyEnergic(
            spawn = {
                val initialInnerEventStream = createEnergicEventStreamExternally(
                    emittedEventByTick = mapOfNotNull(
                        (Tick(1) to 11).takeIf { shouldOldInnerEmitSimultaneously },
                    ),
                    terminationTick = null,
                )

                val newInnerEventStream = newInnerEventStreamFactory.createExternally<Int>()

                val outerCell = createDynamicCellExternally(
                    initialValue = initialInnerEventStream,
                    updatedValueByTick = mapOf(
                        Tick(1) to newInnerEventStream,
                    ),
                    freezeTick = if (shouldOuterFreezeSimultaneously) Tick(1) else null,
                )

                Cell.divert(outerCell)
            },
            expectedNotificationByTick = emptyMap(),
        )

        ExhaustedEventStreamFactory.values.forEach { newInnerEventStreamFactory ->
            test(
                newInnerEventStreamFactory = newInnerEventStreamFactory, // 👈
                shouldOuterFreezeSimultaneously = true,
                shouldOldInnerEmitSimultaneously = false,
            )
        }

        test(
            newInnerEventStreamFactory = ExhaustedEventStreamFactory.Never,
            shouldOuterFreezeSimultaneously = false,
            shouldOldInnerEmitSimultaneously = true, // 👈
        )
    }

    /**
     * - The outer cell updates (new inner event stream: initially energic)
     */
    @Test
    fun test_outerUpdates_newInnerEnergic() {
        fun test(
            shouldOuterFreezeSimultaneously: Boolean,
            shouldOldInnerEmitSimultaneously: Boolean,
            shouldNewInnerTerminateSimultaneously: Boolean,
        ) = testEventStream_initiallyEnergic(
            spawn = {
                val initialInnerEventStream = createEnergicEventStreamExternally(
                    emittedEventByTick = mapOfNotNull(
                        (Tick(1) to 11).takeIf { shouldOldInnerEmitSimultaneously },
                    ),
                    terminationTick = null,
                )

                val newInnerEventStream = createEnergicEventStreamExternally(
                    emittedEventByTick = emptyMap(),
                    terminationTick = Tick(1).takeIf { shouldNewInnerTerminateSimultaneously },
                )

                val outerCell = createDynamicCellExternally(
                    initialValue = initialInnerEventStream,
                    updatedValueByTick = mapOf(
                        Tick(1) to newInnerEventStream,
                    ),
                    freezeTick = Tick(1).takeIf { shouldOuterFreezeSimultaneously },
                )

                Cell.divert(outerCell)
            },
            expectedNotificationByTick = emptyMap(),
        )

        test(
            shouldOuterFreezeSimultaneously = true,
            shouldOldInnerEmitSimultaneously = false,
            shouldNewInnerTerminateSimultaneously = false,
        )

        test(
            shouldOuterFreezeSimultaneously = false,
            shouldOldInnerEmitSimultaneously = true,
            shouldNewInnerTerminateSimultaneously = false,
        )

        test(
            shouldOuterFreezeSimultaneously = false,
            shouldOldInnerEmitSimultaneously = false,
            shouldNewInnerTerminateSimultaneously = true,
        )
    }

    /**
     * - The outer cell updates (new inner event stream: initially energic)
     * - Simultaneously: The new inner event stream emits
     */
    // TODO: Add a test when the _old_ inner stream emits
    @Test
    fun test_outerUpdates_newInnerEnergic_newInnerEmitsSimultaneously() {
        fun test(
            shouldOuterFreezeSimultaneously: Boolean,
            shouldInnerTerminateSimultaneously: Boolean,
        ) = testEventStream_initiallyEnergic(
            spawn = {
                val initialInnerEventStream = createEnergicEventStreamExternally(
                    emittedEventByTick = emptyMap(),
                    terminationTick = null,
                )

                val newInnerEventStream = createEnergicEventStreamExternally(
                    emittedEventByTick = mapOf(
                        Tick(1) to 21,
                    ),
                    terminationTick = Tick(1).takeIf { shouldInnerTerminateSimultaneously },
                )

                val outerCell = createDynamicCellExternally(
                    initialValue = initialInnerEventStream,
                    updatedValueByTick = mapOf(
                        Tick(1) to newInnerEventStream,
                    ),
                    freezeTick = Tick(1).takeIf { shouldOuterFreezeSimultaneously },
                )

                Cell.divert(outerCell)
            },
            expectedNotificationByTick = emptyMap(),
        )

        test(
            shouldOuterFreezeSimultaneously = false,
            shouldInnerTerminateSimultaneously = false,
        )

        test(
            shouldOuterFreezeSimultaneously = true,
            shouldInnerTerminateSimultaneously = false,
        )

        test(
            shouldOuterFreezeSimultaneously = false,
            shouldInnerTerminateSimultaneously = true,
        )

        test(
            shouldOuterFreezeSimultaneously = true,
            shouldInnerTerminateSimultaneously = true,
        )
    }

    /**
     * - The outer cell updates (new inner event stream: initially energic)
     * - The subsequent inner event stream emits
     */
    @Test
//    @Ignore // FIXME: Vertex (...) is not a dependent of (...)
    fun test_outerUpdates_newInnerEnergic_newInnerEmitsLater() {
        fun test(
            shouldOuterFreezeSimultaneously: Boolean,
            shouldSubsequentInnerTerminateSimultaneously: Boolean,
        ) = testEventStream_initiallyEnergic(
            spawn = {
                val initialInnerEventStream = createEnergicEventStreamExternally<Int>(
                    emittedEventByTick = emptyMap(),
                    terminationTick = null,
                )

                val subsequentInnerEventStream = createEnergicEventStreamExternally(
                    emittedEventByTick = mapOf(
                        Tick(2) to 21,
                    ), terminationTick = Tick(2).takeIf { shouldSubsequentInnerTerminateSimultaneously })

                val outerCell = createDynamicCellExternally(
                    initialValue = initialInnerEventStream,
                    updatedValueByTick = mapOf(
                        Tick(1) to subsequentInnerEventStream,
                    ),
                    freezeTick = Tick(1).takeIf { shouldOuterFreezeSimultaneously },
                )

                Cell.divert(outerCell)
            },
            expectedNotificationByTick = mapOf(
                Tick(2) to EventStream.EmissionNotification.of(
                    emittedEvent = 21,
                    isTerminal = shouldOuterFreezeSimultaneously && shouldSubsequentInnerTerminateSimultaneously,
                ),
            ),
        )

        test(
            shouldOuterFreezeSimultaneously = false,
            shouldSubsequentInnerTerminateSimultaneously = false,
        )

        test(
            shouldOuterFreezeSimultaneously = true,
            shouldSubsequentInnerTerminateSimultaneously = false,
        )

        test(
            shouldOuterFreezeSimultaneously = false,
            shouldSubsequentInnerTerminateSimultaneously = true,
        )

        test(
            shouldOuterFreezeSimultaneously = true,
            shouldSubsequentInnerTerminateSimultaneously = true,
        )
    }

    /**
     * Initial inner event stream: inert
     *
     * - The outer cell freezes
     */
    @Test
    fun test_initialInnerExhausted_outerJustFreezes() {
        fun test(
            initialInnerEventStreamFactory: ExhaustedEventStreamFactory,
        ) {
            testEventStream_initiallyEnergic(
                spawn = {
                    val initialInnerEventStream = initialInnerEventStreamFactory.createExternally<Int>()

                    val outerCell = createDynamicCellExternally(
                        initialValue = initialInnerEventStream,
                        updatedValueByTick = emptyMap(),
                        freezeTick = Tick(1),
                    )

                    Cell.divert(outerCell)
                },
                expectedNotificationByTick = mapOf(
                    Tick(1) to EventStream.IsolatedTerminationNotification,
                ),
            )
        }

        ExhaustedEventStreamFactory.values.forEach { initialInnerEventStreamFactory ->
            test(
                initialInnerEventStreamFactory = initialInnerEventStreamFactory,
            )
        }
    }

    /**
     * Initial inner event stream: energic
     *
     * - The outer cell freezes
     * - The initial inner event stream terminates
     */
    @Test
//    @Ignore // FIXME: Vertex (...) is not a dependent of (...)
    fun test_initialInnerEnergic_outerJustFreezes_initialInnerTerminatesLater() {
        fun test(
            shouldInitialInnerEmitSimultaneously: Boolean,
        ) {
            val finalEmittedEvent = when {
                shouldInitialInnerEmitSimultaneously -> 11
                else -> null
            }

            testEventStream_initiallyEnergic(
                spawn = {
                    val initialInnerEventStream = createEnergicEventStreamExternally(
                        emittedEventByTick = mapOfNotNull(
                            finalEmittedEvent?.let {
                                Tick(2) to it
                            }),
                        terminationTick = Tick(2),
                    )

                    val outerCell = createDynamicCellExternally(
                        initialValue = initialInnerEventStream,
                        updatedValueByTick = emptyMap(),
                        freezeTick = Tick(1),
                    )

                    Cell.divert(outerCell)
                },
                expectedNotificationByTick = mapOf(
                    Tick(2) to EventStream.TerminationNotification.of(
                        emittedEvent = finalEmittedEvent,
                    ),
                ),
            )
        }

        test(
            shouldInitialInnerEmitSimultaneously = false,
        )

        test(
            shouldInitialInnerEmitSimultaneously = true,
        )
    }

    /**
     * Initial inner event stream: energic
     *
     * - The outer cell freezes
     * - Simultaneously: The initial inner event stream terminates
     */
    @Test
    fun test_initialInnerEnergic_outerJustFreezes_initialInnerTerminatesSimultaneously() {
        fun test(
            shouldInitialInnerEmitSimultaneously: Boolean,
        ) {
            val finalEmittedEvent = when {
                shouldInitialInnerEmitSimultaneously -> 11
                else -> null
            }

            testEventStream_initiallyEnergic(
                spawn = {
                    val initialInnerEventStream = createEnergicEventStreamExternally(
                        emittedEventByTick = mapOfNotNull(
                            finalEmittedEvent?.let {
                                Tick(1) to it
                            },
                        ),
                        terminationTick = Tick(1),
                    )

                    val outerCell = createDynamicCellExternally(
                        initialValue = initialInnerEventStream,
                        updatedValueByTick = emptyMap(),
                        freezeTick = Tick(1),
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

        test(
            shouldInitialInnerEmitSimultaneously = false,
        )

        test(
            shouldInitialInnerEmitSimultaneously = true,
        )
    }

    /**
     * - The outer cell updates (new inner event stream: immediately exhausted)
     * - The outer cell freezes
     */
    @Test
    fun test_outerUpdates_newInnerExhausted_outerJustFreezes() {
        fun test(
            newInnerEventStreamFactory: ExhaustedEventStreamFactory,
        ) {
            testEventStream_initiallyEnergic(
                spawn = {
                    val initialInnerEventStream = createEnergicEventStreamExternally(
                        emittedEventByTick = emptyMap(),
                        terminationTick = null,
                    )

                    val subsequentInnerEventStream = newInnerEventStreamFactory.createExternally<Int>()

                    val outerCell = createDynamicCellExternally(
                        initialValue = initialInnerEventStream,
                        updatedValueByTick = mapOf(
                            Tick(1) to subsequentInnerEventStream,
                        ),
                        freezeTick = Tick(2),
                    )

                    Cell.divert(outerCell)
                },
                expectedNotificationByTick = mapOf(
                    Tick(2) to EventStream.IsolatedTerminationNotification,
                ),
            )
        }

        ExhaustedEventStreamFactory.values.forEach { newInnerEventStreamFactory ->
            test(
                newInnerEventStreamFactory = newInnerEventStreamFactory,
            )
        }
    }

    /**
     * - The outer cell updates
     * - The outer cell freezes
     * - The subsequent inner event stream terminates
     */
    @Test
    fun test_outerUpdates_newInnerEnergic_outerJustFreezes_newInnerTerminatesLater() {
        fun test(
            shouldSubsequentInnerEmitSimultaneously: Boolean,
        ) {
            val finalEmittedEvent = when {
                shouldSubsequentInnerEmitSimultaneously -> 21
                else -> null
            }

            testEventStream_initiallyEnergic(
                spawn = {
                    val initialInnerEventStream = createEnergicEventStreamExternally<Int>(
                        emittedEventByTick = emptyMap(),
                        terminationTick = null,
                    )

                    val subsequentInnerEventStream = createEnergicEventStreamExternally(
                        emittedEventByTick = mapOfNotNull(
                            finalEmittedEvent?.let {
                                Tick(3) to it
                            },
                        ),
                        terminationTick = Tick(3),
                    )

                    val outerCell = createDynamicCellExternally(
                        initialValue = initialInnerEventStream,
                        updatedValueByTick = mapOf(
                            Tick(1) to subsequentInnerEventStream,
                        ),
                        freezeTick = Tick(2),
                    )

                    Cell.divert(outerCell)
                },
                expectedNotificationByTick = mapOf(
                    Tick(3) to EventStream.TerminationNotification.of(
                        emittedEvent = finalEmittedEvent,
                    ),
                ),
            )
        }

        test(
            shouldSubsequentInnerEmitSimultaneously = false,
        )

        test(
            shouldSubsequentInnerEmitSimultaneously = true,
        )
    }

    /**
     * - The outer cell updates
     * - The outer cell freezes
     * - Simultaneously: The subsequent inner event stream terminates
     */
    @Test
    fun test_outerUpdates_newInnerEnergic_outerJustFreezes_newInnerTerminatesSimultaneously() {
        fun test(
            shouldSubsequentInnerEmitSimultaneously: Boolean,
        ) {
            val finalEmittedEvent = when {
                shouldSubsequentInnerEmitSimultaneously -> 21
                else -> null
            }

            testEventStream_initiallyEnergic(
                spawn = {
                    val initialInnerEventStream = createEnergicEventStreamExternally<Int>(
                        emittedEventByTick = emptyMap(),
                        terminationTick = null,
                    )

                    val subsequentInnerEventStream = createEnergicEventStreamExternally(
                        emittedEventByTick = mapOfNotNull(
                            finalEmittedEvent?.let {
                                Tick(2) to it
                            },
                        ),
                        terminationTick = Tick(2),
                    )

                    val outerCell = createDynamicCellExternally(
                        initialValue = initialInnerEventStream,
                        updatedValueByTick = mapOf(
                            Tick(1) to subsequentInnerEventStream,
                        ),
                        freezeTick = Tick(2),
                    )

                    Cell.divert(outerCell)
                },
                expectedNotificationByTick = mapOf(
                    Tick(2) to EventStream.TerminationNotification.of(
                        emittedEvent = finalEmittedEvent,
                    ),
                ),
            )
        }

        test(
            shouldSubsequentInnerEmitSimultaneously = false,
        )

        test(
            shouldSubsequentInnerEmitSimultaneously = true,
        )
    }

    /**
     * - The outer cell updates, freezing (new inner event stream: immediately exhausted)
     */
    @Test
    fun test_outerUpdatesFreezing_newInnerExhausted() {
        fun test(
            newInnerEventStreamFactory: ExhaustedEventStreamFactory,
        ) {
            testEventStream_initiallyEnergic(
                spawn = {
                    val initialInnerEventStream = createEnergicEventStreamExternally(
                        emittedEventByTick = emptyMap(),
                        terminationTick = null,
                    )

                    val newInnerEventStream = newInnerEventStreamFactory.createExternally<Int>()

                    val outerCell = createDynamicCellExternally(
                        initialValue = initialInnerEventStream,
                        updatedValueByTick = mapOf(
                            Tick(1) to newInnerEventStream,
                        ),
                        freezeTick = Tick(1),
                    )

                    Cell.divert(outerCell)
                },
                expectedNotificationByTick = mapOf(
                    Tick(1) to EventStream.IsolatedTerminationNotification,
                ),
            )
        }

        ExhaustedEventStreamFactory.values.forEach { newInnerEventStreamFactory ->
            test(
                newInnerEventStreamFactory = newInnerEventStreamFactory,
            )
        }
    }

    /**
     * - The outer cell updates, freezing (new inner event stream: initially energic)
     * - The new inner event stream terminates
     */
    @Test
//    @Ignore // FIXME: Vertex (...) is not a dependent of (...)
    fun test_outerUpdatesFreezing_newInnerEnergic_newInnerTerminatesLater() {
        fun test(
            shouldNewInnerEmitSimultaneously: Boolean,
        ) {
            val finalEmittedEvent = when {
                shouldNewInnerEmitSimultaneously -> 21
                else -> null
            }

            testEventStream_initiallyEnergic(
                spawn = {
                    val initialInnerEventStream = createEnergicEventStreamExternally(
                        emittedEventByTick = emptyMap(),
                        terminationTick = null,
                    )

                    val newInnerEventStream = createEnergicEventStreamExternally(
                        emittedEventByTick = mapOfNotNull(
                            finalEmittedEvent?.let {
                                Tick(2) to it
                            },
                        ),
                        terminationTick = Tick(2),
                    )

                    val outerCell = createDynamicCellExternally(
                        initialValue = initialInnerEventStream,
                        updatedValueByTick = mapOf(
                            Tick(1) to newInnerEventStream,
                        ),
                        freezeTick = Tick(1),
                    )

                    Cell.divert(outerCell)
                },
                expectedNotificationByTick = mapOf(
                    Tick(2) to EventStream.TerminationNotification.of(
                        emittedEvent = finalEmittedEvent,
                    ),
                ),
            )
        }

        test(
            shouldNewInnerEmitSimultaneously = false,
        )

        test(
            shouldNewInnerEmitSimultaneously = true,
        )
    }

    /**
     * - The outer cell updates, freezing (new inner event stream: initially energic)
     * - Simultaneously: The new inner event stream terminates
     */
    @Test
    fun test_outerUpdatesFreezing_newInnerEnergic_newInnerTerminatesSimultaneously() {
        fun test(
            shouldNewInnerEmitSimultaneously: Boolean,
        ) {
            val finalEmittedEvent = when {
                shouldNewInnerEmitSimultaneously -> 21
                else -> null
            }

            testEventStream_initiallyEnergic(
                spawn = {
                    val initialInnerEventStream = createEnergicEventStreamExternally(
                        emittedEventByTick = emptyMap(),
                        terminationTick = null,
                    )

                    val newInnerEventStream = createEnergicEventStreamExternally(
                        emittedEventByTick = mapOfNotNull(
                            finalEmittedEvent?.let {
                                Tick(1) to it
                            },
                        ),
                        terminationTick = Tick(1),
                    )

                    val outerCell = createDynamicCellExternally(
                        initialValue = initialInnerEventStream,
                        updatedValueByTick = mapOf(
                            Tick(1) to newInnerEventStream,
                        ),
                        freezeTick = Tick(1),
                    )

                    Cell.divert(outerCell)
                },
                expectedNotificationByTick = mapOf(
                    Tick(1) to EventStream.TerminalEmissionNotification(
                        emittedTerminalEvent = when {
                            shouldNewInnerEmitSimultaneously -> 21
                            else -> 20
                        }
                    ),
                ),
            )
        }

        test(
            shouldNewInnerEmitSimultaneously = false,
        )

        test(
            shouldNewInnerEmitSimultaneously = true,
        )
    }
}
