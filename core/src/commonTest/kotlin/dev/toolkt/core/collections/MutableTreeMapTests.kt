package dev.toolkt.core.collections

import dev.toolkt.core.collections.maps.addEx
import dev.toolkt.core.collections.maps.mutableTreeMapOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class MutableTreeMapTests {
    @Test
    fun testInitial() {
        val map = mutableTreeMapOf<Int, String>()

        assertEquals(
            expected = 0,
            actual = map.size,
        )

        assertNull(
            actual = map[10],
        )

        map.verifyContent(
            entries = emptyList(),
            controlKeys = setOf(10, 20, 30),
        )
    }

    @Test
    fun testPut_empty() {
        val map = mutableTreeMapOf<Int, String>()

        assertNull(
            actual = map.put(
                key = 10,
                value = "a",
            ),
        )

        assertEquals(
            expected = "a",
            actual = map[10],
        )

        assertEquals(
            expected = 1,
            actual = map.size,
        )

        map.verifyContent(
            entries = listOf(10 to "a"),
            controlKeys = setOf(20, 30),
        )
    }

    @Test
    fun testPut_overwrite() {
        val map = mutableTreeMapOf(
            10 to "a",
            20 to "b",
        )

        assertEquals(
            expected = "a",
            actual = map.put(
                key = 10,
                value = "z",
            ),
        )

        assertEquals(
            expected = "z",
            actual = map[10],
        )

        assertEquals(
            expected = "b",
            actual = map[20],
        )

        assertEquals(
            expected = 2,
            actual = map.size,
        )

        map.verifyContent(
            entries = listOf(10 to "z", 20 to "b"),
            controlKeys = setOf(30, 40),
        )
    }

    @Test
    fun testPut_new() {
        val map = mutableTreeMapOf(
            10 to "a",
            20 to "b",
        )

        assertNull(
            actual = map.put(
                key = 15,
                value = "c",
            ),
        )

        assertEquals(
            expected = "c",
            actual = map[15],
        )

        assertEquals(
            expected = 3,
            actual = map.size,
        )

        map.verifyContent(
            entries = listOf(10 to "a", 15 to "c", 20 to "b"),
            controlKeys = setOf(30, 40),
        )
    }

    @Test
    fun testAddEx_duplicate() {
        val map = mutableTreeMapOf(
            10 to "a",
            20 to "b",
        )

        assertNull(
            actual = map.addEx(
                key = 10,
                value = "x",
            ),
        )

        assertEquals(
            expected = "a",
            actual = map[10],
        )

        assertEquals(
            expected = 2,
            actual = map.size,
        )

        map.verifyContent(
            entries = listOf(10 to "a", 20 to "b"),
            controlKeys = setOf(30, 40),
        )
    }

    @Test
    fun testAddEx_nonDuplicate() {
        val map = mutableTreeMapOf(
            10 to "a",
            20 to "b",
        )

        val handle = assertNotNull(
            actual = map.addEx(
                key = 15,
                value = "c",
            ),
        )

        assertEquals(
            expected = "c",
            actual = map.getValueVia(handle = handle),
        )

        assertEquals(
            expected = 3,
            actual = map.size,
        )

        map.verifyContent(
            entries = listOf(10 to "a", 15 to "c", 20 to "b"),
            controlKeys = setOf(30, 40),
        )
    }

    @Test
    fun testRemove() {
        val map = mutableTreeMapOf(
            10 to "a",
            20 to "b",
            30 to "c",
        )

        assertEquals(
            expected = "b",
            actual = map.remove(20),
        )

        assertNull(
            actual = map[20],
        )

        assertEquals(
            expected = 2,
            actual = map.size,
        )

        map.verifyContent(
            entries = listOf(10 to "a", 30 to "c"),
            controlKeys = setOf(20, 40, 50),
        )

        assertEquals(
            expected = "a",
            actual = map.remove(10),
        )

        map.verifyContent(
            entries = listOf(30 to "c"),
            controlKeys = setOf(10, 20, 40, 50),
        )

        assertEquals(
            expected = "c",
            actual = map.remove(30),
        )

        assertEquals(
            expected = 0,
            actual = map.size,
        )

        map.verifyContent(
            entries = emptyList(),
            controlKeys = setOf(10, 20, 30, 40, 50),
        )
    }

    @Test
    fun testRemove_notContained() {
        val map = mutableTreeMapOf(
            10 to "a",
            20 to "b",
        )

        assertNull(
            actual = map.remove(99),
        )

        assertEquals(
            expected = 2,
            actual = map.size,
        )

        map.verifyContent(
            entries = listOf(10 to "a", 20 to "b"),
            controlKeys = setOf(99, 30, 40),
        )
    }

    @Test
    fun testRemoveVia() {
        val map = mutableTreeMapOf(
            10 to "a",
            20 to "b",
            30 to "c",
        )

        val handle = assertNotNull(
            actual = map.resolve(key = 20),
        )

        val entry = assertNotNull(
            actual = map.removeVia(handle = handle),
        )

        assertEquals(
            expected = 20,
            actual = entry.key,
        )

        assertEquals(
            expected = "b",
            actual = entry.value,
        )

        assertNull(
            actual = map.getVia(handle = handle),
        )

        assertNull(
            actual = map.removeVia(handle = handle),
        )

        map.verifyContent(
            entries = listOf(10 to "a", 30 to "c"),
            controlKeys = setOf(20, 40, 50),
        )
    }

    @Test
    fun testResolveAndGetVia() {
        val map = mutableTreeMapOf(
            10 to "a",
            20 to "b",
        )

        val handle = assertNotNull(
            actual = map.resolve(key = 10),
        )

        assertEquals(
            expected = "a",
            actual = map.getValueVia(handle = handle),
        )

        map.verifyContent(
            entries = listOf(10 to "a", 20 to "b"),
            controlKeys = setOf(30, 40),
        )
    }
}
