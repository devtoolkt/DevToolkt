package dev.toolkt.core.platform

import dev.toolkt.core.async_tests.AsyncTest
import dev.toolkt.core.async_tests.AsyncTestGroup
import dev.toolkt.core.platform.test_utils.awaitCollection
import kotlin.test.assertEquals
import kotlin.test.assertNull

data object PlatformFinalizationRegistryTestGroup : AsyncTestGroup() {
    override val tests = listOf(
        CleanOnceTest,
        CleanTwiceTest,
        UnregisterTest,
    )

    data object CleanOnceTest : AsyncTest() {
        override suspend fun execute() {
            val finalizationRegistry = PlatformFinalizationRegistry()

            var cleanCounter = 0

            fun buildArray(): PlatformWeakReference<Array<Int>> {
                val array = Array(1024) { 0 }

                val cleanable = finalizationRegistry.register(
                    target = array,
                ) {
                    ++cleanCounter
                }

                cleanable.clean()

                assertEquals(
                    expected = 1,
                    actual = cleanCounter,
                    message = "The cleanup action should have been invoked once.",
                )

                return PlatformWeakReference(array)
            }

            val arrayWeakRef = buildArray()

            arrayWeakRef.awaitCollection(
                tag = "PlatformFinalizationRegistry/CleanOnce/array",
            )

            assertNull(
                actual = arrayWeakRef.get(),
                message = "The array should have been collected.",
            )

            assertEquals(
                expected = 1,
                actual = cleanCounter,
                message = "The manually cleaned cleanable should not have been invoked again after garbage collection.",
            )
        }
    }

    data object CleanTwiceTest : AsyncTest() {
        override suspend fun execute() {
            val finalizationRegistry = PlatformFinalizationRegistry()

            var cleanCounter = 0

            val array = Array(1024) { 0 }

            val cleanable = finalizationRegistry.register(
                target = array,
            ) {
                ++cleanCounter
            }

            // Clean the cleanable twice
            cleanable.clean()
            cleanable.clean()

            assertEquals(
                expected = 1,
                actual = cleanCounter,
                message = "The cleanup action should have been invoked once.",
            )
        }
    }

    data object UnregisterTest : AsyncTest() {
        override suspend fun execute() {
            val finalizationRegistry = PlatformFinalizationRegistry()

            var cleanCounter = 0

            val arrayWeakRef = Array(1024) { 0 }.let { array ->
                val cleanable = finalizationRegistry.register(
                    target = array,
                ) {
                    ++cleanCounter
                }

                cleanable.unregister()

                // Just to ensure that it doesn't throw or otherwise blow up
                cleanable.unregister()

                assertEquals(
                    expected = 0,
                    actual = cleanCounter,
                    message = "The unregistering should not have invoked the cleanup action.",
                )

                PlatformWeakReference(array)
            }

            arrayWeakRef.awaitCollection(
                tag = "PlatformFinalizationRegistry/Unregister/array",
            )

            assertNull(
                actual = arrayWeakRef.get(),
                message = "The array should have been collected.",
            )

            assertEquals(
                expected = 0,
                actual = cleanCounter,
                message = "The manually unregistered cleanable should not have been invoked also after the garbage collection.",
            )
        }
    }
}
