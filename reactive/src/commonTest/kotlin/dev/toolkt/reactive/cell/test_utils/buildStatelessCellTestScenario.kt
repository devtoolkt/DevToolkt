package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.observe
import dev.toolkt.reactive.event_stream.EmitterEventStream
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertIsNot

fun <InputT, ValueT> buildStatelessCellTestScenario(
    configure: context (MomentContext, ConfigurationContext) () -> InputT,
    instantiate: InputT.() -> Cell<ValueT>,
    verificationTick: TickAlike,
    expectedUpdatedValue: ValueT?,
    shouldExpectFreeze: Boolean,
): StatelessCellTestScenario = object : StatelessCellTestScenario {
    override fun testObserved(
        observationTick: TickAlike,
    ) {
        val properObservationTick = observationTick.asTick
        val properVerificationTick = verificationTick.asTick

        require(properObservationTick.t <= properVerificationTick.t) {
            "Observation tick can't be later than expected update tick"
        }

        if (properObservationTick == properVerificationTick) {
            return // TODO: Implement pre-observed verification
        }

        val ticker = Ticker()

        val (configuredInput, maxRecordedTick) = MomentContext.execute {
            val configurationContext = ConfigurationContext(
                momentContext = MomentContext.extract(),
                ticker = ticker,
            )

            val configuredInput = with(configurationContext) {
                configure()
            }

            val maxRecordedTick = configurationContext.getMaxRecordedTick()

            configurationContext.close()

            Pair(configuredInput, maxRecordedTick)
        }

        require(properObservationTick.t <= maxRecordedTick.t) {
            "Observation tick ${properObservationTick.t} exceeds the max recorded tick ${maxRecordedTick.t}"
        }

        require(properVerificationTick.t <= maxRecordedTick.t) {
            "Verification tick ${properVerificationTick.t} exceeds the max recorded tick ${maxRecordedTick.t}"
        }

        val subjectCell = configuredInput.instantiate()

        ticker.fastForward(
            stopTick = properObservationTick,
        )

        var receivedNotifications: MutableList<Cell.Notification<ValueT>>? = null

        subjectCell.observe(
            observer = object : Cell.Observer<ValueT> {
                override fun handleNotification(
                    notification: Cell.Notification<ValueT>,
                ) {
                    receivedNotifications?.add(notification)
                }
            },
        )

        ticker.fastForward(
            stopTick = properVerificationTick,
        )

        receivedNotifications = mutableListOf()

        ticker.step(
            properVerificationTick,
        )

        when {
            expectedUpdatedValue == null && !shouldExpectFreeze -> {
                val receivedNotificationCount = receivedNotifications.size

                assertEquals(
                    expected = 0,
                    actual = receivedNotificationCount,
                    message = "Expected no notifications at verification tick (got: $receivedNotificationCount)",
                )
            }

            else -> {
                assertEquals(
                    expected = 1,
                    actual = receivedNotifications.size,
                    message = "Expected exactly one notification at verification tick",
                )

                val singleReceivedNotification = receivedNotifications.single()

                when {
                    expectedUpdatedValue != null -> {
                        val singleReceivedUpdateNotification = assertIs<Cell.UpdateNotification<*>>(
                            value = singleReceivedNotification,
                            message = "Expected an update notification",
                        )

                        assertEquals(
                            expected = expectedUpdatedValue,
                            actual = singleReceivedUpdateNotification.updatedValue,
                            message = "Received updated value differs from expected",
                        )
                    }

                    else -> {
                        assertIsNot<Cell.UpdateNotification<*>>(
                            value = singleReceivedNotification,
                            message = "Did not expect an update notification",
                        )
                    }
                }

                when {
                    shouldExpectFreeze -> {
                        assertIs<Cell.FreezeNotification<*>>(
                            value = singleReceivedNotification,
                            message = "Expected a freeze notification",
                        )
                    }

                    else -> {
                        assertIsNot<Cell.FreezeNotification<*>>(
                            value = singleReceivedNotification,
                            message = "Did not expect a freeze notification",
                        )
                    }
                }
            }
        }
    }
}

class Ticker {
    private var tCurrent = -1

    private val doEmitTick = EmitterEventStream<Tick>()

    val onTick: EmitterEventStream<Tick>
        get() = doEmitTick

    fun step(
        nextTick: Tick,
    ) {
        step(tNext = nextTick.t)
    }

    private fun step(
        tNext: Int,
    ) {
        require(tNext == tCurrent + 1) {
            "Expected tick must be exactly one more than current tick"
        }

        println("Emit: $tNext")

        doEmitTick.emit(Tick(t = tNext))

        tCurrent = tNext
    }

    fun fastForward(
        stopTick: Tick,
    ) {
        require(stopTick.t > tCurrent) {
            "Stop tick (${stopTick.t}) must be later than current tick ($tCurrent)"
        }

        while (tCurrent < stopTick.t - 1) {
            step(tNext = tCurrent + 1)
        }
    }
}
