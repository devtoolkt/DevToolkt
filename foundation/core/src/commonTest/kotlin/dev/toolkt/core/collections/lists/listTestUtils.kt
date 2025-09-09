package dev.toolkt.core.collections.lists

import kotlin.test.assertEquals
import kotlin.test.assertTrue

fun <E> List<E>.verifyContentUnique(
    vararg elements: E,
) {
    verifyContent(
        elements = elements.toList(),
        unique = true,
    )
}

fun <E> List<E>.verifyContent(
    vararg elements: E,
) {
    verifyContent(
        elements = elements.toList(),
        unique = false,
    )
}

fun <E> List<E>.verifyContent(
    elements: List<E>,
    unique: Boolean,
) {
    assertEquals(
        expected = elements.size,
        actual = size,
        message = "Actual size does not match expected size: expected ${elements.size}, got $size",
    )

    elements.forEachIndexed { index, element ->
        val actualElement = this[index]

        assertEquals(
            expected = element,
            actual = actualElement,
            message = "Actual element at index $index does not match expected element: expected $element, got $actualElement",
        )

        assertTrue(
            actual = contains(element),
            message = "List does not contain expected element: $element",
        )

        if (unique) {
            val foundIndex = indexOf(element)

            assertEquals(
                expected = index,
                actual = foundIndex,
                message = "Index-of $index does not match expected index: expected $index, got $foundIndex",
            )

            val foundLastIndex = lastIndexOf(element)

            assertEquals(
                expected = foundIndex,
                actual = foundLastIndex,
                message = "Last-index-of $index does not match index-of: expected $foundIndex, got $foundLastIndex",
            )
        }
    }
}
