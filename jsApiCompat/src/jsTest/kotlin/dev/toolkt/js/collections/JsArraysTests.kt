package dev.toolkt.js.collections

import kotlin.test.Test
import kotlin.test.assertEquals

class JsArraysTests {
    @Test
    fun testOf() {
        val array = JsArrays.of(1, 2, 3)

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
