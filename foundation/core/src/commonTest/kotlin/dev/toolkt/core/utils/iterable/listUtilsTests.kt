package dev.toolkt.core.utils.iterable

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ListUtilsTests {
    @Test
    fun testIndexOfOrNull_found() {
        val list = listOf("a", "b", "c")

        assertEquals(
            expected = 0,
            actual = list.indexOfOrNull("a"),
        )

        assertEquals(
            expected = 2,
            actual = list.indexOfOrNull("c"),
        )
    }

    @Test
    fun testIndexOfOrNull_notFound() {
        val list = listOf("a", "b", "c")

        assertNull(
            list.indexOfOrNull("d"),
        )
    }

    @Test
    fun testIndexOfOrNull_emptyList() {
        val list = emptyList<String>()

        assertNull(
            list.indexOfOrNull("a"),
        )
    }
}
