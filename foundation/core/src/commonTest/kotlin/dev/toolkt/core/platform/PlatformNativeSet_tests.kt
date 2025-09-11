package dev.toolkt.core.platform

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Suppress("ClassName")
class PlatformNativeSet_tests {
    @Test
    fun test_add() {
        val set = PlatformNativeSet<String>()

        assertTrue(
            actual = set.add("a"),
        )

        assertFalse(
            actual = set.add("a"),
        )

        assertTrue(
            actual = "a" in set,
        )

        assertEquals(
            expected = 1,
            actual = set.size,
        )
    }

    @Test
    fun test_addIfAbsent() {
        val set = PlatformNativeSet<Int>()

        set.addIfAbsent(
            value = 1,
        )

        set.addIfAbsent(
            value = 1,
        )

        assertTrue(
            actual = 1 in set,
        )

        assertEquals(
            expected = 1,
            actual = set.size,
        )
    }

    @Test
    fun test_remove() {
        val set = PlatformNativeSet<String>()

        set.add(
            value = "x",
        )

        assertTrue(
            actual = set.remove("x"),
        )

        assertFalse(
            actual = set.remove("x"),
        )

        assertFalse(
            actual = "x" in set,
        )

        assertEquals(
            expected = 0,
            actual = set.size,
        )
    }

    @Test
    fun test_contains() {
        val set = PlatformNativeSet<Int>()

        set.add(
            value = 42,
        )

        assertTrue(
            actual = 42 in set,
        )

        assertFalse(
            actual = 99 in set,
        )
    }

    @Test
    fun test_clear() {
        val set = PlatformNativeSet<String>()

        set.add(
            value = "a",
        )

        set.add(
            value = "b",
        )

        set.clear()

        assertEquals(
            expected = 0,
            actual = set.size,
        )

        assertFalse(
            actual = "a" in set,
        )

        assertFalse(
            actual = "b" in set,
        )
    }

    @Test
    fun test_forEach() {
        val set = PlatformNativeSet<Int>()

        set.add(
            value = 1,
        )

        set.add(
            value = 2,
        )

        val visitedElements = mutableListOf<Int>()

        set.forEach { element ->
            visitedElements.add(element)
        }

        assertEquals(
            expected = set.size,
            actual = visitedElements.size,
        )

        assertTrue(
            actual = visitedElements.containsAll(listOf(1, 2)),
        )
    }

    @Test
    fun test_copy() {
        val set = PlatformNativeSet<String>()

        set.add(
            value = "foo",
        )

        val copiedSet = set.copy()

        assertTrue(
            actual = "foo" in copiedSet,
        )

        assertEquals(
            expected = set.size,
            actual = copiedSet.size,
        )

        copiedSet.add(
            value = "bar",
        )

        assertTrue(
            actual = "bar" in copiedSet,
        )

        assertFalse(
            actual = "bar" in set,
        )
    }
}
