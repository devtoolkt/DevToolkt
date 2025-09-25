package dev.toolkt.reactive.cell

import dev.toolkt.reactive.cell.test_utils.CellSetup
import dev.toolkt.reactive.cell.test_utils.SamplingStrategy
import dev.toolkt.reactive.cell.test_utils.testSampleInitial
import kotlin.test.Test

@Suppress("ClassName")
class Cell_map2_sampleInitial_tests {
    val value1 = 10
    val value2 = 'A'

    private val testedSetup_nonConst = CellSetup.Map2ConcatCellSetup.configure(
        source1Setup = CellSetup.NonConstCellSetup.configure(
            value = value1,
        ),
        source2Setup = CellSetup.NonConstCellSetup.configure(
            value = value2,
        ),
    )

    private val testedSetup_constFirst = CellSetup.Map2ConcatCellSetup.configure(
        source1Setup = CellSetup.NonConstCellSetup.configure(
            value = value1,
        ),
        source2Setup = CellSetup.ConstCellSetup.configure(
            constValue = value2,
        ),
    )

    private val testedSetup_constSecond = CellSetup.Map2ConcatCellSetup.configure(
        source1Setup = CellSetup.ConstCellSetup.configure(
            constValue = value1,
        ),
        source2Setup = CellSetup.NonConstCellSetup.configure(
            value = value2,
        ),
    )

    private val testedSetup_constBoth = CellSetup.Map2ConcatCellSetup.configure(
        source1Setup = CellSetup.ConstCellSetup.configure(
            constValue = value1,
        ),
        source2Setup = CellSetup.ConstCellSetup.configure(
            constValue = value2,
        ),
    )

    private val expectedInitialValue = "10:A"

    @Test
    fun test_sampleInitial_inactive() {
        val strategy = SamplingStrategy.Inactive<String>()

        testedSetup_nonConst.testSampleInitial(
            strategy = strategy,
            expectedInitialValue = expectedInitialValue,
        )

        testedSetup_constFirst.testSampleInitial(
            strategy = strategy,
            expectedInitialValue = expectedInitialValue,
        )

        testedSetup_constSecond.testSampleInitial(
            strategy = strategy,
            expectedInitialValue = expectedInitialValue,
        )

        testedSetup_constBoth.testSampleInitial(
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

        testedSetup_constFirst.testSampleInitial(
            strategy = strategy,
            expectedInitialValue = expectedInitialValue,
        )

        testedSetup_constSecond.testSampleInitial(
            strategy = strategy,
            expectedInitialValue = expectedInitialValue,
        )

        testedSetup_constBoth.testSampleInitial(
            strategy = strategy,
            expectedInitialValue = expectedInitialValue,
        )
    }
}
