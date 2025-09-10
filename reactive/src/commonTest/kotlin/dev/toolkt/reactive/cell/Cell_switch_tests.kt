package dev.toolkt.reactive.cell

import dev.toolkt.reactive.cell.test_utils.NewValuesExtractor
import dev.toolkt.reactive.cell.test_utils.UpdatedValuesExtractor
import dev.toolkt.reactive.cell.test_utils.ValueEventStreamExtractor
import dev.toolkt.reactive.cell.test_utils.energize
import dev.toolkt.reactive.cell.test_utils.sampleExternally
import dev.toolkt.reactive.event_stream.Cell
import dev.toolkt.reactive.event_stream.map
import dev.toolkt.reactive.event_stream.subscribeCollecting
import dev.toolkt.reactive.test_utils.ReactiveTest
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("ClassName")
class Cell_switch_tests {
    private enum class PathId {
        Path1, Path2, Path3,
    }

    private data class Stimulation(
        val newPathId: PathId? = null,
        val newInnerValue1: Int? = null,
        val newInnerValue2: Int? = null,
        val newInnerValue3: Int? = null,
    )

    private fun setup(
        initialInnerValue1: Int,
        initialInnerValue2: Int,
        initialInnerValue3: Int,
        initialPathId: PathId,
    ): Pair<Cell<Int>, ReactiveTest<Stimulation>> = ReactiveTest.setup {
        val innerCell1 = extractCell(
            initialValue = initialInnerValue1,
            selector = Stimulation::newInnerValue1,
        )

        val innerCell2 = extractCell(
            initialValue = initialInnerValue2,
            selector = Stimulation::newInnerValue1,
        )

        val innerCell3 = extractCell(
            initialValue = initialInnerValue3,
            selector = Stimulation::newInnerValue1,
        )

        val pathId = extractCell(
            initialValue = initialPathId,
            selector = Stimulation::newPathId,
        )

        val outerCell = pathId.map { pathIdNow ->
            when (pathIdNow) {
                PathId.Path1 -> innerCell1
                PathId.Path2 -> innerCell2
                PathId.Path3 -> innerCell3
            }
        }

        Cell.switch(
            outerCell = outerCell,
        )
    }

    @Test
    fun test_sample_initial_deEnergized() {
        val (switchCell, _) = setup(
            initialPathId = PathId.Path1,
            initialInnerValue1 = 10,
            initialInnerValue2 = 20,
            initialInnerValue3 = 30,
        )

        assertEquals(
            expected = 10,
            actual = switchCell.sampleExternally(),
        )
    }

    @Test
    fun test_sample_initial_energized() {
        val (switchCell, _) = setup(
            initialPathId = PathId.Path2,
            initialInnerValue1 = 10,
            initialInnerValue2 = 20,
            initialInnerValue3 = 30,
        )

        switchCell.energize()

        assertEquals(
            expected = 20,
            actual = switchCell.sampleExternally(),
        )
    }

    private fun test_sample_subsequent_afterOuterUpdate(
        shouldEnergize: Boolean,
    ) {
        val (switchCell, reactiveSystem) = setup(
            initialPathId = PathId.Path1,
            initialInnerValue1 = 10,
            initialInnerValue2 = 20,
            initialInnerValue3 = 30,
        )

        if (shouldEnergize) {
            switchCell.energize()
        }

        reactiveSystem.stimulate(
            Stimulation(
                newPathId = PathId.Path3,
            ),
        )

        assertEquals(
            expected = 30,
            actual = switchCell.sampleExternally(),
        )
    }

    @Test
    fun test_sample_subsequent_afterOuterUpdate_deEnergized() {
        test_sample_subsequent_afterOuterUpdate(
            shouldEnergize = false,
        )
    }

    @Test
    fun test_sample_subsequent_afterOuterUpdate_energized() {
        test_sample_subsequent_afterOuterUpdate(
            shouldEnergize = true,
        )
    }

    private fun test_sample_subsequent_afterInnerUpdate(
        shouldEnergize: Boolean,
    ) {
        val (switchCell, reactiveSystem) = setup(
            initialPathId = PathId.Path1,
            initialInnerValue1 = 10,
            initialInnerValue2 = 20,
            initialInnerValue3 = 30,
        )

        if (shouldEnergize) {
            switchCell.energize()
        }

        reactiveSystem.stimulate(
            Stimulation(
                newInnerValue1 = 11,
            ),
        )

        assertEquals(
            expected = 11,
            actual = switchCell.sampleExternally(),
        )
    }

    @Test
    fun test_sample_subsequent_afterInnerUpdate_deEnergized() {
        test_sample_subsequent_afterInnerUpdate(
            shouldEnergize = false,
        )
    }

    @Test
    fun test_sample_subsequent_afterInnerUpdate_energized() {
        test_sample_subsequent_afterInnerUpdate(
            shouldEnergize = true,
        )
    }


    private fun test_sample_subsequent_afterSimultaneousUpdate(
        shouldEnergize: Boolean,
    ) {
        val (switchCell, reactiveSystem) = setup(
            initialPathId = PathId.Path2,
            initialInnerValue1 = 10,
            initialInnerValue2 = 20,
            initialInnerValue3 = 30,
        )

        if (shouldEnergize) {
            switchCell.energize()
        }

        reactiveSystem.stimulate(
            Stimulation(
                newPathId = PathId.Path1,
                newInnerValue1 = 21,
            ),
        )

        assertEquals(
            expected = 11,
            actual = switchCell.sampleExternally(),
        )
    }

    @Test
    fun test_sample_subsequent_afterSimultaneousUpdate_deEnergized() {
        test_sample_subsequent_afterSimultaneousUpdate(
            shouldEnergize = false,
        )
    }

    @Test
    fun test_sample_subsequent_afterSimultaneousUpdate_energized() {
        test_sample_subsequent_afterSimultaneousUpdate(
            shouldEnergize = true,
        )
    }

    @Test
    fun test_sample_subsequent_afterDeEnergization() {
        val (switchCell, reactiveSystem) = setup(
            initialPathId = PathId.Path3,
            initialInnerValue1 = 10,
            initialInnerValue2 = 20,
            initialInnerValue3 = 30,
        )

        val energization = switchCell.energize()

        reactiveSystem.stimulate(
            Stimulation(
                newPathId = PathId.Path1,
                newInnerValue1 = 11,
                newInnerValue2 = 21,
                newInnerValue3 = 31,
            ),
        )

        energization.cutOff()

        assertEquals(
            expected = 11,
            actual = switchCell.sampleExternally(),
        )
    }

    private fun test_updatePropagation(
        valueEventStreamExtractor: ValueEventStreamExtractor,
        initialPathId: PathId,
        initialInnerValue1: Int,
        initialInnerValue2: Int,
        initialInnerValue3: Int,
        stimulation: Stimulation,
        expectedValue: Int,
    ) {
        val (switchCell, reactiveSystem) = setup(
            initialPathId = initialPathId,
            initialInnerValue1 = initialInnerValue1,
            initialInnerValue2 = initialInnerValue2,
            initialInnerValue3 = initialInnerValue3,
        )

        val collectedEvents = mutableListOf<Int>()

        val valueEventStream = valueEventStreamExtractor.extractValueEventStream(switchCell)

        valueEventStream.subscribeCollecting(
            targetList = collectedEvents,
        )

        reactiveSystem.stimulate(stimulation)

        assertEquals(
            expected = listOf(expectedValue),
            actual = collectedEvents,
        )
    }

    private fun test_updatePropagation_outerUpdate(
        valueEventStreamExtractor: ValueEventStreamExtractor,
    ) {
        test_updatePropagation(
            valueEventStreamExtractor = valueEventStreamExtractor,
            initialPathId = PathId.Path1,
            initialInnerValue1 = 10,
            initialInnerValue2 = 20,
            initialInnerValue3 = 30,
            stimulation = Stimulation(
                newPathId = PathId.Path3,
            ),
            expectedValue = 30,
        )
    }

    @Test
    fun test_updatePropagation_outerUpdate_newValues() {
        test_updatePropagation_outerUpdate(
            valueEventStreamExtractor = NewValuesExtractor,
        )
    }

    @Test
    fun test_updatePropagation_outerUpdate_updatedValues() {
        test_updatePropagation_outerUpdate(
            valueEventStreamExtractor = UpdatedValuesExtractor,
        )
    }

    private fun test_updatePropagation_innerUpdate(
        valueEventStreamExtractor: ValueEventStreamExtractor,
    ) {
        test_updatePropagation(
            valueEventStreamExtractor = valueEventStreamExtractor,
            initialPathId = PathId.Path2,
            initialInnerValue1 = 11,
            initialInnerValue2 = 21,
            initialInnerValue3 = 31,
            stimulation = Stimulation(
                newInnerValue2 = 22,
            ),
            expectedValue = 22,
        )
    }

    @Test
    fun test_updatePropagation_innerUpdate_newValues() {
        test_updatePropagation_innerUpdate(
            valueEventStreamExtractor = NewValuesExtractor,
        )
    }

    @Test
    fun test_updatePropagation_innerUpdate_updatedValues() {
        test_updatePropagation_innerUpdate(
            valueEventStreamExtractor = UpdatedValuesExtractor,
        )
    }

    private fun test_updatePropagation_simultaneousUpdate(
        valueEventStreamExtractor: ValueEventStreamExtractor,
    ) {
        test_updatePropagation(
            valueEventStreamExtractor = valueEventStreamExtractor,
            initialPathId = PathId.Path3,
            initialInnerValue1 = 10,
            initialInnerValue2 = 20,
            initialInnerValue3 = 30,
            stimulation = Stimulation(
                newPathId = PathId.Path1,
                newInnerValue1 = 11,
                newInnerValue3 = 31,
            ),
            expectedValue = 11,
        )
    }

    @Test
    fun test_updatePropagation_simultaneousUpdate_newValues() {
        test_updatePropagation_simultaneousUpdate(
            valueEventStreamExtractor = NewValuesExtractor,
        )
    }

    @Test
    fun test_updatePropagation_simultaneousUpdate_updatedValues() {
        test_updatePropagation_simultaneousUpdate(
            valueEventStreamExtractor = UpdatedValuesExtractor,
        )
    }

    private fun test_updatePropagation_afterCancel(
        valueEventStreamExtractor: ValueEventStreamExtractor,
    ) {
        val (switchCell, reactiveSystem) = setup(
            initialPathId = PathId.Path1,
            initialInnerValue1 = 10,
            initialInnerValue2 = 20,
            initialInnerValue3 = 30,
        )

        val collectedEvents = mutableListOf<Int>()

        val valueEventStream = valueEventStreamExtractor.extractValueEventStream(switchCell)

        val subscription = valueEventStream.subscribeCollecting(
            targetList = collectedEvents,
        )

        reactiveSystem.stimulate(
            Stimulation(
                newPathId = PathId.Path2,
                newInnerValue1 = 11,
                newInnerValue2 = 21,
                newInnerValue3 = 31,
            ),
        )

        collectedEvents.clear()

        subscription.cancel()

        reactiveSystem.stimulate(
            Stimulation(
                newPathId = PathId.Path3,
                newInnerValue1 = 12,
                newInnerValue2 = 22,
                newInnerValue3 = 32,
            ),
        )

        assertEquals(
            expected = emptyList(),
            actual = collectedEvents,
        )
    }

    @Test
    fun test_updatePropagation_afterCancel_newValues() {
        test_updatePropagation_afterCancel(
            valueEventStreamExtractor = NewValuesExtractor,
        )
    }

    @Test
    fun test_updatePropagation_afterCancel_updatedValues() {
        test_updatePropagation_afterCancel(
            valueEventStreamExtractor = UpdatedValuesExtractor,
        )
    }
}
