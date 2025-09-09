package dev.toolkt.js.collections

import dev.toolkt.js.Object
import kotlin.js.collections.JsMap
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JsMapUtilsTests {
    private val mapConstructor = js("Map")

    @Test
    fun testJsMapConstructorEmpty() {
        val map: JsMap<Int, String> = JsMap()

        assertEquals(
            expected = mapConstructor.prototype,
            actual = Object.getPrototypeOf(map),
        )

        assertEquals(
            expected = 0,
            actual = map.size,
        )

        assertFalse(
            actual = map.has(1),
        )

        val visitedEntries = mutableListOf<Pair<Int, String>>()

        map.forEach { k, v ->
            visitedEntries.add(k to v)
        }

        assertEquals(
            expected = emptyList(),
            actual = visitedEntries,
        )
    }

    @Test
    @Ignore // TODO: Add proper support for JsArrays
    fun testJsMapConstructorCopying() {
        val originalMap: JsMap<Int, String> = JsMap<Int, String>().apply {
            set(1, "one")
            set(2, "two")
            set(3, "three")
        }

        val map: JsMap<Int, String> = JsMap(originalMap)

        assertEquals(
            expected = mapConstructor.prototype,
            actual = Object.getPrototypeOf(map),
        )

        assertEquals(
            expected = 3,
            actual = map.size,
        )

        assertTrue(
            actual = map.has(1),
        )

        assertTrue(
            actual = map.has(2),
        )

        assertTrue(
            actual = map.has(3),
        )

        assertFalse(
            actual = map.has(4),
        )

        val visitedEntries = mutableListOf<Pair<Int, String>>()

        map.forEach { k, v ->
            visitedEntries.add(k to v)
        }

        assertEquals(
            expected = listOf(1 to "one", 2 to "two", 3 to "three"),
            actual = visitedEntries,
        )

        // Modify the copied map
        assertTrue(
            actual = map.delete(2),
        )

        // Ensure that the original map is not affected
        assertTrue(
            actual = originalMap.has(2),
        )
    }

    @Test
    @Ignore // TODO: Add proper support for JsArrays
    fun testJsMapSet() {
        val map: JsMap<Int, String> = JsMap()

        map.set(1, "one")
        map.set(2, "two")
        map.set(3, "three")

        assertEquals(
            expected = 3,
            actual = map.size,
        )

        assertTrue(
            actual = map.has(1),
        )

        assertTrue(
            actual = map.has(2),
        )

        assertTrue(
            actual = map.has(3),
        )

        assertFalse(
            actual = map.has(4),
        )

        assertEquals(
            expected = "one",
            actual = map.get(1),
        )

        assertEquals(
            expected = "two",
            actual = map.get(2),
        )

        assertEquals(
            expected = "three",
            actual = map.get(3),
        )

        assertEquals(
            expected = null,
            actual = map.get(4),
        )

        val visitedEntries = mutableListOf<Pair<Int, String>>()

        map.forEach { k, v ->
            visitedEntries.add(k to v)
        }

        assertEquals(
            expected = listOf(1 to "one", 2 to "two", 3 to "three"),
            actual = visitedEntries,
        )
    }

    @Test
    @Ignore // TODO: Add proper support for JsArrays
    fun testJsMapDelete() {
        val map: JsMap<Int, String> = JsMap()

        map.set(1, "one")
        map.set(2, "two")
        map.set(3, "three")

        assertEquals(
            expected = 3,
            actual = map.size,
        )

        assertTrue(
            actual = map.delete(2),
        )

        assertEquals(
            expected = 2,
            actual = map.size,
        )

        assertTrue(
            actual = map.has(1),
        )

        assertFalse(
            actual = map.has(2),
        )

        assertTrue(
            actual = map.has(3),
        )

        assertFalse(
            actual = map.delete(4),
        )

        assertEquals(
            expected = 2,
            actual = map.size,
        )

        val visitedEntries = mutableListOf<Pair<Int, String>>()

        map.forEach { k, v ->
            visitedEntries.add(k to v)
        }

        assertEquals(
            expected = listOf(1 to "one", 3 to "three"),
            actual = visitedEntries,
        )
    }

    @Test
    @Ignore // TODO: Add proper support for JsArrays
    fun testJsMapClear() {
        val map: JsMap<Int, String> = JsMap()

        map.set(1, "one")
        map.set(2, "two")
        map.set(3, "three")

        assertEquals(
            expected = 3,
            actual = map.size,
        )

        map.clear()

        assertEquals(
            expected = 0,
            actual = map.size,
        )

        assertFalse(
            actual = map.has(1),
        )

        assertFalse(
            actual = map.has(2),
        )

        assertFalse(
            actual = map.has(3),
        )

        val visitedEntries = mutableListOf<Pair<Int, String>>()

        map.forEach { key, value ->
            visitedEntries.add(key to value)
        }

        assertEquals(
            expected = emptyList(),
            actual = visitedEntries,
        )
    }
}
