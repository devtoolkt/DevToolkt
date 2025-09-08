package dev.toolkt.core.platform.test_utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

suspend fun <T> assertEqualsEventually(
    pauseDuration: Duration,
    timeoutDuration: Duration,
    expected: T,
    actual: () -> T,
) {
    val result = waitUntil(
        pauseDuration = pauseDuration,
        timeoutDuration = timeoutDuration,
    ) {
        actual() == expected
    }

    when (result) {
        WaitUntilResult.Timeout -> {
            throw AssertionError("Expected value to eventually become $expected, but it is at ${actual()} after checking every $pauseDuration for $timeoutDuration")
        }

        WaitUntilResult.ConditionMet -> {}
    }
}

/**
 * Executes [testBody] as a test in a new coroutine, using the default dispatcher. This disables the delay-skipping
 * behavior.
 *
 * The platform difference entails that, in order to use this function correctly in common code, one must always
 * immediately return the produced [TestResult] from the test method, without doing anything else afterward. See
 * [TestResult] for details on this.
 */
// The return-immediately part of the contract is inherited from `runTest`
fun <T> runTestDefault(
    timeout: Duration = 10.seconds,
    testBody: suspend CoroutineScope.() -> T,
): TestResult = runTest(
    timeout = timeout,
) {
    // Although `runTest` accepts a `context` parameter, it throws an exception when `Dispatchers.Default` is passed
    withContext(Dispatchers.Default.limitedParallelism(1)) {
        testBody()
    }
}
