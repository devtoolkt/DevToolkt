package dev.toolkt.core

import dev.toolkt.core.async_tests.GarbageCollectionPressureTestSuite
import dev.toolkt.core.async_tests.runTestAsyncSuite
import dev.toolkt.core.platform.PlatformFinalizationRegistryTestGroup
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data object CoreGarbageCollectionTestSuite : GarbageCollectionPressureTestSuite() {
    override val timeout: Duration = 10.seconds

    override val groups = listOf(
        PlatformFinalizationRegistryTestGroup,
    )
}

class GarbageCollectionTests {
    @Test
    fun testGarbageCollection() = runTestAsyncSuite(
        suite = CoreGarbageCollectionTestSuite,
    )
}
