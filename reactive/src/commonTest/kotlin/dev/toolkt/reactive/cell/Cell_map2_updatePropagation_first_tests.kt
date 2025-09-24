package dev.toolkt.reactive.cell

import dev.toolkt.reactive.cell.test_utils.CellSetup
import dev.toolkt.reactive.cell.test_utils.UpdatePropagationStrategy
import dev.toolkt.reactive.cell.test_utils.testUpdatePropagation
import dev.toolkt.reactive.cell.test_utils.testUpdatePropagationDeactivated
import kotlin.test.Test

@Suppress("ClassName")
class Cell_map2_updatePropagation_first_tests {
    private val testedSetup = CellSetup.Map2ConcatCellSetup.configure(
        initialSourceValue1 = 10,
        newSourceValue1 = 20,
        initialSourceValue2 = 'A',
    )

    private val expectedUpdatedValue = "20:A"

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
