package dev.toolkt.core.platform.test_utils

import dev.toolkt.core.platform.PlatformSystem
import dev.toolkt.core.platform.PlatformWeakReference
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

enum class WaitUntilResult {
    Timeout, ConditionMet,
}

/**
 * Waits until the [predicate] returns `true`, checking it every [pauseDuration] for a maximum of [timeoutDuration].
 * Puts stress on the garbage collector.
 *
 * @return [WaitUntilResult.ConditionMet] if the predicate returned `true` within the timeout, or [WaitUntilResult.Timeout] if it did not.
 */
suspend fun waitUntil(
    pauseDuration: Duration,
    timeoutDuration: Duration,
    predicate: () -> Boolean,
): WaitUntilResult {
    val tryCount = (timeoutDuration / pauseDuration).roundToInt()

    (tryCount downTo 0).forEach { tryIndex ->
        delay(pauseDuration)
        PlatformSystem.collectGarbage()

        if (predicate()) {
            return WaitUntilResult.ConditionMet
        }
    }

    return WaitUntilResult.Timeout
}

/**
 * Ensures that the object referenced by [weakRef] is not collected by the garbage collector, despite the stress applied.
 */
suspend fun <T : Any> ensureNotCollected(
    weakRef: PlatformWeakReference<T>,
): T {
    // It was empirically proven that a system under test which does NOT
    // correctly ensure that the object is not collected fails this test
    // virtually immediately (after a single iteration)

    val result = waitUntil(
        pauseDuration = 1.milliseconds,
        timeoutDuration = 10.milliseconds,
    ) {
        // If GC doesn't happen within the testing duration (even though
        // stress is applied on GC), it doesn't formally prove the correctness
        // of the system under test, but in practice it should be enough

        weakRef.get() == null
    }

    assertEquals(
        expected = WaitUntilResult.Timeout,
        actual = result,
    )

    // TODO: Improve it, remove the null assertion
    return weakRef.get()!!
}

suspend fun <T : Any> ensureCollected(
    weakRef: PlatformWeakReference<T>,
) {
    val result = waitUntil(
        pauseDuration = 50.milliseconds,
        timeoutDuration = 5.seconds,
    ) {
        weakRef.get() == null
    }

    assertEquals(
        expected = WaitUntilResult.ConditionMet,
        actual = result,
    )
}

suspend fun <T : Any> assertCollected(
    weakRef: PlatformWeakReference<T>,
) {
    val maxTryCount = 8

    @Suppress("VariableNeverRead") var garbage: Any? = null

    @Suppress("AssignedValueIsNeverRead")
    fun collectGarbage(size: Int) {
        garbage = List(size = size) { 1 }
    }

    tailrec suspend fun assertCollectedRecursively(
        tryIndex: Int,
    ) {
        val ref = weakRef.get() ?: return

        if (tryIndex == maxTryCount) {
            throw AssertionError("Expected the object to be garbage collected: $ref")
        }

        val garbageSize = tryIndex * 1_000_000

        PlatformSystem.log("Garbage size: $garbageSize")

        collectGarbage(garbageSize)

        val duration = (tryIndex * 10).milliseconds

        PlatformSystem.log("Waiting for: ${duration.inWholeMilliseconds} ms")

        delay(duration)

        return assertCollectedRecursively(
            tryIndex = tryIndex + 1,
        )
    }

    return assertCollectedRecursively(
        tryIndex = 0,
    )
}

/**
 * Asserts that the object referenced by [weakRef] is collected by the garbage collector.
 *
 * This function will keep checking if the object is collected, doubling the sleep duration
 * between checks.
 */
suspend fun <T : Any> assertCollectedEb(
    weakRef: PlatformWeakReference<T>,
) {
    tailrec suspend fun assertCollectedRecursively(
        sleepDuration: Duration,
    ) {
        if (weakRef.get() == null) {
            return
        }

        delay(sleepDuration)

        assertCollectedRecursively(
            sleepDuration = sleepDuration * 2,
        )
    }

    return assertCollectedRecursively(
        sleepDuration = 10.milliseconds,
    )
}

/**
 * Waits for [waitDuration], waking up every [pauseDuration] and putting stress on the garbage collector.
 */
suspend fun waitBusy(
    pauseDuration: Duration,
    waitDuration: Duration,
) {
    val tryCount = (waitDuration / pauseDuration).roundToInt()

    (tryCount downTo 0).forEach { tryIndex ->
        PlatformSystem.collectGarbage()
        delay(pauseDuration)
    }
}
