package dev.toolkt.core.collections.maps

import kotlin.test.assertEquals
import kotlin.test.assertTrue

fun <K, V> MultiValuedMap<K, V>.verifyIntegrity(
    expectedEntries: Collection<Map.Entry<K, V>>,
    controlKeys: Set<K>,
    controlEntries: Set<Map.Entry<K, V>>,
) {
    val groupedExpectedEntries = expectedEntries.groupBy { it.key }
    val expectedValuesByKey = groupedExpectedEntries.mapValues { (_, entries) ->
        entries.map { (_, value) -> value }
    }

    val expectedKeys = expectedValuesByKey.keys
    val expectedValues = expectedValuesByKey.flatMap { (_, values) ->
        values
    }.toList()

    val expectedSize = expectedEntries.size
    val actualSize = size

    // Size

    assertEquals(
        expected = expectedSize,
        actual = actualSize,
        message = "Actual size does not match expected size: expected $expectedSize, got $actualSize",
    )

    // Traversal

    val actualEntries = toList()

    assertEquals(
        expected = expectedEntries.sortedBy { it.hashCode() },
        actual = actualEntries.sortedBy { it.hashCode() },
        message = "Actual entries do not match expected entries: expected $expectedEntries, got $actualEntries",
    )

    // asMap

    assertEquals(
        expected = expectedValuesByKey.mapValues { (_, values) ->
            values.sortedBy { it.hashCode() }
        },
        actual = asMap().mapValues { (_, values) ->
            values.sortedBy { it.hashCode() }
        },
    )

    // getAll

    expectedValuesByKey.forEach { (key, expectedValues) ->
        // TODO: Change to toBag() == other.toBag()?
        val actualValues = getAll(key = key).toList().sortedBy { it.hashCode() }

        assertEquals(
            expected = expectedValues.sortedBy { it.hashCode() },
            actual = actualValues,
            message = "Values for key $key do not match: expected $expectedValues, got $actualValues",
        )
    }

    assertTrue(
        actual = controlEntries.none { actualEntries.contains(it) },
    )

    // Keys / values

    val actualKeys = keys

    assertEquals(
        expected = expectedKeys,
        actual = keys,
        message = "Actual keys do not match expected keys: expected $expectedKeys, got $actualKeys",
    )

    val actualValues = values.sortedBy { it.hashCode() }

    assertEquals(
        expected = expectedValues.sortedBy { it.hashCode() },
        actual = actualValues,
        message = "Actual values do not match expected values: expected $expectedValues, got $actualValues",
    )

    // contains / containsKey

    assertTrue(
        actual = expectedEntries.all { contains(it) },
    )

    assertTrue(
        actual = expectedKeys.all { containsKey(key = it) },
    )

    assertTrue(
        actual = controlKeys.none { containsKey(key = it) },
    )

    assertTrue(
        actual = controlEntries.none { contains(it) },
    )
}
