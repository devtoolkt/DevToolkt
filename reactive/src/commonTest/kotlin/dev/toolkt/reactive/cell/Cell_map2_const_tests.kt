package dev.toolkt.reactive.cell

import dev.toolkt.reactive.cell.test_utils.sampleExternally
import dev.toolkt.reactive.event_stream.subscribeCollecting
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("ClassName")
class Cell_map2_const_tests {
    @Test
    fun test_allConstSources() {
        val source1 = Cell.of(
            value = 10,
        )

        val source2 = Cell.of(
            value = 'A',
        )

        val map2Cell = Cell.map2(
            source1,
            source2,
        ) { value1, value2 ->
            "$value1:$value2"
        }

        assertEquals(
            expected = "10:A",
            actual = map2Cell.sampleExternally(),
        )
    }

    @Test
    fun test_constSource1() {
        val source1 = Cell.of(
            value = 10,
        )

        val source2 = MutableCell(
            initialValue = 'A',
        )

        val map2Cell = Cell.map2(
            source1,
            source2,
        ) { value1, value2 ->
            "$value1:$value2"
        }

        val collectedEvents = mutableListOf<String>()

        map2Cell.newValues.subscribeCollecting(
            targetList = collectedEvents,
        )

        assertEquals(
            expected = "10:A",
            actual = map2Cell.sampleExternally(),
        )

        source2.set('B')

        assertEquals(
            expected = "10:B",
            actual = map2Cell.sampleExternally(),
        )

        assertEquals(
            expected = listOf("10:B"),
            actual = collectedEvents,
        )
    }

    @Test
    fun test_constSource2() {
        val source1 = MutableCell(
            initialValue = 10,
        )

        val source2 = Cell.of(
            value = 'B',
        )

        val map2Cell = Cell.map2(
            source1,
            source2,
        ) { value1, value2 ->
            "$value1:$value2"
        }

        val collectedEvents = mutableListOf<String>()

        map2Cell.newValues.subscribeCollecting(
            targetList = collectedEvents,
        )

        assertEquals(
            expected = "10:B",
            actual = map2Cell.sampleExternally(),
        )

        source1.set(11)

        assertEquals(
            expected = "11:B",
            actual = map2Cell.sampleExternally(),
        )

        assertEquals(
            expected = listOf("11:B"),
            actual = collectedEvents,
        )
    }
}
