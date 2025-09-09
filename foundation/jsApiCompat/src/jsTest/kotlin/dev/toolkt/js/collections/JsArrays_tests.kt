package dev.toolkt.js.collections

import dev.toolkt.js.JsObjects
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("ClassName")
class JsArrays_tests {
    private val arrayConstructor = js("Array")

    @Test
    fun testOf() {
        val array = JsArrays.of(1, 2, 3)

        assertEquals(
            expected = arrayConstructor.prototype,
            actual = JsObjects.getPrototypeOf(array),
        )

        assertEquals(
            expected = 3,
            actual = array.length,
        )

        assertEquals(
            expected = 1,
            actual = array[0],
        )

        assertEquals(
            expected = 2,
            actual = array[1],
        )

        assertEquals(
            expected = 3,
            actual = array[2],
        )

        assertEquals(
            expected = null,
            actual = array[3],
        )
    }
}
