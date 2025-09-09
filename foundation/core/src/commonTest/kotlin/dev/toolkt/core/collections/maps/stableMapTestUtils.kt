package dev.toolkt.core.collections.maps

import dev.toolkt.core.collections.getValueVia
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Verifies the consistency of a [Map] against expected entries and control keys.
 */
fun <K, V : Any> StableMap<K, V>.verifyContent(
    /**
     * Expected entries in the map (in the iteration order).
     */
    entries: List<Pair<K, V>>,
    /**
     * Control keys that should not be present in the map.
     */
    controlKeys: Set<K>,
) {
    assertEquals(
        expected = entries.size,
        actual = size,
        message = "Actual size does not match expected size: expected ${entries.size}, got $size",
    )

    entries.forEach { (key, value) ->
        val entryHandle = assertNotNull(
            resolve(key = key)
        )

        assertEquals(
            actual = getValueVia(entryHandle),
            expected = value,
        )
    }

    val self: Map<K, V> = this

    // Actual entries in the iteration order
    val actualEntries = self.toList()

    assertEquals(
        expected = entries,
        actual = actualEntries,
        message = "Actual entries do not match expected entries: expected $entries, got $actualEntries",
    )

    assertTrue(
        actual = controlKeys.none { controlKey ->
            actualEntries.find { it.first == controlKey } != null
        },
    )

    assertTrue(
        actual = controlKeys.none { self.contains(it) },
    )

    assertTrue(
        actual = controlKeys.all { resolve(key = it) == null },
    )
}
