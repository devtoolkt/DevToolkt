package dev.toolkt.core.utils.iterable

import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("ClassName")
class mapUtils_tests {
    @Test
    fun test_mapOfNotNull() {
        val map = mapOfNotNull(
            1 to "A",
            null,
            2 to "B",
            3 to "C",
            null,
        )

        assertEquals(
            expected = mapOf(
                1 to "A",
                2 to "B",
                3 to "C",
            ),
            actual = map,
        )
    }
}
