package dev.toolkt.reactive.cell.test_utils.timeline

interface BaseNamedTick

val BaseNamedTick.ordinalTick: RawTick
    get() {
        val self = this as Enum<*>
        return RawTick(t = self.ordinal)
    }
