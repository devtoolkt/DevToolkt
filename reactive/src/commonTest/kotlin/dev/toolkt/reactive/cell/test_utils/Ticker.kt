package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.event_stream.EmitterEventStream

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
