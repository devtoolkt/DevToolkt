package dev.toolkt.js

import kotlin.js.collections.toList
import kotlin.test.Test
import kotlin.test.assertEquals

class JsObjectUtilsTests {
    private val objectConstructor = js("Object")

    @Test
    fun testJsObject() {
        val obj = jsObject()

        assertEquals(
            expected = objectConstructor.prototype,
            actual = JsObjects.getPrototypeOf(obj),
        )

        assertEquals(
            expected = emptyList(),
            actual = JsObjects.keys(obj).toList(),
        )

        assertEquals(
            expected = emptyList(),
            actual = JsObjects.values(obj).toList(),
        )
    }
}
