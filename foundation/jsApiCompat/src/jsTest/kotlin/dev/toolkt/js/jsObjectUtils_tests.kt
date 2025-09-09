package dev.toolkt.js

import kotlin.js.collections.toList
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("ClassName")
class jsObjectUtils_tests {
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

    @Test
    fun testJsObjectBlock() {
        val obj = jsObject {
            foo = 1
            bar = "A"
        }

        assertEquals(
            expected = objectConstructor.prototype,
            actual = JsObjects.getPrototypeOf(obj),
        )

        assertEquals(
            expected = 1,
            actual = obj.foo,
        )

        assertEquals(
            expected = "A",
            actual = obj.bar,
        )

        assertEquals(
            expected = listOf(
                listOf("foo", 1),
                listOf("bar", "A"),
            ),
            actual = JsObjects.entries(obj).toList().map { entryArray ->
                entryArray.toList()
            },
        )
    }
}
