package dev.toolkt.core.collections.mutable_stable_multivalued_map

import dev.toolkt.core.collections.maps.MapEntry
import dev.toolkt.core.collections.maps.MutableStableMultiValuedMap
import dev.toolkt.core.collections.bags.mutableStableBagOf
import dev.toolkt.core.collections.verifyIntegrity
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@Ignore
class NewFromStableBagTests {
    @Test
    fun testNewFromStableBag_initial() {
        val mutableMultiValuedMap = MutableStableMultiValuedMap.newFromStableBag(
            entryBag = mutableStableBagOf(
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
        )

        mutableMultiValuedMap.verifyIntegrity(
            expectedEntries = listOf(
                MapEntry(10, "A"),
                MapEntry(10, "A"),
                MapEntry(10, "B"),
                MapEntry(10, "C"),
                MapEntry(20, "A"),
                MapEntry(20, "W"),
                MapEntry(20, "X"),
                MapEntry(30, "K"),
                MapEntry(30, "O"),
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
    fun testNewFromStableBag_add_existingKeyExistingValue() {
        val mutableMultiValuedMap = MutableStableMultiValuedMap.newFromStableBag(
            entryBag = mutableStableBagOf(
                MapEntry(10, "A"),
                MapEntry(20, "X"),
                MapEntry(30, "O"),
                MapEntry(20, "X"),
                MapEntry(20, "W"),
                MapEntry(30, "K"),
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
                MapEntry(20, "W"),
                MapEntry(20, "X"),
                MapEntry(20, "X"),
                MapEntry(30, "K"),
                MapEntry(30, "O"),
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
    fun testNewFromStableBag_add_existingKeyNewValue() {
        val mutableMultiValuedMap = MutableStableMultiValuedMap.newFromStableBag(
            entryBag = mutableStableBagOf(
                MapEntry(10, "A"),
                MapEntry(30, "K"),
                MapEntry(10, "B"),
                MapEntry(20, "X"),
                MapEntry(20, "W"),
                MapEntry(10, "A"),
                MapEntry(10, "J"),
                MapEntry(30, "O"),
                MapEntry(10, "C"),
                MapEntry(20, "A"),
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
                MapEntry(10, "A"),
                MapEntry(10, "B"),
                MapEntry(10, "C"),
                MapEntry(10, "J"),
                MapEntry(20, "A"),
                MapEntry(20, "W"),
                MapEntry(20, "X"),
                MapEntry(30, "K"),
                MapEntry(30, "O"),
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
    fun testNewFromStableBag_add_newKey() {
        val mutableMultiValuedMap = MutableStableMultiValuedMap.newFromStableBag(
            entryBag = mutableStableBagOf(
                MapEntry(40, "L"),
                MapEntry(10, "A"),
                MapEntry(20, "X"),
                MapEntry(30, "O"),
                MapEntry(20, "W"),
                MapEntry(30, "K"),
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
                MapEntry(20, "W"),
                MapEntry(20, "X"),
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
    fun testNewFromStableBag_removeKey_existing() {
        val mutableMultiValuedMap = MutableStableMultiValuedMap.newFromStableBag(
            entryBag = mutableStableBagOf(
                MapEntry(10, "A"),
                MapEntry(40, "L"),
                MapEntry(30, "K"),
                MapEntry(30, "O"),
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
    fun testNewFromStableBag_removeKey_nonExisting() {
        val mutableMultiValuedMap = MutableStableMultiValuedMap.newFromStableBag(
            entryBag = mutableStableBagOf(
                MapEntry(30, "D"),
                MapEntry(10, "A"),
                MapEntry(30, "C"),
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
    fun testNewFromStableBag_removeEntry_existing() {
        val mutableMultiValuedMap = MutableStableMultiValuedMap.newFromStableBag(
            entryBag = mutableStableBagOf(
                MapEntry(30, "K"),
                MapEntry(20, "W"),
                MapEntry(30, "O"),
                MapEntry(10, "A"),
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
                MapEntry(20, "W"),
                MapEntry(30, "K"),
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
    fun testNewFromStableBag_removeEntry_nonExisting() {
        val mutableMultiValuedMap = MutableStableMultiValuedMap.newFromStableBag(
            entryBag = mutableStableBagOf(
                MapEntry(20, "W"),
                MapEntry(30, "K"),
                MapEntry(10, "A"),
                MapEntry(20, "X"),
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
                MapEntry(20, "W"),
                MapEntry(20, "X"),
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
    fun testNewFromStableBag_removeEntry_lastForKey() {
        val mutableMultiValuedMap = MutableStableMultiValuedMap.newFromStableBag(
            entryBag = mutableStableBagOf(
                MapEntry(30, "K"),
                MapEntry(20, "W"),
                MapEntry(30, "O"),
                MapEntry(20, "X"),
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
                MapEntry(20, "W"),
                MapEntry(20, "X"),
                MapEntry(30, "K"),
                MapEntry(30, "O"),
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
