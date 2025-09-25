package dev.toolkt.reactive.cell

import dev.toolkt.reactive.cell.test_utils.CellSetup
import dev.toolkt.reactive.cell.test_utils.SamplingStrategy
import dev.toolkt.reactive.cell.test_utils.testSample
import kotlin.test.Test

@Suppress("ClassName")
class Cell_switch_sampleInitial_tests {
    private val testedSetup_nonConst = CellSetup.SwitchCellSetups.Switching.configure(
        initialInnerCellSetup = CellSetup.MapToStringCellSetup.configure(
            initialSourceValue = -10,
        ),
    )

    private val testedSetup_constOuter = CellSetup.SwitchCellSetups.Const.configure(
        constInnerCellSetup = CellSetup.MapToStringCellSetup.configure(
            initialSourceValue = -10,
        ),
    )

    private val testedSetup_constInner = CellSetup.SwitchCellSetups.Const.configure(
        constInnerCellSetup = CellSetup.ConstCellSetup.configure(
            constValue = "-10",
        ),
    )

    private val expectedInitialValue = "-10"

    @Test
    fun test_sampleInitial_inactive() {
        testedSetup_nonConst.testSample(
            strategy = SamplingStrategy.Inactive(),
            expectedInitialValue = expectedInitialValue,
        )

        testedSetup_constOuter.testSample(
            strategy = SamplingStrategy.Inactive(),
            expectedInitialValue = expectedInitialValue,
        )

        testedSetup_constInner.testSample(
            strategy = SamplingStrategy.Inactive(),
            expectedInitialValue = expectedInitialValue,
        )
    }

    @Test
    fun test_sampleInitial_active() {
        testedSetup_nonConst.testSample(
            strategy = SamplingStrategy.Active(),
            expectedInitialValue = expectedInitialValue,
        )
    }
}
