package dev.toolkt.reactive.cell

import dev.toolkt.reactive.cell.test_utils.sampleExternally
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("ClassName")
class Cell_map_const_tests {
    @Test
    fun test_constSource() {
        val mapCell = Cell.of(10).map {
            it.toString()
        }

        assertEquals(
            expected = "10",
            actual = mapCell.sampleExternally(),
        )
    }
}
