package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.sample
import dev.toolkt.reactive.cell.test_utils.GivenCellTimeline.GivenNotification
import dev.toolkt.reactive.cell.test_utils.GivenCellTimeline.GivenUpdate
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
    givenCellTimeline: GivenCellTimeline<ValueT>,
): Cell<ValueT> {
    val givenInitialValue = givenCellTimeline.givenInitialValue
    val givenNotificationByTick = givenCellTimeline.givenNotificationByTick

    val onTickCropped = when (val freezeTick = givenCellTimeline.freezeTick) {
        null -> context.onTick

        else -> MomentContext.execute {
            context.onTick.take(freezeTick.t)
        }
    }

    return MomentContext.execute {
        Cell.define(
            initialValue = givenInitialValue,
            newValues = onTickCropped.mapNotNull { tick ->
                val givenNotification = givenNotificationByTick[tick] ?: return@mapNotNull null

                when (givenNotification) {
                    is GivenUpdate -> givenNotification.givenUpdatedValue

                    else -> null
                }
            },
        )
    }
}

context(context: CellDynamicTestContext) fun <ValueT : Any> createDynamicCellExternally(
    givenInitialValue: ValueT,
    givenNotificationByTick: Map<Tick, GivenNotification<ValueT>>,
): Cell<ValueT> = createDynamicCellExternally(
    givenCellTimeline = GivenCellTimeline(
        givenInitialValue = givenInitialValue,
        givenNotificationByTick = givenNotificationByTick,
    ),
)

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

    val receivedUpdatedValues = mutableListOf<ValueT>()

    // TODO: Use different verification strategies
    // TODO: Handle freezing

    assertNotNull(
        actual = subjectCell.updatedValues.subscribe { updatedValue ->
            receivedUpdatedValues.add(updatedValue)
        },
        message = "Expected a non-null subscription for a dynamic cell",
    )

    val sampledInitialValue = subjectCell.sampleExternally()

    assertEquals(
        expected = expectedInitialValue,
        actual = sampledInitialValue,
    )

    val maxTick = expectedNotificationByTick.keys.maxByOrNull { it.t } ?: return

    (1..maxTick.t).forEach { t ->
        val tick = Tick(t = t)
        val expectedNotification = expectedNotificationByTick[tick]

        receivedUpdatedValues.clear()

        doTick.emit(tick)

        when (expectedNotification) {
            null -> {
                assertEquals(
                    expected = 0,
                    actual = receivedUpdatedValues.size,
                    message = "At t=${tick.t}, no updates expected, but received: $receivedUpdatedValues",
                )
            }

            else -> {
                when (expectedNotification) {
                    is Cell.UpdateNotification -> {
                        val expectedUpdatedValue = expectedNotification.updatedValue

                        assertEquals(
                            expected = 1,
                            actual = receivedUpdatedValues.size,
                            message = "At t=${tick.t}, as single update expected, but received: $receivedUpdatedValues",
                        )

                        val receivedUpdatedValue = receivedUpdatedValues.single()

                        assertEquals(
                            expected = expectedUpdatedValue,
                            actual = receivedUpdatedValue,
                            message = "At t=${tick.t}, expected updated value ${expectedNotification.updatedValue}, but received: $receivedUpdatedValue",
                        )

                        val sampledNewValue = subjectCell.sampleExternally()

                        assertEquals(
                            expected = expectedUpdatedValue,
                            actual = sampledNewValue,
                        )
                    }

                    else -> {
                        // TODO
                    }
                }
            }
        }
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
