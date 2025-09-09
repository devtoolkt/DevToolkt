@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package dev.toolkt.core.platform

interface PlatformCleanable {
    /**
     * Unregisters the cleanable and invokes the cleaning action.
     */
    fun clean()

    /**
     * Unregisters the cleanable without invoking the cleaning action.
     */
    fun unregister()
}

expect class PlatformFinalizationRegistry {
    constructor()

    fun register(
        target: Any,
        cleanup: () -> Unit,
    ): PlatformCleanable
}
