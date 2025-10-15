package dev.toolkt.reactive.test_utils

import dev.toolkt.reactive.cell.test_utils.Tick
import dev.toolkt.reactive.event_stream.EventStream

interface DynamicTestContext {
    val onTick: EventStream<Tick>
}
