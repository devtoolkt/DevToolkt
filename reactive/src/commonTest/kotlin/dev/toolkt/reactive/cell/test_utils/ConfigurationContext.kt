package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.mapNotNull
import dev.toolkt.reactive.event_stream.take

class ConfigurationContext(
    private val momentContext: MomentContext,
    private val ticker: Ticker,
) {
    private var isClosed = false

    private var tMax = 0

    fun recordTick(
        tick: Tick,
    ) {
        require(!isClosed) {
            "ConfigurationContext is already closed."
        }

        if (tick.t > tMax) {
            tMax = tick.t
        }
    }

    fun getMomentContext(): MomentContext {
        require(!isClosed) {
            "ConfigurationContext is already closed."
        }

        return momentContext
    }

    fun getTicker(): Ticker {
        require(!isClosed) {
            "ConfigurationContext is already closed."
        }

        return ticker
    }

    fun close() {
        isClosed = true
    }

    fun getMaxRecordedTick(): Tick {
        require(!isClosed) {
            "ConfigurationContext is already closed."
        }

        return Tick(t = tMax)
    }
}

fun <ValueT : Any> defineConstCell(
    constValue: ValueT,
): Cell<ValueT> = Cell.of(constValue)

context(configurationContext: ConfigurationContext) fun <ValueT : Any> defineDynamicCell(
    initialValue: ValueT,
    updatedValueByTick: Map<TickAlike, ValueT>,
    freezeTick: TickAlike?,
): Cell<ValueT> {
    val onTick = configurationContext.getTicker().onTick
    val properFreezeTick = freezeTick?.asTick

    updatedValueByTick.forEach { (tickAlike, _) ->
        configurationContext.recordTick(
            tick = tickAlike.asTick,
        )
    }

    val updatedValueByProperTick = updatedValueByTick.entries.associate {  (tickAlike, updatedValue) ->
        tickAlike.asTick to updatedValue
    }

    freezeTick?.let {
        configurationContext.recordTick(
            tick = it.asTick,
        )
    }

    val onTickCropped = when (properFreezeTick) {
        null -> onTick

        else -> MomentContext.execute {
            onTick.take(properFreezeTick.t + 1)
        }
    }

    val newValues = onTickCropped.mapNotNull { tick ->
        updatedValueByProperTick[tick]
    }

    return with(configurationContext.getMomentContext()) {
        Cell.define(
            initialValue = initialValue,
            newValues = newValues,
        )
    }
}
