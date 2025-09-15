package dev.toolkt.reactive.cell

import dev.toolkt.core.utils.iterable.copyAndClear
import dev.toolkt.reactive.cell.test_utils.NewValuesExtractor
import dev.toolkt.reactive.cell.test_utils.UpdatedValuesExtractor
import dev.toolkt.reactive.cell.test_utils.ValueEventStreamExtractor
import dev.toolkt.reactive.cell.test_utils.energize
import dev.toolkt.reactive.cell.test_utils.sampleExternally
import dev.toolkt.reactive.event_stream.subscribeCollecting
import dev.toolkt.reactive.test_utils.ReactiveTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Suppress("ClassName")
class Cell_map_tests {
    private fun setup(
        initialSourceValue: Int,
    ): Pair<Cell<String>, ReactiveTest<Int>> = ReactiveTest.setup {
        val inputCell = formCell(
            initialValue = initialSourceValue,
        )

        inputCell.map { it.toString() }
    }

    @Test
    fun test_sample_initial_deEnergized() {
        val (mapCell, _) = setup(
            initialSourceValue = 0,
        )

        assertEquals(
            expected = "0",
            actual = mapCell.sampleExternally(),
        )
    }

    @Test
    fun test_sample_initial_energized() {
        val (mapCell, _) = setup(
            initialSourceValue = 1,
        )

        mapCell.energize()

        assertEquals(
            expected = "1",
            actual = mapCell.sampleExternally(),
        )
    }

    @Test
    fun test_sample_subsequent_deEnergized() {
        val (mapCell, reactiveSystem) = setup(
            initialSourceValue = 0,
        )

        reactiveSystem.stimulate(10)

        assertEquals(
            expected = "10",
            actual = mapCell.sampleExternally(),
        )
    }

    @Test
    fun test_sample_subsequent_energized() {
        val (mapCell, reactiveSystem) = setup(
            initialSourceValue = 0,
        )

        mapCell.energize()

        reactiveSystem.stimulate(11)

        assertEquals(
            expected = "11",
            actual = mapCell.sampleExternally(),
        )
    }

    @Test
    fun test_sample_subsequent_afterDeEnergization() {
        val (mapCell, reactiveSystem) = setup(
            initialSourceValue = 0,
        )

        val energization = mapCell.energize()

        reactiveSystem.stimulate(11)

        energization.cutOff()

        assertEquals(
            expected = "11",
            actual = mapCell.sampleExternally(),
        )
    }

    private fun test_updatePropagation(
        valueEventStreamExtractor: ValueEventStreamExtractor,
    ) {
        val (mapCell, reactiveSystem) = setup(
            initialSourceValue = 0,
        )

        val collectedEvents = mutableListOf<String>()

        val valueEventStream = valueEventStreamExtractor.extractValueEventStream(mapCell)

        valueEventStream.subscribeCollecting(
            targetList = collectedEvents,
        )

        reactiveSystem.stimulate(11)

        assertEquals(
            expected = listOf("11"),
            actual = collectedEvents.copyAndClear(),
        )

        reactiveSystem.stimulate(12)

        assertEquals(
            expected = listOf("12"),
            actual = collectedEvents.copyAndClear(),
        )
    }

    @Test
    fun test_updatePropagation_newValues() {
        test_updatePropagation(
            valueEventStreamExtractor = NewValuesExtractor,
        )
    }

    @Test
    fun test_updatePropagation_updatedValues() {
        test_updatePropagation(
            valueEventStreamExtractor = UpdatedValuesExtractor,
        )
    }

    private fun test_updatePropagation_afterCancel(
        valueEventStreamExtractor: ValueEventStreamExtractor,
    ) {
        val (mapCell, reactiveSystem) = setup(
            initialSourceValue = 0,
        )

        val collectedEvents = mutableListOf<String>()

        val valueEventStream = valueEventStreamExtractor.extractValueEventStream(mapCell)

        val subscription = assertNotNull(
            valueEventStream.subscribeCollecting(
                targetList = collectedEvents,
            ),
        )

        reactiveSystem.stimulate(11)

        collectedEvents.clear()

        subscription.cancel()

        reactiveSystem.stimulate(12)

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
