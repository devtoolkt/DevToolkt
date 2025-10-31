package dev.toolkt.reactive.cell.test_utils.timeline

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.event_stream.EmitterEventStream
import dev.toolkt.reactive.event_stream.mapAt
import dev.toolkt.reactive.event_stream.subscribe

class TimelineTicker {
    private var tCurrent = -1

    private val doEmitTick = EmitterEventStream<RawTick>()

    val onTick: EmitterEventStream<RawTick>
        get() = doEmitTick

    fun proceed(
        tick: RawTick,
    ) {
        proceed(tNext = tick.t)
    }

    private fun proceed(
        tNext: Int,
    ) {
        require(tNext == tCurrent + 1) {
            "Expected tick must be exactly one more than current tick"
        }

        println("Emit: $tNext")

        doEmitTick.emit(RawTick(t = tNext))

        tCurrent = tNext
    }

    fun fastForward(
        stopTick: RawTick,
    ) {
        require(stopTick.t > tCurrent) {
            "Stop tick (${stopTick.t}) must be later than current tick ($tCurrent)"
        }

        while (tCurrent < stopTick.t - 1) {
            proceed(tNext = tCurrent + 1)
        }
    }

    fun <ValueT : Any> evaluate(
        tick: RawTick,
        block: context(MomentContext) () -> ValueT,
    ): ValueT {
        val onEvaluated = onTick.mapAt {
            block()
        }

        var receivedValue: ValueT? = null

        val subscription = onEvaluated.subscribe { value ->
            receivedValue = value
        } ?: throw IllegalStateException("Unexpected null subscription")

        proceed(
            tick,
        )

        val properReceivedValue = receivedValue
            ?: throw IllegalStateException("Expected a value to be received after proceeding to tick ${tick.t}")

        subscription.cancel()

        return properReceivedValue
    }

}
