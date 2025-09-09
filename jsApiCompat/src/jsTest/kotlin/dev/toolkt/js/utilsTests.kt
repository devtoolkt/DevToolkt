package dev.toolkt.js

import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class UtilsTests {
    @Test
    @Ignore // TODO: Add proper support for JsArrays
    fun testJsObject() {
        val obj = jsObject()

        assertEquals(
            expected = "object",
            actual = jsTypeOf(obj),
        )

        assertEquals(
            expected = emptyArray(),
            actual = JsObjects.keys(obj),
        )
    }
}
