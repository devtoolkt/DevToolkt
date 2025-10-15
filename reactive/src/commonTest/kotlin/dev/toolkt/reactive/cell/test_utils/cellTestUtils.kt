package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.observe
import dev.toolkt.reactive.cell.sample
import dev.toolkt.reactive.cell.updatedValues
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.mapNotNull
import dev.toolkt.reactive.event_stream.subscribe
import dev.toolkt.reactive.event_stream.take
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

fun <ValueT> Cell<ValueT>.sampleExternally(): ValueT = MomentContext.execute {
    sample()
}

interface CellDynamicTestContext {
    val onTick: EventStream<Tick>
}

context(context: CellDynamicTestContext) fun <ValueT : Any> createDynamicCellExternally(
    initialValue: ValueT,
    updatedValueByTick: Map<Tick, ValueT>,
    freezeTick: Tick?,
): Cell<ValueT> {
    val onTickCropped = when (freezeTick) {
        null -> context.onTick

        else -> MomentContext.execute {
            context.onTick.take(freezeTick.t)
        }
    }

    return MomentContext.execute {
        Cell.define(
            initialValue = initialValue,
            newValues = onTickCropped.mapNotNull { tick ->
                updatedValueByTick[tick]
            },
        )
    }
}

fun <ValueT : Any> testCell_initiallyDynamic(
    setup: context(CellDynamicTestContext) () -> Cell<ValueT>,
    expectedInitialValue: ValueT,
    expectedNotificationByTick: Map<Tick, Cell.Notification<ValueT>>,
) {
    val doTick = EmitterEventStream<Tick>()

    val subjectCell = with(
        object : CellDynamicTestContext {
            override val onTick: EventStream<Tick> = doTick
        },
    ) {
        setup()
    }

    val receivedNotifications = mutableListOf<Cell.Notification<ValueT>>()

    // TODO: Use different verification strategies

    assertNotNull(
        actual = subjectCell.observe(
            observer = object : Cell.Observer<ValueT> {
                override fun handleNotification(
                    notification: Cell.Notification<ValueT>,
                ) {
                    receivedNotifications.add(notification)
                }
            },
        ),
        message = "Expected a non-null observation for a dynamic cell",
    )

    val sampledInitialValue = subjectCell.sampleExternally()

    assertEquals(
        expected = expectedInitialValue,
        actual = sampledInitialValue,
    )

    val lastTick = expectedNotificationByTick.keys.maxByOrNull { it.t } ?: return

    (1..lastTick.t).forEach { t ->
        val tick = Tick(t = t)

        receivedNotifications.clear()

        doTick.emit(tick)

        val expectedNotification = expectedNotificationByTick[tick]

        when {
            expectedNotification != null -> {
                assertEquals(
                    expected = 1,
                    actual = receivedNotifications.size,
                    message = "At t=${tick.t}, as single notification expected ($expectedNotification), but received: $receivedNotifications",
                )
            }

            else -> {
                assertEquals(
                    expected = 0,
                    actual = receivedNotifications.size,
                    message = "At t=${tick.t}, no notifications expected, but received: $receivedNotifications",
                )
            }
        }

        val receivedNotification = receivedNotifications.single()

        assertEquals(
            expected = expectedNotification,
            actual = receivedNotification,
            message = "At t=${tick.t}, expected $expectedNotification, but received: $receivedNotification",
        )
    }
}

interface CellInertTestContext

fun <ValueT : Any> testCell_immediatelyInert(
    setup: context(CellInertTestContext) () -> Cell<ValueT>,
    expectedValue: ValueT,
) {
    val subjectCell = with(
        object : CellInertTestContext {},
    ) {
        setup()
    }

    val sampledInitialValue = subjectCell.sampleExternally()

    assertEquals(
        expected = expectedValue,
        actual = sampledInitialValue,
    )

    assertNull(
        actual = subjectCell.updatedValues.subscribe {},
        message = "Expected a null subscription for an inert cell",
    )
}
