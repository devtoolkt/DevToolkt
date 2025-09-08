package dev.toolkt.core.collections.sets

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MutableTreeSetTests {
    @Test
    fun testInitial() {
        val set = mutableTreeSetOf<Int>()

        set.verifyContent(
            elements = emptyList(),
            controlElements = setOf(10, 20, 30),
        )
    }

    @Test
    fun testLookup() {
        val set = mutableTreeSetOf(
            10, 20, 30, 40,
        )

        val handle20 = assertNotNull(
            actual = set.lookup(20),
        )

        assertEquals(
            expected = 20,
            actual = set.getVia(handle = handle20),
        )
    }

    @Test
    fun testAdd_empty() {
        val set = mutableTreeSetOf<Int>()

        assertTrue(
            actual = set.add(10),
        )

        set.verifyContent(
            elements = listOf(10),
            controlElements = setOf(20, 30),
        )
    }

    @Test
    fun testAdd_nonEmpty() {
        val set = mutableTreeSetOf<Int>()

        set.addAll(
            listOf(
                10,
                20,
                30,
            ),
        )

        assertTrue(
            actual = set.add(15),
        )

        set.verifyContent(
            elements = listOf(10, 15, 20, 30),
            controlElements = setOf(-10, 40, 50),
        )
    }

    @Test
    fun testAddEx_duplicate() {
        val set = mutableTreeSetOf(
            10, 15, 20, 30,
        )

        assertNull(
            actual = set.addEx(
                element = 20,
            ),
        )

        set.verifyContent(
            elements = listOf(10, 15, 20, 30),
            controlElements = setOf(-10, 40, 50),
        )
    }

    @Test
    fun testAddEx_nonDuplicate() {
        val set = mutableTreeSetOf(
            10, 15, 20, 30,
        )

        val handle = assertNotNull(
            actual = set.addEx(
                element = 25,
            ),
        )

        assertEquals(
            expected = 25,
            actual = set.getVia(handle = handle),
        )

        set.verifyContent(
            elements = listOf(10, 15, 20, 25, 30),
            controlElements = setOf(-10, 40, 50),
        )
    }

    @Test
    fun testRemove() {
        val set = mutableTreeSetOf<Int>()

        set.addAll(
            listOf(
                10,
                20,
                30,
            ),
        )

        assertTrue(
            actual = set.remove(20),
        )

        set.verifyContent(
            elements = listOf(10, 30),
            controlElements = setOf(20, 40, 50),
        )

        assertTrue(
            actual = set.remove(10),
        )

        set.verifyContent(
            elements = listOf(30),
            controlElements = setOf(10, 20, 40, 50),
        )

        assertTrue(
            actual = set.remove(30),
        )

        set.verifyContent(
            elements = emptyList(),
            controlElements = setOf(10, 20, 30, 40, 50),
        )
    }

    @Test
    fun testRemoveVia() {
        val set = mutableTreeSetOf(
            10, 15, 20, 30,
        )

        val handle15 = assertNotNull(
            actual = set.lookup(element = 15),
        )

        assertEquals(
            expected = 15,
            actual = set.removeVia(handle = handle15),
        )

        assertNull(
            actual = set.getVia(handle = handle15),
        )

        assertNull(
            actual = set.removeVia(handle = handle15),
        )

        set.verifyContent(
            elements = listOf(10, 20, 30),
            controlElements = setOf(15, -20),
        )
    }

    @Test
    fun testRemove_notContained() {
        val set = mutableTreeSetOf<Int>()

        set.addAll(
            listOf(
                10,
                20,
                30,
            ),
        )

        assertFalse(
            actual = set.remove(40),
        )

        set.verifyContent(
            elements = listOf(10, 20, 30),
            controlElements = setOf(40, 50),
        )
    }
}
