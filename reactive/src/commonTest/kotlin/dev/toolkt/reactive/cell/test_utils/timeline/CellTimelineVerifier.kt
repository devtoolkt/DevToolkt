package dev.toolkt.reactive.cell.test_utils.timeline

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.observe
import dev.toolkt.reactive.cell.sample
import dev.toolkt.reactive.cell.test_utils.BufferingObservation
import dev.toolkt.reactive.cell.test_utils.observeBuffering
import dev.toolkt.reactive.cell.test_utils.sampleExternally
import dev.toolkt.reactive.cell.updatedValues
import dev.toolkt.reactive.event_stream.single
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertIsNot
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.fail

sealed class CellTimelineVerifier {
    data object Passive : CellTimelineVerifier() {
        override fun <ValueT : Any> verifyStatelessCell(
            ticker: TimelineTicker,
            statelessSubjectCell: Cell<ValueT>,
            lastVerifiedTick: RawTick,
            expectedSubjectCellTimeline: ExpectedCellTimeline<ValueT>,
        ) {
            // The stateless cell is frozen before the first tick iff semantically it froze at t=-Inf (is const).
            val isFrozenUpFront = checkIfIsFrozen(
                cell = statelessSubjectCell,
            )

            when (expectedSubjectCellTimeline) {
                is ExpectedCellTimeline.Const -> {
                    assertTrue(
                        actual = isFrozenUpFront,
                        message = "The subject cell is expected to be constant, but it is not frozen up-front.",
                    )

                    val sampledConstValue = statelessSubjectCell.sampleExternally()

                    assertEquals(
                        actual = sampledConstValue,
                        expected = expectedSubjectCellTimeline.expectedConstValue,
                        message = "The subject cell's constant value does not match the expected constant value.",
                    )
                }

                is ExpectedCellTimeline.Dynamic -> {
                    assertFalse(
                        actual = isFrozenUpFront,
                        message = "The subject cell is expected to be dynamic, but it is frozen up-front.",
                    )

                    val sampledInitialOldValue = statelessSubjectCell.sampleExternally()

                    assertEquals(
                        expected = expectedSubjectCellTimeline.expectedInitialValue,
                        actual = sampledInitialOldValue,
                        message = "The subject cell's initial value does not match the expected initial value.",
                    )

                    verifyDynamicCellTail(
                        ticker = ticker,
                        subjectCell = statelessSubjectCell,
                        firstTrailingVerifiedTick = RawTick.First,
                        lastVerifiedTick = lastVerifiedTick,
                        expectedSubjectCellTimeline = expectedSubjectCellTimeline,
                    )
                }
            }
        }

        override fun <ValueT : Any> verifyStatefulCell(
            ticker: TimelineTicker,
            spawnTick: RawTick,
            spawnStatefulSubjectCell: context(MomentContext) () -> Cell<ValueT>,
            lastVerifiedTick: RawTick,
            expectedSubjectCellTimeline: ExpectedCellTimeline.Dynamic<ValueT>,
        ) {
            ticker.fastForward(
                stopTick = spawnTick,
            )

            val (subjectCell, sampledSpawnOldValue) = ticker.evaluate(
                tick = spawnTick,
            ) {
                val subjectCell = spawnStatefulSubjectCell()

                // As the subject cell can update at t=t_spawn, we have to sample the old value internally, within
                // a transaction (we'll not be able to retrieve it later).
                val sampledSpawnOldValue = subjectCell.sample()

                Pair(
                    subjectCell,
                    sampledSpawnOldValue,
                )
            }

            val expectedInitialValue = expectedSubjectCellTimeline.expectedInitialValue

            assertEquals(
                expected = expectedInitialValue,
                // From the stateful cell semantics, we know that the stateful cell exposes the same value from t=-Inf
                // to t=t_spawn. That implies that the old value observed at spawn is the initial value in the semantic
                // sense.
                actual = sampledSpawnOldValue,
                message = "The subject cell's initial value at spawn tick (t=${spawnTick.t}) does not match the expected initial value.",
            )

            val expectedSpawnUpdatedValue = expectedSubjectCellTimeline.getExpectedUpdatedValue(
                tick = spawnTick,
            )

            // As we're not actively observing the cell, we have to sample the new value externally
            val sampledSpawnNewValue = subjectCell.sampleExternally()

            // From the expected timeline contract, we know that same-valued updates (x -> x) are not supported. From
            // the cell semantics, we know that (value_old != value_new) implies that an update happened. In consequence,
            // we can assume that an update takes place if **and only if** value_old != value_new.
            val sampledSpawnUpdatedValue = sampledSpawnNewValue.takeIf { it != sampledSpawnOldValue }

            assertEquals(
                expected = expectedSpawnUpdatedValue,
                actual = sampledSpawnUpdatedValue,
                message = "The subject cell's updated value at spawn tick (t=${spawnTick.t}) does not match the expected updated value.",
            )

            val shouldExpectFreezeAtSpawn = expectedSubjectCellTimeline.shouldExpectFreeze(tick = spawnTick)

            val isFrozenImmediately = checkIfIsFrozen(
                cell = subjectCell,
            )

            when {
                shouldExpectFreezeAtSpawn -> {
                    assertTrue(
                        actual = isFrozenImmediately,
                        message = "The subject cell is expected to be frozen immediately at spawn tick (t=${spawnTick.t}), but it is not.",
                    )
                }

                else -> {
                    assertFalse(
                        actual = isFrozenImmediately,
                        message = "The subject cell is expected to be warm immediately at spawn tick (t=${spawnTick.t}), but it is frozen.",
                    )
                }
            }

            verifyDynamicCellTail(
                ticker = ticker,
                subjectCell = subjectCell,
                firstTrailingVerifiedTick = spawnTick.next,
                lastVerifiedTick = lastVerifiedTick,
                expectedSubjectCellTimeline = expectedSubjectCellTimeline,
            )
        }

        private fun <ValueT : Any> verifyDynamicCellTail(
            ticker: TimelineTicker,
            subjectCell: Cell<ValueT>,
            firstTrailingVerifiedTick: RawTick,
            lastVerifiedTick: RawTick,
            expectedSubjectCellTimeline: ExpectedCellTimeline.Dynamic<ValueT>,
        ) {
            firstTrailingVerifiedTick.generateTicksUpTo(
                lastTick = lastVerifiedTick,
            ).forEach { tick ->
                val expectedOldValue = expectedSubjectCellTimeline.getExpectedOldValue(
                    tick = tick,
                )

                val sampledOldValue = subjectCell.sampleExternally()

                assertEquals(
                    expected = expectedOldValue,
                    actual = sampledOldValue,
                    message = "The subject cell's old value at tick t=${tick.t} does not match the expected old value.",
                )

                val shouldExpectFreezeNow = expectedSubjectCellTimeline.shouldExpectFreeze(tick = tick)

                val isFrozenBeforeTick = checkIfIsFrozen(
                    cell = subjectCell,
                )

                assertFalse(
                    actual = isFrozenBeforeTick,
                    message = "The subject cell is expected to not be frozen just before the tick (t=${tick.t}), but it is.",
                )

                ticker.proceed(
                    tick = tick,
                )

                val expectedUpdatedValue = expectedSubjectCellTimeline.getExpectedUpdatedValue(
                    tick = tick,
                )

                val sampledNewValue = subjectCell.sampleExternally()

                // We assume that an update takes place if **and only if** value_old != value_new.
                val sampledUpdatedValue = sampledNewValue.takeIf { it != sampledOldValue }

                assertEquals(
                    expected = expectedUpdatedValue,
                    actual = sampledUpdatedValue,
                    message = "The subject cell's updated value at tick t=${tick.t} does not match the expected updated value.",
                )

                val isFrozenPostTick = checkIfIsFrozen(
                    cell = subjectCell,
                )

                when {
                    shouldExpectFreezeNow -> {
                        assertTrue(
                            actual = isFrozenPostTick,
                            message = "The subject cell is expected to be frozen after the freeze tick (t=${tick.t}), but it is not.",
                        )
                    }

                    else -> {
                        assertFalse(
                            actual = isFrozenPostTick,
                            message = "The subject cell is expected to be warm after the tick (t=${tick.t}), but it is frozen.",
                        )
                    }
                }
            }
        }
    }

    data class Active(
        val observationSpan: ObservationSpan,
    ) : CellTimelineVerifier() {
        data class ObservationSpan(
            val firstObservedTick: RawTick,
        ) {
            companion object {
                val Full = ObservationSpan(
                    firstObservedTick = RawTick.First,
                )
            }

            constructor(
                firstObservedNamedTick: BaseNamedTick,
            ) : this(
                firstObservedTick = firstObservedNamedTick.ordinalTick,
            )
        }

        private data class SpawnSnapshot<ValueT : Any>(
            val subjectCell: Cell<ValueT>,
            private val sampledOldValue: ValueT,
            private val updatedValueCell: Cell<ValueT?>,
        ) {
            companion object {
                context(momentContext: MomentContext) fun <ValueT : Any> prepare(
                    subjectCell: Cell<ValueT>,
                ): SpawnSnapshot<ValueT> {
                    val sampledOldValue = subjectCell.sample()

                    val updateValueCell = Cell.define(
                        initialValue = null,
                        newValues = subjectCell.updatedValues.single(),
                    )

                    return SpawnSnapshot(
                        subjectCell = subjectCell,
                        sampledOldValue = sampledOldValue,
                        updatedValueCell = updateValueCell,
                    )
                }
            }

            fun extractOldValue(): ValueT = sampledOldValue

            fun extractUpdatedValueExternally(): ValueT? = updatedValueCell.sampleExternally()
        }

        override fun <ValueT : Any> verifyStatelessCell(
            ticker: TimelineTicker,
            statelessSubjectCell: Cell<ValueT>,
            lastVerifiedTick: RawTick,
            expectedSubjectCellTimeline: ExpectedCellTimeline<ValueT>,
        ) {
            val firstObservedTick = observationSpan.firstObservedTick

            ticker.fastForward(
                stopTick = firstObservedTick,
            )

            val observation = statelessSubjectCell.observeBuffering()

            when (expectedSubjectCellTimeline) {
                is ExpectedCellTimeline.Const -> {
                    assertNull(
                        actual = observation,
                        message = "The subject cell is expected to be constant (frozen up-front), but it is warm.",
                    )

                    val sampledConstValue = statelessSubjectCell.sampleExternally()

                    assertEquals(
                        expected = expectedSubjectCellTimeline.expectedConstValue,
                        actual = sampledConstValue,
                        message = "The subject cell's constant value does not match the expected constant value.",
                    )
                }

                is ExpectedCellTimeline.Dynamic -> {
                    assertNotNull(
                        actual = observation,
                        message = "The subject cell is expected to be initially warm, but it is frozen up-front (constant).",
                    )

                    verifyCellTail(
                        ticker = ticker,
                        subjectCell = statelessSubjectCell,
                        observation = observation,
                        firstTrailingVerifiedTick = firstObservedTick,
                        lastVerifiedTick = lastVerifiedTick,
                        expectedSubjectCellTimeline = expectedSubjectCellTimeline,
                    )
                }
            }
        }

        override fun <ValueT : Any> verifyStatefulCell(
            ticker: TimelineTicker,
            spawnTick: RawTick,
            spawnStatefulSubjectCell: context(MomentContext) () -> Cell<ValueT>,
            lastVerifiedTick: RawTick,
            expectedSubjectCellTimeline: ExpectedCellTimeline.Dynamic<ValueT>,
        ) {
            val firstObservedTick = observationSpan.firstObservedTick

            require(firstObservedTick.t >= spawnTick.t) {
                "The first observed tick (t=${firstObservedTick.t}) cannot be earlier than the spawn tick (t=${spawnTick.t})."
            }

            ticker.fastForward(
                stopTick = spawnTick,
            )

            when {
                firstObservedTick == spawnTick -> { // The cell is observed at the same tick it is spawned
                    val spawnSnapshot = ticker.evaluate(
                        tick = spawnTick,
                    ) {
                        val subjectCell = spawnStatefulSubjectCell()

                        SpawnSnapshot.prepare(
                            subjectCell = subjectCell,
                        )
                    }

                    val subjectCell = spawnSnapshot.subjectCell

                    val sampledSpawnOldValue = spawnSnapshot.extractOldValue()

                    assertEquals(
                        expected = expectedSubjectCellTimeline.expectedInitialValue,
                        actual = sampledSpawnOldValue,
                        message = "The subject cell's initial value at spawn tick (t=${spawnTick.t}) does not match the expected initial value.",
                    )

                    val expectedSpawnUpdatedValue =
                        expectedSubjectCellTimeline.getExpectedUpdatedValue(tick = spawnTick)
                    val receivedSpawnUpdatedValue = spawnSnapshot.extractUpdatedValueExternally()

                    assertEquals(
                        expected = expectedSpawnUpdatedValue,
                        actual = receivedSpawnUpdatedValue,
                        message = "The subject cell's updated value at spawn tick (t=${spawnTick.t}) does not match the expected updated value.",
                    )

                    // Start observing _after_ the spawn/observation tick, as it's impossible a cell that's not spawned yet
                    val postObservation = subjectCell.observeBuffering()

                    if (expectedSubjectCellTimeline.shouldBeFrozenAfter(tick = spawnTick)) {
                        assertNull(
                            actual = postObservation,
                            message = "The subject cell is expected to be frozen after the spawn tick (t=${spawnTick.t}), but it warm.",
                        )
                    } else {
                        assertNotNull(
                            actual = postObservation,
                            message = "The subject cell is expected to be warm after the spawn tick (t=${spawnTick.t}), but it is frozen.",
                        )

                        verifyCellTail(
                            ticker = ticker,
                            subjectCell = subjectCell,
                            observation = postObservation,
                            firstTrailingVerifiedTick = spawnTick.next,
                            lastVerifiedTick = lastVerifiedTick,
                            expectedSubjectCellTimeline = expectedSubjectCellTimeline,
                        )
                    }
                }

                else -> { // The cell is observed after being spawned
                    val statefulSubjectCell = ticker.evaluate(
                        tick = spawnTick,
                    ) {
                        spawnStatefulSubjectCell()
                    }

                    ticker.fastForward(
                        stopTick = firstObservedTick,
                    )

                    // Start observing just before the first observed tick
                    val preObservation = statefulSubjectCell.observeBuffering()

                    val expectedOldValue = expectedSubjectCellTimeline.getExpectedOldValue(
                        tick = firstObservedTick,
                    )

                    val sampledOldValue = statefulSubjectCell.sampleExternally()

                    assertEquals(
                        expected = expectedOldValue,
                        actual = sampledOldValue,
                        message = "The subject cell's old value before the first observed tick (t=${firstObservedTick.t}) does not match the expected old value.",
                    )

                    if (expectedSubjectCellTimeline.shouldBeFrozenBefore(tick = firstObservedTick)) {
                        assertNull(
                            actual = preObservation,
                            message = "The subject cell is expected to be frozen before the first observed tick (t=${firstObservedTick.t}), but it is warm.",
                        )
                    } else {
                        assertNotNull(
                            actual = preObservation,
                            message = "The subject cell is expected to be warm before the first observed tick (t=${firstObservedTick.t}), but it is frozen.",
                        )

                        // In this path, the first observed tick is treated like any other
                        verifyCellTail(
                            ticker = ticker,
                            subjectCell = statefulSubjectCell,
                            observation = preObservation,
                            firstTrailingVerifiedTick = firstObservedTick,
                            lastVerifiedTick = lastVerifiedTick,
                            expectedSubjectCellTimeline = expectedSubjectCellTimeline,
                        )
                    }
                }
            }
        }

        private fun <ValueT : Any> verifyCellTail(
            ticker: TimelineTicker,
            subjectCell: Cell<ValueT>,
            observation: BufferingObservation<ValueT>,
            firstTrailingVerifiedTick: RawTick,
            lastVerifiedTick: RawTick,
            expectedSubjectCellTimeline: ExpectedCellTimeline.Dynamic<ValueT>,
        ) {
            firstTrailingVerifiedTick.generateTicksUpTo(
                lastTick = lastVerifiedTick,
            ).forEach { tick ->
                val sampledOldValue = ticker.evaluate(
                    tick = tick,
                ) {
                    subjectCell.sample()
                }

                val expectedOldValue = expectedSubjectCellTimeline.getExpectedOldValue(
                    tick = tick,
                )

                // Verify the old value first. If it doesn't match, it could indicate an issue with atomicity.
                assertEquals(
                    expected = expectedOldValue,
                    actual = sampledOldValue,
                    message = "The subject cell's old value at tick t=${tick.t} does not match the expected old value.",
                )

                val receivedNotifications = observation.extractReceivedNotifications()
                val receivedNotificationCount = receivedNotifications.size

                if (receivedNotificationCount > 1) {
                    fail(
                        message = "The subject cell emitted more than one notification (exactly: $receivedNotificationCount) at tick t=${tick.t}.",
                    )
                }

                val singleReceivedNotification = receivedNotifications.singleOrNull()

                val expectedUpdatedValue = expectedSubjectCellTimeline.getExpectedUpdatedValue(
                    tick = tick,
                )

                when {
                    expectedUpdatedValue != null -> {
                        val receivedUpdateNotification = assertIs<Cell.UpdateNotification<*>>(
                            value = singleReceivedNotification,
                            message = "The subject cell is expected to emit an update notification at tick t=${tick.t}",
                        )

                        assertEquals(
                            expected = expectedUpdatedValue,
                            actual = receivedUpdateNotification.updatedValue,
                            message = "Unexpected updated value received from the subject cell at tick t=${tick.t}.",
                        )

                        val sampledNewValue = subjectCell.sampleExternally()

                        assertEquals(
                            expected = expectedUpdatedValue,
                            actual = sampledNewValue,
                            message = "The subject cell's new value after tick t=${tick.t} does not match the expected updated value.",
                        )
                    }

                    else -> {
                        assertIsNot<Cell.UpdateNotification<*>>(
                            value = singleReceivedNotification,
                            message = "The subject cell is not expected to emit an update notification at tick t=${tick.t}",
                        )
                    }
                }

                val shouldExpectFreezeNow = expectedSubjectCellTimeline.shouldExpectFreeze(
                    tick = tick,
                )

                when {
                    shouldExpectFreezeNow -> {
                        assertIs<Cell.FreezeNotification<*>>(
                            value = singleReceivedNotification,
                            message = "The subject cell is expected to emit a freeze notification at tick t=${tick.t}",
                        )
                    }

                    else -> {
                        assertIsNot<Cell.FreezeNotification<*>>(
                            value = singleReceivedNotification,
                            message = "The subject cell is not expected to emit a freeze notification at tick t=${tick.t}",
                        )
                    }
                }
            }
        }
    }

    companion object {
        private fun <ValueT> checkIfIsFrozen(
            cell: Cell<ValueT>,
        ): Boolean {
            val observation = cell.observe(
                observer = object : Cell.Observer<ValueT> {
                    override fun handleNotification(
                        notification: Cell.Notification<ValueT>,
                    ) {
                    }
                },
            ) ?: return true

            observation.cancel()

            return false
        }
    }

    abstract fun <ValueT : Any> verifyStatelessCell(
        ticker: TimelineTicker,
        statelessSubjectCell: Cell<ValueT>,
        lastVerifiedTick: RawTick,
        expectedSubjectCellTimeline: ExpectedCellTimeline<ValueT>,
    )

    abstract fun <ValueT : Any> verifyStatefulCell(
        ticker: TimelineTicker,
        spawnTick: RawTick,
        spawnStatefulSubjectCell: context (MomentContext) () -> Cell<ValueT>,
        lastVerifiedTick: RawTick,
        expectedSubjectCellTimeline: ExpectedCellTimeline.Dynamic<ValueT>,
    )
}
