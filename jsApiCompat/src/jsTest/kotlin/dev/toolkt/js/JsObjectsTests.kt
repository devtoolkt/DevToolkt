package dev.toolkt.js

import kotlin.js.collections.JsArray
import kotlin.js.collections.toList
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class JsObjectsTests {
    @Test
    fun testKeys() {
        val obj = jsObject()

        obj.foo = 1
        obj.bar = "A"

        assertEquals(
            expected = listOf("foo", "bar"),
            actual = JsObjects.keys(obj).toList(),
        )
    }

    @Test
    fun testValues() {
        val obj = jsObject()

        obj.foo = 1
        obj.bar = "A"

        assertEquals(
            expected = listOf(1, "A"),
            actual = JsObjects.values(obj).toList(),
        )
    }

    @Test
    fun testEntries() {
        val obj = jsObject()

        obj.foo = 1
        obj.bar = "A"

        assertEquals(
            expected = listOf(
                listOf("foo", 1),
                listOf("bar", "A"),
            ),
            actual = JsObjects.entries(obj).toList().map { entryArray: JsArray<Any?> ->
                entryArray.toList()
            }
        )
    }
}
