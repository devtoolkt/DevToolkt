package dev.toolkt.core.collections.maps.mutable_stable_multivalued_map

import dev.toolkt.core.collections.maps.MapEntry
import dev.toolkt.core.collections.maps.MutableStableMultiValuedMap
import dev.toolkt.core.collections.bags.mutableStableBagOf
import dev.toolkt.core.collections.maps.mutableStableMapOf
import dev.toolkt.core.collections.maps.verifyIntegrity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class NewFromStableMapTests {
    @Test
    fun testNewFromStableMap_initial() {
        val mutableMultiValuedMap = MutableStableMultiValuedMap.newFromStableMap(
            bucketMap = mutableStableMapOf(
                10 to mutableStableBagOf("A", "B", "C", "A"),
                20 to mutableStableBagOf("X", "W", "A"),
                30 to mutableStableBagOf("K", "O"),
            ),
        )

        mutableMultiValuedMap.verifyIntegrity(
            expectedEntries = listOf(
                MapEntry(10, "A"),
                MapEntry(20, "X"),
                MapEntry(30, "K"),
                MapEntry(10, "B"),
                MapEntry(20, "W"),
                MapEntry(30, "O"),
                MapEntry(10, "C"),
                MapEntry(20, "A"),
                MapEntry(10, "A"),
            ),
            controlKeys = setOf(
                -10, 0, 40,
            ),
            controlEntries = setOf(
                MapEntry(10, "X"),
                MapEntry(20, "B"),
                MapEntry(30, "C"),
                MapEntry(30, "D"),
                MapEntry(40, "A"),
            ),
        )
    }

    @Test
    fun testNewFromStableMap_add_existingKeyExistingValue() {
        val mutableMultiValuedMap = MutableStableMultiValuedMap.newFromStableMap(
            bucketMap = mutableStableMapOf(
                10 to mutableStableBagOf("A"),
                20 to mutableStableBagOf("X", "W"),
                30 to mutableStableBagOf("K", "O"),
            ),
        )

        val entryHandle = assertNotNull(
            actual = mutableMultiValuedMap.addEx(
                MapEntry(
                    key = 20,
                    value = "X",
                ),
            ),
        )

        assertEquals(
            expected = MapEntry(
                key = 20,
                value = "X",
            ),
            actual = mutableMultiValuedMap.getVia(handle = entryHandle),
        )

        mutableMultiValuedMap.verifyIntegrity(
            expectedEntries = listOf(
                MapEntry(10, "A"),
                MapEntry(20, "X"),
                MapEntry(20, "W"),
                MapEntry(30, "K"),
                MapEntry(30, "O"),
                MapEntry(20, "X"),
            ),
            controlKeys = setOf(
                -10, 0, 40,
            ),
            controlEntries = setOf(
                MapEntry(10, "U"),
                MapEntry(20, "A"),
                MapEntry(30, "L"),
                MapEntry(40, "X"),
            ),
        )
    }

    @Test
    fun testNewFromStableMap_add_existingKeyNewValue() {
        val mutableMultiValuedMap = MutableStableMultiValuedMap.newFromStableMap(
            bucketMap = mutableStableMapOf(
                10 to mutableStableBagOf("A", "B", "C", "A"),
                20 to mutableStableBagOf("X", "W", "A"),
                30 to mutableStableBagOf("K", "O"),
            ),
        )

        val entryHandle = assertNotNull(
            actual = mutableMultiValuedMap.addEx(
                MapEntry(
                    key = 10,
                    value = "J",
                ),
            ),
        )

        assertEquals(
            expected = MapEntry(
                key = 10,
                value = "J",
            ),
            actual = mutableMultiValuedMap.getVia(handle = entryHandle),
        )

        mutableMultiValuedMap.verifyIntegrity(
            expectedEntries = listOf(
                MapEntry(10, "A"),
                MapEntry(20, "X"),
                MapEntry(30, "K"),
                MapEntry(10, "B"),
                MapEntry(20, "W"),
                MapEntry(30, "O"),
                MapEntry(10, "C"),
                MapEntry(20, "A"),
                MapEntry(10, "A"),
                MapEntry(10, "J"),
            ),
            controlKeys = setOf(
                -10, 0, 40,
            ),
            controlEntries = setOf(
                MapEntry(10, "X"),
                MapEntry(20, "B"),
                MapEntry(30, "C"),
                MapEntry(30, "D"),
                MapEntry(40, "B"),
            ),
        )
    }

    @Test
    fun testNewFromStableMap_add_newKey() {
        val mutableMultiValuedMap = MutableStableMultiValuedMap.newFromStableMap(
            bucketMap = mutableStableMapOf(
                10 to mutableStableBagOf("A"),
                20 to mutableStableBagOf("X", "W"),
                30 to mutableStableBagOf("K", "O"),
            ),
        )

        val entryHandle = assertNotNull(
            actual = mutableMultiValuedMap.addEx(
                MapEntry(
                    key = 40,
                    value = "L",
                ),
            ),
        )

        assertEquals(
            expected = MapEntry(
                key = 40,
                value = "L",
            ),
            actual = mutableMultiValuedMap.getVia(handle = entryHandle),
        )

        mutableMultiValuedMap.verifyIntegrity(
            expectedEntries = listOf(
                MapEntry(10, "A"),
                MapEntry(20, "X"),
                MapEntry(20, "W"),
                MapEntry(30, "K"),
                MapEntry(30, "O"),
                MapEntry(40, "L"),
            ),
            controlKeys = setOf(
                -10, 0, 50,
            ),
            controlEntries = setOf(
                MapEntry(10, "U"),
                MapEntry(20, "A"),
                MapEntry(30, "L"),
                MapEntry(40, "X"),
            ),
        )
    }

    @Test
    fun testNewFromStableMap_removeKey_existing() {
        val mutableMultiValuedMap = MutableStableMultiValuedMap.newFromStableMap(
            bucketMap = mutableStableMapOf(
                10 to mutableStableBagOf("A"),
                20 to mutableStableBagOf("X", "W"),
                30 to mutableStableBagOf("K", "O"),
                40 to mutableStableBagOf("L"),
            ),
        )

        assertTrue(
            actual = mutableMultiValuedMap.removeKey(key = 20),
        )

        mutableMultiValuedMap.verifyIntegrity(
            expectedEntries = listOf(
                MapEntry(10, "A"),
                MapEntry(30, "K"),
                MapEntry(30, "O"),
                MapEntry(40, "L"),
            ),
            controlKeys = setOf(
                -10, 0, 20, 50,
            ),
            controlEntries = setOf(
                MapEntry(10, "U"),
                MapEntry(20, "A"),
                MapEntry(20, "X"),
                MapEntry(20, "W"),
                MapEntry(30, "L"),
                MapEntry(40, "X"),
            ),
        )
    }

    @Test
    fun testNewFromStableMap_removeKey_nonExisting() {
        val mutableMultiValuedMap = MutableStableMultiValuedMap.newFromStableMap(
            bucketMap = mutableStableMapOf(
                10 to mutableStableBagOf("A"),
                30 to mutableStableBagOf("C", "D"),
            ),
        )

        assertFalse(
            actual = mutableMultiValuedMap.removeKey(key = 20),
        )

        mutableMultiValuedMap.verifyIntegrity(
            expectedEntries = listOf(
                MapEntry(10, "A"),
                MapEntry(30, "C"),
                MapEntry(30, "D"),
            ),
            controlKeys = setOf(
                -10, 0, 20,
            ),
            controlEntries = setOf(
                MapEntry(30, "L"),
                MapEntry(40, "X"),
            ),
        )
    }

    @Test
    fun testNewFromStableMap_removeEntry_existing() {
        val mutableMultiValuedMap = MutableStableMultiValuedMap.newFromStableMap(
            bucketMap = mutableStableMapOf(
                10 to mutableStableBagOf("A"),
                20 to mutableStableBagOf("X", "W"),
                30 to mutableStableBagOf("K", "O"),
            ),
        )

        assertTrue(
            actual = mutableMultiValuedMap.remove(
                MapEntry(
                    key = 20,
                    value = "X",
                ),
            ),
        )

        mutableMultiValuedMap.verifyIntegrity(
            expectedEntries = listOf(
                MapEntry(10, "A"),
                MapEntry(30, "K"),
                MapEntry(20, "W"),
                MapEntry(30, "O"),
            ),
            controlKeys = setOf(
                -10, 0, 40, 50,
            ),
            controlEntries = setOf(
                MapEntry(10, "U"),
                MapEntry(20, "A"),
                MapEntry(20, "X"),
                MapEntry(30, "L"),
                MapEntry(40, "X"),
            ),
        )
    }

    @Test
    fun testNewFromStableMap_removeEntry_nonExisting() {
        val mutableMultiValuedMap = MutableStableMultiValuedMap.newFromStableMap(
            bucketMap = mutableStableMapOf(
                10 to mutableStableBagOf("A"),
                20 to mutableStableBagOf("X", "W"),
                30 to mutableStableBagOf("K"),
            ),
        )

        assertFalse(
            actual = mutableMultiValuedMap.remove(
                MapEntry(
                    key = 20,
                    value = "Y",
                ),
            ),
        )

        mutableMultiValuedMap.verifyIntegrity(
            expectedEntries = listOf(
                MapEntry(10, "A"),
                MapEntry(20, "X"),
                MapEntry(20, "W"),

                MapEntry(30, "K"),
            ),
            controlKeys = setOf(
                -10, 0, 40, 50,
            ),
            controlEntries = setOf(
                MapEntry(10, "U"),
            ),
        )
    }

    @Test
    fun testNewFromStableMap_removeEntry_lastForKey() {
        val mutableMultiValuedMap = MutableStableMultiValuedMap.newFromStableMap(
            bucketMap = mutableStableMapOf(
                10 to mutableStableBagOf("A"),
                20 to mutableStableBagOf("X", "W"),
                30 to mutableStableBagOf("K", "O"),
            ),
        )

        mutableMultiValuedMap.remove(
            MapEntry(
                key = 10,
                value = "A",
            ),
        )

        mutableMultiValuedMap.verifyIntegrity(
            expectedEntries = listOf(
                MapEntry(30, "K"),
                MapEntry(20, "W"),
                MapEntry(30, "O"),
                MapEntry(20, "X"),
            ),
            controlKeys = setOf(
                -10, 0, 10, 40, 50,
            ),
            controlEntries = setOf(
                MapEntry(10, "A"),
                MapEntry(20, "A"),
                MapEntry(30, "L"),
                MapEntry(40, "X"),
            ),
        )
    }
}
