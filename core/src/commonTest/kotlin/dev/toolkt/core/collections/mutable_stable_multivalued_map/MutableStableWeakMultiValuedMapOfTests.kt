package dev.toolkt.core.collections.mutable_stable_multivalued_map

import dev.toolkt.core.collections.maps.MapEntry
import dev.toolkt.core.collections.maps.mutableStableWeakMultiValuedMapOf
import dev.toolkt.core.collections.verifyIntegrity
import dev.toolkt.core.platform.test_utils.runTestDefault
import dev.toolkt.core.platform.test_utils.waitUntil
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

@Ignore
class MutableStableWeakMultiValuedMapOfTests {
    private class Key()

    @Test
    fun testNewWeakFromStableBag_initial() {
        val key10 = Key()
        val key20 = Key()
        val key30 = Key()

        val mutableMultiValuedMap = mutableStableWeakMultiValuedMapOf(
            key10 to "A",
            key20 to "X",
            key30 to "K",
            key10 to "B",
            key20 to "W",
            key30 to "O",
            key10 to "C",
            key20 to "A",
            key10 to "A",
        )

        mutableMultiValuedMap.verifyIntegrity(
            expectedEntries = listOf(
                MapEntry(key10, "A"),
                MapEntry(key10, "A"),
                MapEntry(key10, "B"),
                MapEntry(key10, "C"),
                MapEntry(key20, "A"),
                MapEntry(key20, "W"),
                MapEntry(key20, "X"),
                MapEntry(key30, "K"),
                MapEntry(key30, "O"),
            ),
            controlKeys = setOf(
                Key(), Key(), Key(),
            ),
            controlEntries = setOf(
                MapEntry(key10, "X"),
                MapEntry(key20, "B"),
                MapEntry(key30, "C"),
                MapEntry(key30, "D"),
                MapEntry(Key(), "A"),
            ),
        )
    }

    @Test
    fun testNewWeakFromStableBag_add_existingKeyExistingValue() {
        val key10 = Key()
        val key20 = Key()
        val key30 = Key()

        val mutableMultiValuedMap = mutableStableWeakMultiValuedMapOf(
            key10 to "A",
            key20 to "X",
            key30 to "O",
            key20 to "X",
            key20 to "W",
            key30 to "K",
        )

        val entryHandle = assertNotNull(
            actual = mutableMultiValuedMap.addEx(
                MapEntry(
                    key = key20,
                    value = "X",
                ),
            ),
        )

        assertEquals(
            expected = MapEntry(
                key = key20,
                value = "X",
            ),
            actual = mutableMultiValuedMap.getVia(handle = entryHandle),
        )

        mutableMultiValuedMap.verifyIntegrity(
            expectedEntries = listOf(
                MapEntry(key10, "A"),
                MapEntry(key20, "W"),
                MapEntry(key20, "X"),
                MapEntry(key20, "X"),
                MapEntry(key30, "K"),
                MapEntry(key30, "O"),
            ),
            controlKeys = setOf(
                Key(), Key(), Key(),
            ),
            controlEntries = setOf(
                MapEntry(key10, "U"),
                MapEntry(key20, "A"),
                MapEntry(key30, "L"),
                MapEntry(Key(), "X"),
            ),
        )
    }

    @Test
    fun testNewWeakFromStableBag_add_existingKeyNewValue() {
        val key10 = Key()
        val key20 = Key()
        val key30 = Key()

        val mutableMultiValuedMap = mutableStableWeakMultiValuedMapOf(
            key10 to "A",
            key30 to "K",
            key10 to "B",
            key20 to "X",
            key20 to "W",
            key10 to "A",
            key10 to "J",
            key30 to "O",
            key10 to "C",
            key20 to "A",
        )

        val entryHandle = assertNotNull(
            actual = mutableMultiValuedMap.addEx(
                MapEntry(
                    key = key10,
                    value = "J",
                ),
            ),
        )

        assertEquals(
            expected = MapEntry(
                key = key10,
                value = "J",
            ),
            actual = mutableMultiValuedMap.getVia(handle = entryHandle),
        )

        mutableMultiValuedMap.verifyIntegrity(
            expectedEntries = listOf(
                MapEntry(key10, "A"),
                MapEntry(key10, "A"),
                MapEntry(key10, "B"),
                MapEntry(key10, "C"),
                MapEntry(key10, "J"),
                MapEntry(key20, "A"),
                MapEntry(key20, "W"),
                MapEntry(key20, "X"),
                MapEntry(key30, "K"),
                MapEntry(key30, "O"),
            ),
            controlKeys = setOf(
                Key(), Key(), Key(),
            ),
            controlEntries = setOf(
                MapEntry(key10, "X"),
                MapEntry(key20, "B"),
                MapEntry(key30, "C"),
                MapEntry(key30, "D"),
                MapEntry(Key(), "B"),
            ),
        )
    }

    @Test
    fun testNewWeakFromStableBag_add_newKey() {
        val key10 = Key()
        val key20 = Key()
        val key30 = Key()
        val key40 = Key()

        val mutableMultiValuedMap = mutableStableWeakMultiValuedMapOf(
            key40 to "L",
            key10 to "A",
            key20 to "X",
            key30 to "O",
            key20 to "W",
            key30 to "K",
        )

        val entryHandle = assertNotNull(
            actual = mutableMultiValuedMap.addEx(
                MapEntry(
                    key = key40,
                    value = "L",
                ),
            ),
        )

        assertEquals(
            expected = MapEntry(
                key = key40,
                value = "L",
            ),
            actual = mutableMultiValuedMap.getVia(handle = entryHandle),
        )

        mutableMultiValuedMap.verifyIntegrity(
            expectedEntries = listOf(
                MapEntry(key10, "A"),
                MapEntry(key20, "W"),
                MapEntry(key20, "X"),
                MapEntry(key30, "K"),
                MapEntry(key30, "O"),
                MapEntry(key40, "L"),
            ),
            controlKeys = setOf(
                Key(), Key(), Key(),
            ),
            controlEntries = setOf(
                MapEntry(key10, "U"),
                MapEntry(key20, "A"),
                MapEntry(key30, "L"),
                MapEntry(key40, "X"),
            ),
        )
    }

    @Test
    fun testNewWeakFromStableBag_removeKey_existing() {
        val key10 = Key()
        val key20 = Key()
        val key30 = Key()
        val key40 = Key()

        val mutableMultiValuedMap = mutableStableWeakMultiValuedMapOf(
            key10 to "A",
            key40 to "L",
            key30 to "K",
            key30 to "O",
        )

        assertTrue(
            actual = mutableMultiValuedMap.removeKey(key = key20),
        )

        mutableMultiValuedMap.verifyIntegrity(
            expectedEntries = listOf(
                MapEntry(key10, "A"),
                MapEntry(key30, "K"),
                MapEntry(key30, "O"),
                MapEntry(key40, "L"),
            ),
            controlKeys = setOf(
                Key(), Key(), key20, Key(),
            ),
            controlEntries = setOf(
                MapEntry(key10, "U"),
                MapEntry(key20, "A"),
                MapEntry(key20, "X"),
                MapEntry(key20, "W"),
                MapEntry(key30, "L"),
                MapEntry(key40, "X"),
            ),
        )
    }

    @Test
    fun testNewWeakFromStableBag_removeKey_nonExisting() {
        val key10 = Key()
        val key20 = Key()
        val key30 = Key()

        val mutableMultiValuedMap = mutableStableWeakMultiValuedMapOf(
            key30 to "D",
            key10 to "A",
            key30 to "C",
        )

        assertFalse(
            actual = mutableMultiValuedMap.removeKey(key = key20),
        )

        mutableMultiValuedMap.verifyIntegrity(
            expectedEntries = listOf(
                MapEntry(key10, "A"),
                MapEntry(key30, "C"),
                MapEntry(key30, "D"),
            ),
            controlKeys = setOf(
                Key(), Key(), key20,
            ),
            controlEntries = setOf(
                MapEntry(key30, "L"),
                MapEntry(Key(), "X"),
            ),
        )
    }

    @Test
    fun testNewWeakFromStableBag_removeEntry_existing() {
        val key10 = Key()
        val key20 = Key()
        val key30 = Key()

        val mutableMultiValuedMap = mutableStableWeakMultiValuedMapOf(
            key30 to "K",
            key20 to "W",
            key30 to "O",
            key10 to "A",
        )

        assertTrue(
            actual = mutableMultiValuedMap.remove(
                MapEntry(
                    key = key20,
                    value = "X",
                ),
            ),
        )

        mutableMultiValuedMap.verifyIntegrity(
            expectedEntries = listOf(
                MapEntry(key10, "A"),
                MapEntry(key20, "W"),
                MapEntry(key30, "K"),
                MapEntry(key30, "O"),
            ),
            controlKeys = setOf(
                Key(), Key(), Key(), Key(),
            ),
            controlEntries = setOf(
                MapEntry(key10, "U"),
                MapEntry(key20, "A"),
                MapEntry(key20, "X"),
                MapEntry(key30, "L"),
                MapEntry(Key(), "X"),
            ),
        )
    }

    @Test
    fun testNewWeakFromStableBag_removeEntry_nonExisting() {
        val key10 = Key()
        val key20 = Key()
        val key30 = Key()

        val mutableMultiValuedMap = mutableStableWeakMultiValuedMapOf(
            key20 to "W",
            key30 to "K",
            key10 to "A",
            key20 to "X",
        )

        assertFalse(
            actual = mutableMultiValuedMap.remove(
                MapEntry(
                    key = key20,
                    value = "Y",
                ),
            ),
        )

        mutableMultiValuedMap.verifyIntegrity(
            expectedEntries = listOf(
                MapEntry(key10, "A"),
                MapEntry(key20, "W"),
                MapEntry(key20, "X"),
                MapEntry(key30, "K"),
            ),
            controlKeys = setOf(
                Key(),
            ),
            controlEntries = setOf(
                MapEntry(key10, "U"),
            ),
        )
    }

    @Test
    fun testNewWeakFromStableBag_removeEntry_lastForKey() {
        val key10 = Key()
        val key20 = Key()
        val key30 = Key()

        val mutableMultiValuedMap = mutableStableWeakMultiValuedMapOf(
            key30 to "K",
            key20 to "W",
            key30 to "O",
            key20 to "X",
        )

        mutableMultiValuedMap.remove(
            MapEntry(
                key = key10,
                value = "A",
            ),
        )

        mutableMultiValuedMap.verifyIntegrity(
            expectedEntries = listOf(
                MapEntry(key20, "W"),
                MapEntry(key20, "X"),
                MapEntry(key30, "K"),
                MapEntry(key30, "O"),
            ),
            controlKeys = setOf(
                Key(), Key(), key10, Key(), Key(),
            ),
            controlEntries = setOf(
                MapEntry(key10, "A"),
                MapEntry(key20, "A"),
                MapEntry(key30, "L"),
                MapEntry(Key(), "X"),
            ),
        )
    }

    @Suppress("AssignedValueIsNeverRead")
    @Test
    fun testNewWeakFromStableBag_garbageCollection() = runTestDefault {
        val key10 = Key()
        val key20 = Key()
        val key30 = Key()

        var key40: Key? = Key()

        key40!!

        val mutableMultiValuedMap = mutableStableWeakMultiValuedMapOf(
            key30 to "K",
            key20 to "W",
            key40 to "M",
            key30 to "O",
            key20 to "X",
        )

        val handle40 = mutableMultiValuedMap.addEx(
            key = key40,
            value = "P",
        ) ?: throw AssertionError("Unable to add a new key")

        key40 = null

        waitUntil(
            pauseDuration = 1.milliseconds,
            timeoutDuration = 10.milliseconds,
        ) {
            mutableMultiValuedMap.getVia(handle40) == null
        }

        mutableMultiValuedMap.verifyIntegrity(
            expectedEntries = listOf(
                MapEntry(key20, "W"),
                MapEntry(key20, "X"),
                MapEntry(key30, "K"),
                MapEntry(key30, "O"),
            ),
            controlKeys = setOf(),
            controlEntries = setOf(
                MapEntry(key10, "A"),
                MapEntry(key20, "A"),
                MapEntry(key30, "L"),
                MapEntry(Key(), "X"),
            ),
        )
    }
}
