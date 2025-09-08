package dev.toolkt.core.async_tests

import dev.toolkt.core.platform.test_utils.runTestDefault
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

abstract class AsyncTestSuite {
    open val timeout: Duration = 10.seconds

    data class Result(
        val suite: AsyncTestSuite,
        val groupResults: List<AsyncTestGroup.Result>,
    ) {
        fun print() {
            groupResults.forEach { groupResult ->
                groupResult.print()
            }
        }

        val didSucceed: Boolean
            get() = groupResults.all { groupResult ->
                groupResult.didSucceed
            }
    }

    protected abstract val groups: List<AsyncTestGroup>

    suspend fun run(): Result = coroutineScope {
        val backgroundJob = launch { runInBackground() }

        try {
            val groupResults = withTimeout(timeout = timeout) {
                groups.map { group ->
                    async {
                        group.run()
                    }
                }.awaitAll()
            }

            return@coroutineScope Result(
                suite = this@AsyncTestSuite,
                groupResults = groupResults,
            )
        } catch (_: TimeoutCancellationException) {
            throw AssertionError("Async test suite timed out")
        } finally {
            backgroundJob.cancel()
        }
    }

    open suspend fun runInBackground() {}
}

/**
 * Executes the [suite] as a test in a new coroutine, using the default dispatcher. This disables the delay-skipping
 * behavior.
 *
 * The platform difference entails that, in order to use this function correctly in common code, one must always
 * immediately return the produced [TestResult] from the test method, without doing anything else afterward. See
 * [TestResult] for details on this.
 */
fun runTestAsyncSuite(
    suite: AsyncTestSuite,
): TestResult = runTestDefault(
    timeout = suite.timeout * 2,
) {
    val result = suite.run()

    result.print()

    if (!result.didSucceed) {
        throw AssertionError("Some async tests did not pass! ❌")
    }
}
