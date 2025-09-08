package dev.toolkt.core.collections

import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Verifies the consistency of a [Set] against expected elements and control elements.
 */
fun <E> Set<E>.verifyContent(
    /**
     * Expected elements in the set (in the iteration order).
     */
    elements: List<E>,
    /**
     * Control elements that should not be present in the set.
     */
    controlElements: Set<E>,
) {
    assertEquals(
        expected = elements.size,
        actual = size,
        message = "Actual size does not match expected size: expected ${elements.size}, got $size",
    )

    // Actual elements in the iteration order
    val actualElements = toList()

    assertEquals(
        expected = elements,
        actual = actualElements,
        message = "Actual elements do not match expected elements: expected $elements, got $actualElements",
    )

    assertTrue(
        actual = controlElements.none { actualElements.contains(it) },
    )

    assertTrue(
        actual = controlElements.none { contains(it) },
    )
}
