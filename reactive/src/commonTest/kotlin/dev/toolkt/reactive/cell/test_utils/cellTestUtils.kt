package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.sample
import kotlin.test.assertEquals

fun <ValueT> Cell<ValueT>.sampleExternally(): ValueT = MomentContext.execute {
    sample()
}

fun <ValueT> Cell<ValueT>.testSample(
    approach: SamplingStrategy<ValueT>,
    expectedInitialValue: ValueT,
) {
    approach.prepare(
        subjectCell = this,
    )

    val sampledInitialValue = MomentContext.execute {
        sample()
    }

    assertEquals(
        expected = expectedInitialValue,
        actual = sampledInitialValue,
    )
}
