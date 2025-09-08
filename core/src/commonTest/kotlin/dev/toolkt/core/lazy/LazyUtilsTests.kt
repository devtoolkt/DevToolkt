package dev.toolkt.core.lazy

import kotlin.test.Test
import kotlin.test.assertEquals

class LazyUtilsTests {
    /**
     * An example entity class that benefits from looping
     */
    private data class TestEntity(
        val param: Int,
        val lazyValue: Lazy<Int>,
    ) {
        fun calculateValue1(): Int = param * 2

        fun calculateValue2(): Int = lazyValue.value * 3
    }

    @Test
    fun testLooped() {
        val testEntity = LazyUtils.looped { loopedValue: Lazy<Int> ->
            val testEntity = TestEntity(
                param = 5,
                lazyValue = loopedValue,
            )

            Pair(
                testEntity,
                testEntity.calculateValue1(),
            )
        }

        assertEquals(
            expected = 30, // 5 * 2 * 3
            actual = testEntity.calculateValue2(),
        )
    }
}
