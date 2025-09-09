package dev.toolkt.js

import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class ObjectTests {
    @Test
    @Ignore // TODO: Add proper support for JsArray
    fun testKeys() {
        val obj = jsObject()

        obj.foo = 1
        obj.bar = "A"

        assertEquals(
            expected = arrayOf("foo", "bar"),
            actual = Object.keys(obj),
        )
    }

    @Test
    @Ignore // TODO: Add proper support for JsArray
    fun testValues() {
        val obj = jsObject()

        obj.foo = 1
        obj.bar = "A"

        assertEquals(
            expected = arrayOf(1, "A"),
            actual = Object.values(obj),
        )
    }
}
