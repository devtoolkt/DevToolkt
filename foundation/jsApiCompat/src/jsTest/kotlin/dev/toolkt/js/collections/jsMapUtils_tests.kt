package dev.toolkt.js.collections

import dev.toolkt.js.JsObjects
import kotlin.js.collections.JsArray
import kotlin.js.collections.JsMap
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Suppress("ClassName")
class jsMapUtils_tests {
    private data class ForEachCallbackTuple<K, V>(
        val value: V,
        val key: K,
        val map: JsMap<K, V>,
    )

    private val mapConstructor = js("Map")

    @Test
    fun testConstructorEmpty() {
        val map: JsMap<Int, String> = JsMap()

        assertEquals(
            expected = mapConstructor.prototype,
            actual = JsObjects.getPrototypeOf(map),
        )

        assertEquals(
            expected = 0,
            actual = map.size,
        )

        assertFalse(
            actual = map.has(1),
        )

        val visitedEntries = mutableListOf<Pair<Int, String>>()

        map.forEach { v, k, _ ->
            visitedEntries.add(k to v)
        }

        assertEquals(
            expected = emptyList(),
            actual = visitedEntries,
        )
    }

    @Test
    fun testConstructorCopying() {
        val originalMap: JsMap<Int, String> = JsMap<Int, String>().apply {
            set(1, "one")
            set(2, "two")
            set(3, "three")
        }

        val map: JsMap<Int, String> = JsMap(originalMap)

        assertEquals(
            expected = mapConstructor.prototype,
            actual = JsObjects.getPrototypeOf(map),
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

        map.forEach { value, key, _ ->
            visitedEntries.add(key to value)
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
    fun testForEach() {
        val map = JsMap<Int, String>().apply {
            set(1, "one")
            set(2, "two")
            set(3, "three")
        }

        val visitedEntries = mutableListOf<ForEachCallbackTuple<Int, String>>()

        map.forEach { value, key, map ->
            visitedEntries.add(
                ForEachCallbackTuple(
                    value = value,
                    key = key,
                    map = map,
                )
            )
        }

        assertEquals(
            expected = listOf(
                ForEachCallbackTuple(
                    value = "one",
                    key = 1,
                    map = map,
                ),
                ForEachCallbackTuple(
                    value = "two",
                    key = 2,
                    map = map,
                ),
                ForEachCallbackTuple(
                    value = "three",
                    key = 3,
                    map = map,
                ),
            ),
            actual = visitedEntries,
        )
    }

    @Test
    fun testSet() {
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

        map.forEach { value, key, _ ->
            visitedEntries.add(key to value)
        }

        assertEquals(
            expected = listOf(1 to "one", 2 to "two", 3 to "three"),
            actual = visitedEntries,
        )
    }

    @Test
    fun testDelete() {
        val map = JsMap<Int, String>().apply {
            set(1, "one")
            set(2, "two")
            set(3, "three")
        }

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

        map.forEach { value, key, _ ->
            visitedEntries.add(key to value)
        }

        assertEquals(
            expected = listOf(1 to "one", 3 to "three"),
            actual = visitedEntries,
        )
    }

    @Test
    fun testClear() {
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

        map.forEach { value, key, _ ->
            visitedEntries.add(key to value)
        }

        assertEquals(
            expected = emptyList(),
            actual = visitedEntries,
        )
    }
}
