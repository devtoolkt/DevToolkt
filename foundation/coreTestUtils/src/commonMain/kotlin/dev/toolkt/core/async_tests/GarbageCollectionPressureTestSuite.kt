package dev.toolkt.core.async_tests

import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

abstract class GarbageCollectionPressureTestSuite : AsyncTestSuite() {
    /**
     * Put stress on the garbage collector by continuously allocating memory.
     * If we didn't do this, the garbage collector might not run at all during
     * the tests.
     */
    @Suppress("AssignedValueIsNeverRead")
    final override suspend fun runInBackground() {
        val minSize = 128 * 1024 // 128 KiB
        val maxSize = 8 * 1024 * 1024 // 8 MiB

        val minDelay = 1.milliseconds
        val maxDelay = 100.milliseconds

        @Suppress("VariableNeverRead") var garbageArray: Any? = null

        var delay = minDelay
        var size = minSize

        while (true) {
            garbageArray = List(size) { 0 }

            delay(delay)

            size = (size * 2).coerceAtMost(maxSize)
            delay = (delay * 2).coerceAtMost(maxDelay)
        }
    }
}
