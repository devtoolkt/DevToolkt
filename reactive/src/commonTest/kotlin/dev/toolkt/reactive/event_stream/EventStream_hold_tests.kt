package dev.toolkt.reactive.event_stream

import dev.toolkt.core.utils.iterable.copyAndClear
import dev.toolkt.reactive.cell.test_utils.NewValuesExtractor
import dev.toolkt.reactive.cell.test_utils.UpdatedValuesExtractor
import dev.toolkt.reactive.cell.test_utils.ValueEventStreamExtractor
import dev.toolkt.reactive.cell.test_utils.energize
import dev.toolkt.reactive.cell.test_utils.sampleExternally
import dev.toolkt.reactive.test_utils.ReactiveTest
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("ClassName")
class EventStream_hold_tests {
    private fun setup(
        initialValue: Int,
    ): Pair<Cell<Int>, ReactiveTest<Int>> = ReactiveTest.setup {
        val sourceEventStream = formEventStream()

        sourceEventStream.hold(
            initialValue = initialValue,
        )
    }

    @Test
    fun test_sample_initial_deEnergized() {
        val (holdCell, _) = setup(
            initialValue = 0,
        )

        assertEquals(
            expected = 0,
            actual = holdCell.sampleExternally(),
        )
    }

    @Test
    fun test_sample_initial_energized() {
        val (holdCell, _) = setup(
            initialValue = 1,
        )

        holdCell.energize()

        assertEquals(
            expected = 1,
            actual = holdCell.sampleExternally(),
        )
    }

    @Test
    fun test_sample_subsequent_deEnergized() {
        val (holdCell, reactiveSystem) = setup(
            initialValue = 0,
        )

        reactiveSystem.stimulate(10)

        assertEquals(
            expected = 10,
            actual = holdCell.sampleExternally(),
        )
    }

    @Test
    fun test_sample_subsequent_energized() {
        val (holdCell, reactiveSystem) = setup(
            initialValue = 0,
        )

        holdCell.energize()

        reactiveSystem.stimulate(11)

        assertEquals(
            expected = 11,
            actual = holdCell.sampleExternally(),
        )
    }

    @Test
    fun test_sample_subsequent_afterDeEnergization() {
        val (holdCell, reactiveSystem) = setup(
            initialValue = 0,
        )

        val energization = holdCell.energize()

        reactiveSystem.stimulate(11)

        energization.cutOff()

        assertEquals(
            expected = 11,
            actual = holdCell.sampleExternally(),
        )
    }

    private fun test_updatePropagation(
        valueEventStreamExtractor: ValueEventStreamExtractor,
    ) {
        val (holdCell, reactiveSystem) = setup(
            initialValue = 0,
        )

        val collectedEvents = mutableListOf<Int>()

        val valueEventStream = valueEventStreamExtractor.extractValueEventStream(holdCell)

        valueEventStream.subscribeCollecting(
            targetList = collectedEvents,
        )

        reactiveSystem.stimulate(11)

        assertEquals(
            expected = listOf(11),
            actual = collectedEvents.copyAndClear(),
        )

        reactiveSystem.stimulate(12)

        assertEquals(
            expected = listOf(12),
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
        val (holdCell, reactiveSystem) = setup(
            initialValue = 0,
        )

        val collectedEvents = mutableListOf<Int>()

        val valueEventStream = valueEventStreamExtractor.extractValueEventStream(holdCell)

        val subscription = valueEventStream.subscribeCollecting(
            targetList = collectedEvents,
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
