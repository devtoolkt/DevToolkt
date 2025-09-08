package dev.toolkt.core.iterable

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class IndexOfVariantsTests {
    @Test
    fun testIndexOfOrNull_found() {
        val list = listOf("a", "b", "c")
        val iterable: Iterable<String> = list
        val sequence: Sequence<String> = list.asSequence()

        assertEquals(
            expected = 0,
            actual = list.indexOfOrNull("a"),
        )

        assertEquals(
            expected = 0,
            actual = iterable.indexOfOrNull("a"),
        )

        assertEquals(
            expected = 0,
            actual = sequence.indexOfOrNull("a"),
        )

        assertEquals(
            expected = 2,
            actual = list.indexOfOrNull("c"),
        )

        assertEquals(
            expected = 2,
            actual = iterable.indexOfOrNull("c"),
        )

        assertEquals(
            expected = 2,
            actual = sequence.indexOfOrNull("c"),
        )
    }

    @Test
    fun testIndexOfOrNull_notFound() {
        val list = listOf("a", "b", "c")
        val iterable: Iterable<String> = list
        val sequence: Sequence<String> = list.asSequence()

        assertNull(
            list.indexOfOrNull("d"),
        )

        assertNull(
            iterable.indexOfOrNull("d"),
        )

        assertNull(
            sequence.indexOfOrNull("d"),
        )
    }

    @Test
    fun testIndexOfOrNull_emptyList() {
        val list = emptyList<String>()
        val iterable: Iterable<String> = list
        val sequence: Sequence<String> = list.asSequence()

        assertNull(
            list.indexOfOrNull("a"),
        )

        assertNull(
            iterable.indexOfOrNull("a"),
        )

        assertNull(
            sequence.indexOfOrNull("a"),
        )
    }
}
