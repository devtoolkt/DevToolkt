package dev.toolkt.core.platform

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Suppress("ClassName")
class PlatformNativeMap_tests {
    @Test
    fun test_put() {
        val map = PlatformNativeMap<String, Int>()

        assertNull(
            actual = map.put(
                key = "a",
                value = 1,
            ),
        )

        assertEquals(
            expected = 1,
            actual = map.put(
                key = "a",
                value = 2,
            ),
        )

        assertEquals(
            expected = 2,
            actual = map["a"],
        )

        assertEquals(
            expected = 1,
            actual = map.size,
        )
    }


    @Test
    fun test_extract() {
        val map = PlatformNativeMap<String, Int>()

        map.put(
            key = "x",
            value = 42,
        )

        assertEquals(
            expected = 42,
            actual = map.extract("x"),
        )

        assertFalse(
            actual = map.containsKey("x"),
        )

        assertEquals(
            expected = 0,
            actual = map.size,
        )
    }

    @Test
    fun test_remove() {
        val map = PlatformNativeMap<String, Int>()

        map.put(
            key = "x",
            value = 42,
        )

        assertTrue(
            actual = map.remove("x"),
        )

        assertFalse(
            actual = map.containsKey("x"),
        )

        assertEquals(
            expected = 0,
            actual = map.size,
        )
    }

    @Test
    fun test_containsKey_and_get() {
        val map = PlatformNativeMap<Int, String>()

        map.put(
            key = 42,
            value = "foo",
        )

        assertTrue(
            actual = map.containsKey(42),
        )

        assertFalse(
            actual = map.containsKey(99),
        )

        assertEquals(
            expected = "foo",
            actual = map[42],
        )

        assertNull(
            actual = map[99],
        )
    }

    @Test
    fun test_clear() {
        val map = PlatformNativeMap<String, Int>()

        map.put(
            key = "a",
            value = 1,
        )

        map.put(
            key = "b",
            value = 2,
        )

        map.clear()

        assertEquals(
            expected = 0,
            actual = map.size,
        )

        assertFalse(
            actual = map.containsKey("a"),
        )

        assertFalse(
            actual = map.containsKey("b"),
        )
    }

    @Test
    fun test_forEach() {
        val map = PlatformNativeMap<Int, String>()

        map.put(
            key = 1,
            value = "one",
        )

        map.put(
            key = 2,
            value = "two",
        )

        val visitedEntries = mutableListOf<Pair<Int, String>>()

        map.forEach { key, value ->
            visitedEntries.add(key to value)
        }

        assertEquals(
            expected = map.size,
            actual = visitedEntries.size,
        )

        assertTrue(
            actual = visitedEntries.contains(1 to "one"),
        )

        assertTrue(
            actual = visitedEntries.contains(2 to "two"),
        )
    }

    @Test
    fun test_copy() {
        val map = PlatformNativeMap<String, Int>()

        map.put(
            key = "foo",
            value = 123,
        )

        val copiedMap = map.copy()

        assertEquals(
            expected = 123,
            actual = copiedMap["foo"],
        )

        assertEquals(
            expected = map.size,
            actual = copiedMap.size,
        )

        copiedMap.put(
            key = "bar",
            value = 456,
        )

        assertEquals(
            expected = 456,
            actual = copiedMap["bar"],
        )

        assertNull(
            actual = map["bar"],
        )
    }
}
