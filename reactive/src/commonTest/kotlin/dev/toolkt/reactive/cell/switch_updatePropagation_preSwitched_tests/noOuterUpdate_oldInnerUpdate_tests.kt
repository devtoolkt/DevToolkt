package dev.toolkt.reactive.cell.switch_updatePropagation_preSwitched_tests

import dev.toolkt.reactive.cell.test_utils.CellSetup
import dev.toolkt.reactive.cell.test_utils.UpdatePropagationStrategy
import dev.toolkt.reactive.cell.test_utils.testUpdatePropagation
import dev.toolkt.reactive.cell.test_utils.testUpdatePropagationDeactivated
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore // FIXME: Fix issues with EventStream.merge2
@Suppress("ClassName")
class Cell_switch_updatePropagation_preSwitched_noOuterUpdate_oldInnerUpdate_tests {
    private val testedSetup = CellSetup.SwitchCellSetups.Switching.configure(
        initialInnerCellSetup = CellSetup.NonConstCellSetup.configure(
            value = "(initial)",
        ),
        intermediateInnerCellSetup = CellSetup.MapToStringCellSetup.configure(
            initialSourceValue = -10,
            newSourceValue = -20,
        ),
    )

    private val expectedUpdatedValue = "-20"

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
