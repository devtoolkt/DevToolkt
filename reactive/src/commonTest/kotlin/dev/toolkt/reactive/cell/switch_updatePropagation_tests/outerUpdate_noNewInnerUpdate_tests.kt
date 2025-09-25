package dev.toolkt.reactive.cell.switch_updatePropagation_tests

import dev.toolkt.reactive.cell.test_utils.CellSetup
import dev.toolkt.reactive.cell.test_utils.UpdatePropagationStrategy
import dev.toolkt.reactive.cell.test_utils.testUpdatePropagation
import dev.toolkt.reactive.cell.test_utils.testUpdatePropagationDeactivated
import kotlin.test.Ignore
import kotlin.test.Test

@Suppress("ClassName")
class Cell_switch_updatePropagation_outerUpdate_noNewInnerUpdate_tests {
    private val testedSetup = CellSetup.SwitchCellSetups.Switching.configure(
        initialInnerCellSetup = CellSetup.MapToStringCellSetup.configure(
            initialSourceValue = -10,
            newSourceValue = -20,
        ),
        newInnerCellSetup = CellSetup.MapToStringCellSetup.configure(
            initialSourceValue = 10,
        ),
    )

    private val expectedUpdatedValue = "10"

    @Test
    fun test_updatePropagation_updatedValues() {
        testedSetup.testUpdatePropagation(
            strategy = UpdatePropagationStrategy.UpdatedValues(),
            expectedUpdatedValue = expectedUpdatedValue,
        )
    }

    @Test
    fun test_updatePropagation_newValues() {
        testedSetup.testUpdatePropagation(
            strategy = UpdatePropagationStrategy.NewValues(),
            expectedUpdatedValue = expectedUpdatedValue,
        )
    }

    @Test
    fun test_updatePropagation_switch() {
        testedSetup.testUpdatePropagation(
            strategy = UpdatePropagationStrategy.Switch(),
            expectedUpdatedValue = expectedUpdatedValue,
        )
    }

    @Test
    fun test_updatePropagation_deactivated() {
        testedSetup.testUpdatePropagationDeactivated()
    }
}
