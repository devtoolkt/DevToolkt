package dev.toolkt.reactive.cell

import dev.toolkt.reactive.cell.test_utils.CellSetup
import dev.toolkt.reactive.cell.test_utils.UpdatePropagationStrategy
import dev.toolkt.reactive.cell.test_utils.testUpdatePropagation
import dev.toolkt.reactive.cell.test_utils.testUpdatePropagationDeactivated
import kotlin.test.Test

@Suppress("ClassName")
class Cell_map2_updatePropagation_constSources_tests {
    private val testedSetup_firstConst = CellSetup.Map2ConcatCellSetup.configure(
        source1Setup = CellSetup.ConstCellSetup.configure(
            constValue = 20,
        ),
        source2Setup = CellSetup.HoldCellSetup.configure(
            initialValue = 'A',
            newValue = 'B',
        ),
    )

    private val testedSetup_secondConst = CellSetup.Map2ConcatCellSetup.configure(
        source1Setup = CellSetup.HoldCellSetup.configure(
            initialValue = 10,
            newValue = 20,
        ),
        source2Setup = CellSetup.ConstCellSetup.configure(
            constValue = 'B',
        ),
    )

    private val expectedUpdatedValue = "20:B"

    @Test
    fun test_updatePropagation_updatedValues() {
        val strategy = UpdatePropagationStrategy.UpdatedValues<String>()

        testedSetup_firstConst.testUpdatePropagation(
            strategy = strategy,
            expectedUpdatedValue = expectedUpdatedValue,
        )

        testedSetup_secondConst.testUpdatePropagation(
            strategy = strategy,
            expectedUpdatedValue = expectedUpdatedValue,
        )
    }

    @Test
    fun test_updatePropagation_newValues() {
        val strategy = UpdatePropagationStrategy.NewValues<String>()

        testedSetup_firstConst.testUpdatePropagation(
            strategy = strategy,
            expectedUpdatedValue = expectedUpdatedValue,
        )

        testedSetup_secondConst.testUpdatePropagation(
            strategy = strategy,
            expectedUpdatedValue = expectedUpdatedValue,
        )
    }

    @Test
    fun test_updatePropagation_switch() {
        val strategy = UpdatePropagationStrategy.Switch<String>()

        testedSetup_firstConst.testUpdatePropagation(
            strategy = strategy,
            expectedUpdatedValue = expectedUpdatedValue,
        )

        testedSetup_secondConst.testUpdatePropagation(
            strategy = strategy,
            expectedUpdatedValue = expectedUpdatedValue,
        )
    }

    @Test
    fun test_updatePropagation_deactivated() {
        testedSetup_firstConst.testUpdatePropagationDeactivated()

        testedSetup_secondConst.testUpdatePropagationDeactivated()
    }
}
