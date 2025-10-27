package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.observe
import dev.toolkt.reactive.cell.sample
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.mapNotNull
import dev.toolkt.reactive.event_stream.take
import dev.toolkt.reactive.test_utils.DynamicTestContext
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

interface CellVerificationContext {

}

interface ActiveCellVerificationContext : CellVerificationContext {

}


interface PassiveCellVerificationContext : CellVerificationContext {

}


interface CellObservationContext<ValueT> {

}

fun <ValueT> Cell<ValueT>.sampleExternally(): ValueT = MomentContext.execute {
    sample()
}

context(context: DynamicTestContext) fun <ValueT : Any> createDynamicCellExternally(
    initialValue: ValueT,
    updatedValueByTick: Map<TickAlike, ValueT>,
    freezeTick: TickAlike?,
): Cell<ValueT> {
    val updatedValueByProperTick = updatedValueByTick.entries.associate { (key, value) ->
        key.asTick to value
    }

    val onTickCropped = when (val properFreezeTick = freezeTick?.asTick) {
        null -> context.onTick

        else -> MomentContext.execute {
            context.onTick.take(properFreezeTick.t)
        }
    }

    return MomentContext.execute {
        Cell.define(
            initialValue = initialValue,
            newValues = onTickCropped.mapNotNull { tick ->
                updatedValueByProperTick[tick]
            },
        )
    }
}

fun <ValueT : Any> testCell_initiallyDynamic(
    // TODO: Split to `setup` / `spawn` (both in MomentContext?)
    // Add negative ticks & the tick "0"
    spawnSubject: context(DynamicTestContext) () -> Cell<ValueT>,
    expectedInitialValue: ValueT,
    expectedNotificationByTick: Map<Tick, Cell.Notification<ValueT>>,
) {
    val doTick = EmitterEventStream<Tick>()

    val subjectCell = with(
        object : DynamicTestContext {
            override val onTick: EventStream<Tick> = doTick
        },
    ) {
        spawnSubject()
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

                val receivedNotification = receivedNotifications.single()

                assertEquals(
                    expected = expectedNotification,
                    actual = receivedNotification,
                    message = "At t=${tick.t}, expected $expectedNotification, but received: $receivedNotification",
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
    }
}

interface ReactiveVerifier {

}


fun testReactiveSystem(
    block: context(DynamicTestContext) () -> ReactiveVerifier,
) {
    TODO()
}


/**
 * Test the cell using both active and passive verification strategies.
 */
fun testReactiveSystem(
    block: context(DynamicTestContext, CellVerificationContext) () -> Unit,
) {
    TODO()
}


context(dynamicTestContext: DynamicTestContext, cellVerificationContext: ActiveCellVerificationContext) fun <ValueT> spawnInertCell(
    tick: Tick,
    spawn: (MomentContext) -> Cell<ValueT>,
): Cell<ValueT> {
    TODO()
}

sealed class ObservationMode {
    data object Immediate : ObservationMode()

    data class Delayed(
        val observationTick: Tick,
    ) : ObservationMode()
}

sealed class ExpectedUpdatedValue<out ValueT> {
    data object None : ExpectedUpdatedValue<Nothing>()

    data class Some<ValueT>(
        val expectedUpdatedValue: ValueT,
    ) : ExpectedUpdatedValue<ValueT>()
}

data class ExpectedInitialValue<ValueT>(
    val expectedInitialValue: ValueT,
)


context(dynamicTestContext: DynamicTestContext) fun <ValueT> verifyCellFreezes(
    spawnTick: Tick,
    spawn: (MomentContext) -> Cell<ValueT>,
    observationMode: ObservationMode = ObservationMode.Immediate,
    expectedFreezeTick: Tick,
): ReactiveVerifier {
    TODO()
}

context(dynamicTestContext: DynamicTestContext) fun <ValueT> verifyCellUpdatesFreezing(
    spawnTick: Tick,
    spawn: (MomentContext) -> Cell<ValueT>,
    observationMode: ObservationMode = ObservationMode.Immediate,
    expectedFreezingUpdateTick: Tick,
    expectedUpdatedValue: ValueT,
): ReactiveVerifier {
    TODO()
}


context(dynamicTestContext: DynamicTestContext) fun <ValueT> verifyCellUpdates(
    spawnTick: Tick,
    spawn: (MomentContext) -> Cell<ValueT>,
    observationMode: ObservationMode = ObservationMode.Immediate,
    expectedUpdateTick: Tick,
    expectedUpdatedValue: ValueT,
): ReactiveVerifier {
    TODO()
}

context(dynamicTestContext: DynamicTestContext, cellVerificationContext: CellVerificationContext) fun <ValueT> spawnObservedDynamicCell(
    tick: Tick,
    spawn: (MomentContext) -> Cell<ValueT>,
    block: context(CellObservationContext<ValueT>) () -> Unit,
) {

}

context(dynamicTestContext: DynamicTestContext, cellVerificationContext: CellVerificationContext) fun <ValueT : Any> spawnObservedDynamicCellVerified(
    tick: Tick,
    spawn: (MomentContext) -> Cell<ValueT>,
    expectedInitialValue: ValueT,
    expectedUpdatedValue: ValueT? = null,
    block: context(CellObservationContext<ValueT>) () -> Unit,
) {

}

context(dynamicTestContext: DynamicTestContext) fun fastForward(
    tick: Tick,
) {

}


context(dynamicTestContext: DynamicTestContext, cellObservationContext: CellObservationContext<ValueT>) fun <ValueT : Any> verifyUpdate(
    tick: Tick,
    expectedUpdatedValue: ValueT?,
    shouldExpectFreeze: Boolean,
) {

}

/**
 * Test the cell using only active verification strategies.
 */
fun testCellActively(
    block: context(DynamicTestContext, ActiveCellVerificationContext) () -> Unit,
) {

}

fun <ConfigurationT : Any, ValueT : Any> testCell_initiallyDynamic(
    configure: context(DynamicTestContext, MomentContext) () -> ConfigurationT,
    spawnCell: context(MomentContext) ConfigurationT.() -> Cell<ValueT>,
    stimulationTick: Tick = Tick(0),
    expectedInitialValue: ValueT,
    expectedNotificationByTick: Map<Tick, Cell.Notification<ValueT>>,
) {
    testCell_initiallyDynamic(
        spawnSubject = {
            MomentContext.execute {
                val setup = configure()

                setup.spawnCell()
            }
        },
        expectedInitialValue = expectedInitialValue,
        expectedNotificationByTick = expectedNotificationByTick,
    )
}

fun <ValueT : Any> testCell_immediatelyInert(
    spawnSubject: () -> Cell<ValueT>,
    expectedValue: ValueT,
) {
    val subjectCell = spawnSubject()

    val sampledInitialValue = subjectCell.sampleExternally()

    assertEquals(
        expected = expectedValue,
        actual = sampledInitialValue,
    )

    assertNull(
        actual = subjectCell.observe(
            observer = object : Cell.Observer<ValueT> {
                override fun handleNotification(
                    notification: Cell.Notification<ValueT>,
                ) {
                }
            },
        ),
        message = "Expected a null observation for an inert cell",
    )
}
