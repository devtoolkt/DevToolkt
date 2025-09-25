package dev.toolkt.reactive.cell

import dev.toolkt.reactive.cell.test_utils.CellSetup
import dev.toolkt.reactive.cell.test_utils.SamplingStrategy
import dev.toolkt.reactive.cell.test_utils.testSampleNew
import kotlin.test.Test

@Suppress("ClassName")
class Cell_map_sampleNew_tests {
    private val testedSetup = CellSetup.MapToStringCellSetup.configure(
        initialSourceValue = 10,
        newSourceValue = 20,
    )

    private val expectedNewValue = "20"

    @Test
    fun test_sampleNew_inactive() {
        testedSetup.testSampleNew(
            strategy = SamplingStrategy.Inactive(),
            expectedNewValue = expectedNewValue,
        )
    }

    @Test
    fun test_sampleNew_active() {
        testedSetup.testSampleNew(
            strategy = SamplingStrategy.Active(),
            expectedNewValue = expectedNewValue,
        )
    }
}
