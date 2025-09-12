package dev.toolkt.reactive.cell

import dev.toolkt.reactive.cell.test_utils.NewValuesExtractor
import dev.toolkt.reactive.cell.test_utils.UpdatedValuesExtractor
import dev.toolkt.reactive.cell.test_utils.ValueEventStreamExtractor
import dev.toolkt.reactive.cell.test_utils.energize
import dev.toolkt.reactive.cell.test_utils.sampleExternally
import dev.toolkt.reactive.event_stream.Cell
import dev.toolkt.reactive.event_stream.subscribeCollecting
import dev.toolkt.reactive.test_utils.ReactiveTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

@Ignore // TODO: Implement this logic
@Suppress("ClassName")
class Cell_map2_tests {
    private data class Stimulation(
        val newSourceValue1: Int? = null,
        val newSourceValue2: Char? = null,
    )

    private fun setup(
        initialSourceValue1: Int,
        initialSourceValue2: Char,
    ): Pair<Cell<String>, ReactiveTest<Stimulation>> = ReactiveTest.setup {
        val sourceCell1 = extractCell(
            initialValue = initialSourceValue1,
            selector = Stimulation::newSourceValue1,
        )

        val sourceCell2 = extractCell(
            initialValue = initialSourceValue2,
            selector = Stimulation::newSourceValue2,
        )

        Cell.map2(
            cell1 = sourceCell1,
            cell2 = sourceCell2,
        ) { value1, value2 ->
            "$value1:$value2"
        }
    }

    @Test
    fun test_sample_initial_deEnergized() {
        val (map2Cell, _) = setup(
            initialSourceValue1 = 0,
            initialSourceValue2 = 'A',
        )

        assertEquals(
            expected = "0:A",
            actual = map2Cell.sampleExternally(),
        )
    }

    @Test
    fun test_sample_initial_energized() {
        val (map2Cell, _) = setup(
            initialSourceValue1 = 1,
            initialSourceValue2 = 'B',
        )

        map2Cell.energize()

        assertEquals(
            expected = "1:B",
            actual = map2Cell.sampleExternally(),
        )
    }

    private fun test_sample_subsequent1(
        shouldEnergize: Boolean,
    ) {
        val (map2Cell, reactiveSystem) = setup(
            initialSourceValue1 = 0,
            initialSourceValue2 = 'A',
        )

        if (shouldEnergize) {
            map2Cell.energize()
        }

        reactiveSystem.stimulate(
            Stimulation(
                newSourceValue1 = 1,
            ),
        )

        assertEquals(
            expected = "1:A",
            actual = map2Cell.sampleExternally(),
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
        val (map2Cell, reactiveSystem) = setup(
            initialSourceValue1 = 0,
            initialSourceValue2 = 'A',
        )

        if (shouldEnergize) {
            map2Cell.energize()
        }

        reactiveSystem.stimulate(
            Stimulation(
                newSourceValue2 = 'B',
            ),
        )

        assertEquals(
            expected = "0:B",
            actual = map2Cell.sampleExternally(),
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

    @Test
    fun test_sample_subsequent_afterDeEnergization() {
        val (map2Cell, reactiveSystem) = setup(
            initialSourceValue1 = 0,
            initialSourceValue2 = 'A',
        )

        val energization = map2Cell.energize()

        reactiveSystem.stimulate(
            Stimulation(
                newSourceValue1 = 1,
                newSourceValue2 = 'B',
            ),
        )

        energization.cutOff()

        assertEquals(
            expected = "1:B",
            actual = map2Cell.sampleExternally(),
        )
    }

    private fun test_updatePropagation(
        valueEventStreamExtractor: ValueEventStreamExtractor,
        initialSourceValue1: Int,
        initialSourceValue2: Char,
        newSourceValue1: Int? = null,
        newSourceValue2: Char? = null,
        expectedValue: String,
    ) {
        val (map2Cell, reactiveSystem) = setup(
            initialSourceValue1 = initialSourceValue1,
            initialSourceValue2 = initialSourceValue2,
        )

        val collectedEvents = mutableListOf<String>()

        val valueEventStream = valueEventStreamExtractor.extractValueEventStream(map2Cell)

        valueEventStream.subscribeCollecting(
            targetList = collectedEvents,
        )

        reactiveSystem.stimulate(
            Stimulation(
                newSourceValue1 = newSourceValue1,
                newSourceValue2 = newSourceValue2,
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
            newSourceValue1 = 1,
            expectedValue = "1:A",
        )
    }

    @Test
    fun test_updatePropagation_source1Update_updatedValues() {
        test_updatePropagation(
            valueEventStreamExtractor = UpdatedValuesExtractor,
            initialSourceValue1 = 0,
            initialSourceValue2 = 'A',
            newSourceValue1 = 1,
            expectedValue = "1:A",
        )
    }

    @Test
    fun test_updatePropagation_source2Update_newValues() {
        test_updatePropagation(
            valueEventStreamExtractor = NewValuesExtractor,
            initialSourceValue1 = 10,
            initialSourceValue2 = 'X',
            newSourceValue2 = 'Y',
            expectedValue = "10:Y",
        )
    }

    @Test
    fun test_updatePropagation_source2Update_updatedValues() {
        test_updatePropagation(
            valueEventStreamExtractor = NewValuesExtractor,
            initialSourceValue1 = 10,
            initialSourceValue2 = 'X',
            newSourceValue2 = 'Y',
            expectedValue = "10:Y",
        )
    }

    private fun test_updatePropagation_afterCancel(
        valueEventStreamExtractor: ValueEventStreamExtractor,
    ) {
        val (map2Cell, reactiveSystem) = setup(
            initialSourceValue1 = 0,
            initialSourceValue2 = 'A',
        )

        val collectedEvents = mutableListOf<String>()

        val valueEventStream = valueEventStreamExtractor.extractValueEventStream(map2Cell)

        val subscription = valueEventStream.subscribeCollecting(
            targetList = collectedEvents,
        )

        reactiveSystem.stimulate(
            Stimulation(
                newSourceValue1 = 1,
                newSourceValue2 = 'B',
            ),
        )

        collectedEvents.clear()

        subscription.cancel()

        reactiveSystem.stimulate(
            Stimulation(
                newSourceValue1 = 2,
                newSourceValue2 = 'C',
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
