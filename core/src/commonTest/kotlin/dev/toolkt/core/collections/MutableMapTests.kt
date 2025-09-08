package dev.toolkt.core.collections

import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Tests confirming the behavior of the built-in `MutableMap` implementations
 */
class MutableMapTests {
    private class MutableMapEntry<K, V>(
        override val key: K,
        initialValue: V,
    ) : MutableMap.MutableEntry<K, V> {
        private var mutableValue: V = initialValue

        override fun setValue(newValue: V): V {
            val previousValue = mutableValue
            mutableValue = newValue
            return previousValue
        }

        override val value: V
            get() = mutableValue

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is MutableMap.MutableEntry<*, *>) return false

            if (key != other.key) return false
            if (value != other.value) return false

            return true
        }

        override fun hashCode(): Int {
            var result = key.hashCode()
            result = 31 * result + value.hashCode()
            return result
        }
    }

    @Test
    fun testKeysAdd() {
        val mutableMap = mutableMapOf(
            1 to "A",
            2 to "B",
            3 to "C",
        )

        assertIs<UnsupportedOperationException>(
            assertFails {
                mutableMap.keys.add(4)
            },
        )
    }

    @Test
    fun testKeysRemove() {
        val mutableMap = mutableMapOf(
            1 to "A",
            2 to "B",
            3 to "C",
        )

        assertTrue(
            mutableMap.keys.remove(2),
        )

        assertEquals(
            expected = mutableMapOf(
                1 to "A",
                3 to "C",
            ),
            actual = mutableMap,
        )
    }

    @Test
    fun testValuesAdd() {
        val mutableMap = mutableMapOf(
            1 to "A",
            2 to "B",
            3 to "C",
        )

        assertIs<UnsupportedOperationException>(
            assertFails {
                mutableMap.values.add("D")
            },
        )
    }

    @Test
    fun testValuesRemove() {
        val mutableMap = mutableMapOf(
            1 to "A",
            2 to "B",
            3 to "C",
        )

        assertTrue(
            mutableMap.values.remove("C"),
        )

        assertEquals(
            expected = mutableMapOf(
                1 to "A",
                2 to "B",
            ),
            actual = mutableMap,
        )
    }

    @Test
    @Ignore // FIXME: This fails on the JS target!
    fun testValuesRemove_duplicates() {
        val mutableMap = mutableMapOf(
            1 to "A",
            2 to "B",
            3 to "C",
            4 to "A",
        )

        assertTrue(
            mutableMap.values.remove("A"),
        )

        assertEquals(
            expected = mutableMapOf(
                2 to "B",
                3 to "C",
                4 to "A", // `remove` removes a "a single instance of the specified element"
            ),
            actual = mutableMap,
        )
    }

    @Test
    fun testEntriesContains_external() {
        val mutableMap = mutableMapOf(
            1 to "A",
            2 to "B",
            3 to "C",
        )

        assertTrue(
            mutableMap.entries.contains(
                MutableMapEntry(
                    key = 2,
                    initialValue = "B",
                ),
            ),
        )

        assertFalse(
            mutableMap.entries.contains(
                MutableMapEntry(
                    key = 4,
                    initialValue = "X",
                ),
            ),
        )
    }

    @Test
    fun testEntriesAdd_external() {
        val mutableMap = mutableMapOf(
            1 to "A",
            2 to "B",
            3 to "C",
        )

        assertIs<UnsupportedOperationException>(
            assertFails {
                mutableMap.entries.add(
                    MutableMapEntry(
                        key = 4,
                        initialValue = "D",
                    ),
                )
            },
        )
    }

    @Test
    fun testEntriesSetValue() {
        val mutableMap = mutableMapOf(
            1 to "A",
            2 to "B",
            3 to "C",
        )

        val firstEntry = mutableMap.iterator().next()

        firstEntry.setValue("A2")

        assertEquals(
            expected = mutableMapOf(
                1 to "A2",
                2 to "B",
                3 to "C",
            ),
            actual = mutableMap,
        )
    }

    @Test
    fun testEntriesRemove_internal() {
        val mutableMap = mutableMapOf(
            1 to "A",
            2 to "B",
            3 to "C",
        )

        val firstEntry = mutableMap.iterator().next()

        assertTrue(
            mutableMap.entries.remove(firstEntry),
        )

        assertEquals(
            expected = mutableMapOf(
                2 to "B",
                3 to "C",
            ),
            actual = mutableMap,
        )
    }

    @Test
    fun testEntriesRemove_external() {
        val mutableMap = mutableMapOf(
            1 to "A",
            2 to "B",
            3 to "C",
        )

        assertTrue(
            mutableMap.entries.remove(
                MutableMapEntry(
                    key = 2,
                    initialValue = "B",
                ),
            ),
        )

        assertEquals(
            expected = mutableMapOf(
                1 to "A",
                3 to "C",
            ),
            actual = mutableMap,
        )
    }

    @Test
    fun testKeysIterator() {
        val mutableMap = mutableMapOf(
            1 to "A",
            2 to "B",
            3 to "C",
        )

        val iterator = mutableMap.keys.iterator()

        assertEquals(
            expected = 1,
            actual = iterator.next(),
        )

        iterator.remove()

        assertEquals(
            expected = mutableMapOf(
                2 to "B",
                3 to "C",
            ),
            actual = mutableMap,
        )

        assertEquals(
            expected = setOf(2, 3),
            actual = mutableMap.keys,
        )
    }

    @Test
    fun testValuesIterator() {
        val mutableMap = mutableMapOf(
            1 to "A",
            2 to "B",
            3 to "C",
        )

        val iterator = mutableMap.values.iterator()

        assertEquals(
            expected = "A",
            actual = iterator.next(),
        )

        iterator.remove()

        assertEquals(
            expected = mutableMapOf(
                2 to "B",
                3 to "C",
            ),
            actual = mutableMap,
        )

        assertEquals(
            expected = setOf(2, 3),
            actual = mutableMap.keys,
        )
    }

    @Test
    fun testEntriesIterator() {
        val mutableMap = mutableMapOf(
            1 to "A",
            2 to "B",
            3 to "C",
        )

        val iterator = mutableMap.entries.iterator()

        val firstEntry = iterator.next()

        assertEquals(
            expected = 1,
            actual = firstEntry.key,
        )

        assertEquals(
            expected = "A",
            actual = firstEntry.value,
        )

        iterator.remove()

        assertEquals(
            expected = mutableMapOf(
                2 to "B",
                3 to "C",
            ),
            actual = mutableMap,
        )

        val secondEntry = iterator.next()

        assertEquals(
            expected = 2,
            actual = secondEntry.key,
        )

        assertEquals(
            expected = "B",
            actual = secondEntry.value,
        )

        secondEntry.setValue("B2")

        assertEquals(
            expected = mutableMapOf(
                2 to "B2",
                3 to "C",
            ),
            actual = mutableMap,
        )
    }
}
