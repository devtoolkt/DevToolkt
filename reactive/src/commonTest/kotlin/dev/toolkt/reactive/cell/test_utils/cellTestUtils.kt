package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.sample

fun <ValueT> Cell<ValueT>.sampleExternally(): ValueT = MomentContext.execute {
    sample()
}
