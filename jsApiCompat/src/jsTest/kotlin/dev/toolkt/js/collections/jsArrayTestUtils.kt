package dev.toolkt.js.collections

import kotlin.js.collections.JsReadonlyArray
import kotlin.test.assertEquals

fun <E> assertJsArrayEquals(
    expected: JsReadonlyArray<E>,
    actual: JsReadonlyArray<E>,
) {
    assertEquals(
        expected = expected.length,
        actual = actual.length,
        message = "Array lengths differ. Expected: ${expected.length}, Actual: ${actual.length}",
    )

    actual.forEach { element, index, _ ->
        assertEquals(
            expected = expected[index],
            actual = element,
            message = "Array elements at index $index differ. Expected: ${expected[index]}, Actual: $element",
        )
    }
}
