package dev.toolkt.core.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class pairUtilsTests {
    @Test
    fun testSorted() {
        assertEquals(
            expected = Pair(50.0, 50.0),
            actual = Pair(50.0, 50.0).sorted()
        )

        assertEquals(
            expected = Pair(10, 20),
            actual = Pair(10, 20).sorted()
        )

        assertEquals(
            expected = Pair(100, 200),
            actual = Pair(200, 100).sorted()
        )
    }
}