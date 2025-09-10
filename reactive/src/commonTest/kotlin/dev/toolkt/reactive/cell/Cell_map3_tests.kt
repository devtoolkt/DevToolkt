package dev.toolkt.reactive.cell

import dev.toolkt.reactive.cell.test_utils.NewValuesExtractor
import dev.toolkt.reactive.cell.test_utils.UpdatedValuesExtractor
import dev.toolkt.reactive.cell.test_utils.ValueEventStreamExtractor
import dev.toolkt.reactive.cell.test_utils.energize
import dev.toolkt.reactive.cell.test_utils.sampleExternally
import dev.toolkt.reactive.event_stream.Cell
import dev.toolkt.reactive.event_stream.subscribeCollecting
import dev.toolkt.reactive.test_utils.ReactiveTest
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("ClassName")
class Cell_map3_tests {
    private data class Stimulation(
        val newSourceValue1: Int? = null,
        val newSourceValue2: Char? = null,
        val newSourceValue3: Boolean? = null,
    )

    private fun setup(
        initialSourceValue1: Int,
        initialSourceValue2: Char,
        initialSourceValue3: Boolean,
    ): Pair<Cell<String>, ReactiveTest<Stimulation>> = ReactiveTest.setup {
        val sourceCell1 = extractCell(
            initialValue = initialSourceValue1,
            selector = Stimulation::newSourceValue1,
        )

        val sourceCell2 = extractCell(
            initialValue = initialSourceValue2,
            selector = Stimulation::newSourceValue2,
        )

        val sourceCell3 = extractCell(
            initialValue = initialSourceValue3,
            selector = Stimulation::newSourceValue3,
        )

        Cell.map3(
            cell1 = sourceCell1,
            cell2 = sourceCell2,
            cell3 = sourceCell3,
        ) { value1, value2, value3 ->
            "$value1:$value2:$value3"
        }
    }

    @Test
    fun test_sample_initial_deEnergized() {
        val (map3Cell, _) = setup(
            initialSourceValue1 = 0,
            initialSourceValue2 = 'A',
            initialSourceValue3 = false,
        )

        assertEquals(
            expected = "0:A:false",
            actual = map3Cell.sampleExternally(),
        )
    }

    @Test
    fun test_sample_initial_energized() {
        val (map3Cell, _) = setup(
            initialSourceValue1 = 1,
            initialSourceValue2 = 'B',
            initialSourceValue3 = true,
        )
        map3Cell.energize()
        assertEquals(
            expected = "1:B:true",
            actual = map3Cell.sampleExternally(),
        )
    }

    private fun test_sample_subsequent1(
        shouldEnergize: Boolean,
    ) {
        val (map3Cell, reactiveSystem) = setup(
            initialSourceValue1 = 0,
            initialSourceValue2 = 'A',
            initialSourceValue3 = false,
        )

        if (shouldEnergize) {
            map3Cell.energize()
        }

        reactiveSystem.stimulate(
            Stimulation(
                newSourceValue1 = 2,
            ),
        )

        assertEquals(
            expected = "2:A:false",
            actual = map3Cell.sampleExternally(),
        )
    }

    @Test
    fun test_sample_subsequent1_deEnergized() {
        test_sample_subsequent1(
            shouldEnergize = false,
        )
    }

    @Test
    fun test_sample_subsequent1_energized() {
        test_sample_subsequent1(
            shouldEnergize = true,
        )
    }

    private fun test_sample_subsequent2(
        shouldEnergize: Boolean,
    ) {
        val (map3Cell, reactiveSystem) = setup(
            initialSourceValue1 = 0,
            initialSourceValue2 = 'A',
            initialSourceValue3 = false,
        )

        if (shouldEnergize) {
            map3Cell.energize()
        }

        reactiveSystem.stimulate(
            Stimulation(
                newSourceValue2 = 'C',
            ),
        )

        assertEquals(
            expected = "0:C:false",
            actual = map3Cell.sampleExternally(),
        )
    }

    @Test
    fun test_sample_subsequent2_deEnergized() {
        test_sample_subsequent2(
            shouldEnergize = false,
        )
    }

    @Test
    fun test_sample_subsequent2_energized() {
        test_sample_subsequent2(
            shouldEnergize = true,
        )
    }

    private fun test_sample_subsequent3(
        shouldEnergize: Boolean,
    ) {
        val (map3Cell, reactiveSystem) = setup(
            initialSourceValue1 = 0,
            initialSourceValue2 = 'A',
            initialSourceValue3 = false,
        )

        if (shouldEnergize) {
            map3Cell.energize()
        }

        reactiveSystem.stimulate(
            Stimulation(
                newSourceValue3 = true,
            ),
        )

        assertEquals(
            expected = "0:A:true",
            actual = map3Cell.sampleExternally(),
        )
    }

    @Test
    fun test_sample_subsequent3_deEnergized() {
        test_sample_subsequent3(
            shouldEnergize = false,
        )
    }

    @Test
    fun test_sample_subsequent3_energized() {
        test_sample_subsequent3(
            shouldEnergize = true,
        )
    }

    @Test
    fun test_sample_subsequent_afterDeEnergization() {
        val (map3Cell, reactiveSystem) = setup(
            initialSourceValue1 = 0,
            initialSourceValue2 = 'A',
            initialSourceValue3 = false,
        )

        val energization = map3Cell.energize()

        reactiveSystem.stimulate(
            Stimulation(
                newSourceValue1 = 1,
                newSourceValue2 = 'B',
                newSourceValue3 = true,
            ),
        )

        energization.cutOff()

        assertEquals(
            expected = "1:B:true",
            actual = map3Cell.sampleExternally(),
        )
    }

    private fun test_updatePropagation(
        valueEventStreamExtractor: ValueEventStreamExtractor,
        initialSourceValue1: Int,
        initialSourceValue2: Char,
        initialSourceValue3: Boolean,
        newSourceValue1: Int? = null,
        newSourceValue2: Char? = null,
        newSourceValue3: Boolean? = null,
        expectedValue: String,
    ) {
        val (map3Cell, reactiveSystem) = setup(
            initialSourceValue1 = initialSourceValue1,
            initialSourceValue2 = initialSourceValue2,
            initialSourceValue3 = initialSourceValue3,
        )

        val collectedEvents = mutableListOf<String>()

        val valueEventStream = valueEventStreamExtractor.extractValueEventStream(map3Cell)

        valueEventStream.subscribeCollecting(
            targetList = collectedEvents,
        )

        reactiveSystem.stimulate(
            Stimulation(
                newSourceValue1 = newSourceValue1,
                newSourceValue2 = newSourceValue2,
                newSourceValue3 = newSourceValue3,
            ),
        )

        assertEquals(
            expected = listOf(expectedValue),
            actual = collectedEvents,
        )
    }

    @Test
    fun test_updatePropagation_source1Update_newValues() {
        test_updatePropagation(
            valueEventStreamExtractor = NewValuesExtractor,
            initialSourceValue1 = 0,
            initialSourceValue2 = 'A',
            initialSourceValue3 = false,
            newSourceValue1 = 1,
            expectedValue = "1:A:false",
        )
    }

    @Test
    fun test_updatePropagation_source1Update_updatedValues() {
        test_updatePropagation(
            valueEventStreamExtractor = UpdatedValuesExtractor,
            initialSourceValue1 = 0,
            initialSourceValue2 = 'A',
            initialSourceValue3 = false,
            newSourceValue1 = 1,
            expectedValue = "1:A:false",
        )
    }

    @Test
    fun test_updatePropagation_source2Update_newValues() {
        test_updatePropagation(
            valueEventStreamExtractor = NewValuesExtractor,
            initialSourceValue1 = 10,
            initialSourceValue2 = 'X',
            initialSourceValue3 = true,
            newSourceValue2 = 'Y',
            expectedValue = "10:Y:true",
        )
    }

    @Test
    fun test_updatePropagation_source2Update_updatedValues() {
        test_updatePropagation(
            valueEventStreamExtractor = UpdatedValuesExtractor,
            initialSourceValue1 = 10,
            initialSourceValue2 = 'X',
            initialSourceValue3 = true,
            newSourceValue2 = 'Y',
            expectedValue = "10:Y:true",
        )
    }

    @Test
    fun test_updatePropagation_source3Update_newValues() {
        test_updatePropagation(
            valueEventStreamExtractor = NewValuesExtractor,
            initialSourceValue1 = 5,
            initialSourceValue2 = 'Q',
            initialSourceValue3 = false,
            newSourceValue3 = true,
            expectedValue = "5:Q:true",
        )
    }

    @Test
    fun test_updatePropagation_source3Update_updatedValues() {
        test_updatePropagation(
            valueEventStreamExtractor = UpdatedValuesExtractor,
            initialSourceValue1 = 5,
            initialSourceValue2 = 'Q',
            initialSourceValue3 = false,
            newSourceValue3 = true,
            expectedValue = "5:Q:true",
        )
    }

    private fun test_updatePropagation_afterCancel(
        valueEventStreamExtractor: ValueEventStreamExtractor,
    ) {
        val (map3Cell, reactiveSystem) = setup(
            initialSourceValue1 = 0,
            initialSourceValue2 = 'A',
            initialSourceValue3 = false,
        )

        val collectedEvents = mutableListOf<String>()

        val valueEventStream = valueEventStreamExtractor.extractValueEventStream(map3Cell)

        val subscription = valueEventStream.subscribeCollecting(
            targetList = collectedEvents,
        )

        reactiveSystem.stimulate(
            Stimulation(
                newSourceValue1 = 1,
                newSourceValue2 = 'B',
                newSourceValue3 = true,
            ),
        )

        collectedEvents.clear()

        subscription.cancel()

        reactiveSystem.stimulate(
            Stimulation(
                newSourceValue1 = 2,
                newSourceValue2 = 'C',
                newSourceValue3 = false,
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
