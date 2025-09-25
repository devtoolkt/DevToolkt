package dev.toolkt.reactive.cell

import dev.toolkt.reactive.cell.test_utils.CellSetup
import dev.toolkt.reactive.cell.test_utils.SamplingStrategy
import dev.toolkt.reactive.cell.test_utils.testSampleInitial
import kotlin.test.Test

@Suppress("ClassName")
class Cell_map_sampleInitial_tests {
    private val testedSetup_nonConst = CellSetup.MapToStringCellSetup.configure(
        sourceSetup = CellSetup.NonConstCellSetup.configure(
            value = 10,
        ),
    )

    private val testedSetup_const = CellSetup.MapToStringCellSetup.configure(
        sourceSetup = CellSetup.ConstCellSetup.configure(
            constValue = 10,
        ),
    )

    private val expectedInitialValue = "10"

    @Test
    fun test_sampleInitial_inactive() {
        val strategy = SamplingStrategy.Inactive<String>()

        testedSetup_nonConst.testSampleInitial(
            strategy = strategy,
            expectedInitialValue = expectedInitialValue,
        )

        testedSetup_const.testSampleInitial(
            strategy = strategy,
            expectedInitialValue = expectedInitialValue,
        )
    }

    @Test
    fun test_sampleInitial_active() {
        val strategy = SamplingStrategy.Active<String>()

        testedSetup_nonConst.testSampleInitial(
            strategy = strategy,
            expectedInitialValue = expectedInitialValue,
        )

        testedSetup_const.testSampleInitial(
            strategy = strategy,
            expectedInitialValue = expectedInitialValue,
        )
    }
}
