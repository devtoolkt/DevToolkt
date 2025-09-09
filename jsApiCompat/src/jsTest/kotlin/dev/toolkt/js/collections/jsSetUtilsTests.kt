package dev.toolkt.js.collections

import dev.toolkt.js.Object
import kotlin.js.collections.JsSet
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JsSetUtilsTests {
    val setConstructor = js("Set")

    @Test
    fun testJsSetConstructorEmpty() {
        val set: JsSet<Int> = JsSet()

        assertEquals(
            expected = setConstructor.prototype,
            actual = Object.getPrototypeOf(set),
        )

        assertEquals(
            expected = 0,
            actual = set.size,
        )

        assertFalse(
            actual = set.has("foo"),
        )

        val mutableVisitedElements = mutableListOf<Any>()

        set.forEach {
            mutableVisitedElements.add(it)
        }

        assertEquals(
            expected = emptyList(),
            actual = mutableVisitedElements,
        )
    }

    @Test
    fun testJsSetConstructorCopying() {
        val originalSet: JsSet<Int> = JsSet<Int>().apply {
            add(1)
            add(2)
            add(3)
        }

        val set: JsSet<Int> = JsSet(originalSet)

        assertEquals(
            expected = setConstructor.prototype,
            actual = Object.getPrototypeOf(set),
        )

        assertEquals(
            expected = 3,
            actual = set.size,
        )
        assertTrue(
            actual = set.has(1),
        )

        assertTrue(
            actual = set.has(2),
        )

        assertTrue(
            actual = set.has(3),
        )

        assertFalse(
            actual = set.has(4),
        )

        val mutableVisitedElements = mutableListOf<Any>()

        set.forEach {
            mutableVisitedElements.add(it)
        }

        assertEquals(
            expected = listOf<Any>(1, 2, 3),
            actual = mutableVisitedElements,
        )

        // Modify the copied set
        assertTrue(
            actual = set.delete(2),
        )

        // Ensure that the original set is not affected
        assertTrue(
            actual = originalSet.has(2),
        )
    }

    @Test
    fun testJsSetAdd() {
        val set: JsSet<Int> = JsSet()

        set.add(1)
        set.add(2)
        set.add(3)

        assertEquals(
            expected = 3,
            actual = set.size,
        )

        assertTrue(
            actual = set.has(1),
        )

        assertTrue(
            actual = set.has(2),
        )

        assertTrue(
            actual = set.has(3),
        )

        assertFalse(
            actual = set.has(4),
        )

        val mutableVisitedElements = mutableListOf<Any>()

        set.forEach {
            mutableVisitedElements.add(it)
        }

        assertEquals(
            expected = listOf<Any>(1, 2, 3),
            actual = mutableVisitedElements,
        )
    }

    @Test
    fun testJsSetDelete() {
        val set: JsSet<Int> = JsSet()

        set.add(1)
        set.add(2)
        set.add(3)

        assertEquals(
            expected = 3,
            actual = set.size,
        )

        assertTrue(
            actual = set.delete(2),
        )

        assertEquals(
            expected = 2,
            actual = set.size,
        )

        assertTrue(
            actual = set.has(1),
        )

        assertFalse(
            actual = set.has(2),
        )

        assertTrue(
            actual = set.has(3),
        )

        assertFalse(
            actual = set.delete(4),
        )

        assertEquals(
            expected = 2,
            actual = set.size,
        )

        val mutableVisitedElements = mutableListOf<Any>()

        set.forEach {
            mutableVisitedElements.add(it)
        }

        assertEquals(
            expected = listOf<Any>(1, 3),
            actual = mutableVisitedElements,
        )
    }

    @Test
    fun testJsSetClear() {
        val set: JsSet<Int> = JsSet()

        set.add(1)
        set.add(2)
        set.add(3)

        assertEquals(
            expected = 3,
            actual = set.size,
        )

        set.clear()

        assertEquals(
            expected = 0,
            actual = set.size,
        )

        assertFalse(
            actual = set.has(1),
        )

        assertFalse(
            actual = set.has(2),
        )

        assertFalse(
            actual = set.has(3),
        )

        val mutableVisitedElements = mutableListOf<Any>()

        set.forEach {
            mutableVisitedElements.add(it)
        }

        assertEquals(
            expected = emptyList<Any>(),
            actual = mutableVisitedElements,
        )
    }
}
