package dev.toolkt.reactive

import kotlin.test.Test
import kotlin.test.assertEquals

class FooTests {
    @Test
    fun testFoo() {
        assertEquals(
            expected = 42,
            actual = foo(),
        )
    }
}
