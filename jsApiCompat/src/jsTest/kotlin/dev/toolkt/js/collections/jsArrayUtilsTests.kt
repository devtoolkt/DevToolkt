package dev.toolkt.js.collections

import kotlin.js.collections.JsArray
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class JsArrayUtilsTests {
    private data class ForEachCallbackTuple<E>(
        val element: E,
        val index: Int,
        val array: JsArray<String>,
    )

    /**
     * Tests the [kotlin.js.collections.JsArray] constructor
     */
    @Test
    fun testJsArrayConstructorEmpty() {
        val array = JsArray<Int>()

        assertEquals(
            expected = 0,
            actual = array.length,
        )

        assertEquals(
            expected = null,
            actual = array[0],
        )
    }

    /**
     * Tests the [dev.toolkt.js.collections.JsArray] constructor
     */
    @Test
    fun testJsArrayConstructionNonEmpty() {
        val array = JsArray(1, 2, 3)

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
    }

    @Test
    fun testPushAndLength() {
        val array = JsArray<Int>()

        assertEquals(
            expected = 0,
            actual = array.length,
        )

        array.push(42)

        assertEquals(
            expected = 1,
            actual = array.length,
        )

        assertEquals(
            expected = 42,
            actual = array[0],
        )
    }

    @Test
    fun testPop() {
        val array = JsArray<Int>().apply {
            push(10)
            push(20)
        }

        val popped = array.pop()

        assertEquals(
            expected = 20,
            actual = popped,
        )

        assertEquals(
            expected = 1,
            actual = array.length,
        )

        assertEquals(
            expected = 10,
            actual = array[0],
        )

        assertEquals(
            expected = 10,
            actual = array.pop(),
        )

        assertEquals(
            expected = 0,
            actual = array.length,
        )

        assertNull(
            actual = array.pop(),
        )
    }

    @Test
    fun testForEach() {
        val array = JsArray("a", "b", "c")

        val result = mutableListOf<ForEachCallbackTuple<String>>()

        array.forEach { element, index, array ->
            result.add(
                ForEachCallbackTuple(
                    element = element,
                    index = index,
                    array = array,
                ),
            )
        }

        assertEquals(
            expected = listOf(
                ForEachCallbackTuple(
                    element = "a",
                    index = 0,
                    array = array,
                ),
                ForEachCallbackTuple(
                    element = "b",
                    index = 1,
                    array = array,
                ),
                ForEachCallbackTuple(
                    element = "c",
                    index = 2,
                    array = array,
                ),
            ),
            actual = result,
        )
    }
}
