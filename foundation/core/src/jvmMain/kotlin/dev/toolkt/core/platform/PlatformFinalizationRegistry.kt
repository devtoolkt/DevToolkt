@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package dev.toolkt.core.platform

import java.lang.ref.Cleaner

actual class PlatformFinalizationRegistry {
    private val cleaner = Cleaner.create()

    /**
     * [Cleaner] doesn't offer any way to just unregister a [Cleaner.Cleanable] without invoking the cleaning action,
     * so we wrap the cleaning action in a [Runnable] that can be disabled explicitly.
     */
    class CleanupRunnable(
        private val cleanup: () -> Unit,
    ) : Runnable {
        private var isEnabled = true

        fun disable() {
            isEnabled = false
        }

        override fun run() {
            if (isEnabled) {
                cleanup()
            }
        }
    }

    actual fun register(
        target: Any,
        cleanup: () -> Unit,
    ): PlatformCleanable {
        val cleanupRunnable = CleanupRunnable(cleanup = cleanup)

        val cleanable = cleaner.register(target, cleanupRunnable)

        return object : PlatformCleanable {
            override fun clean() {
                cleanable.clean()
            }

            override fun unregister() {
                cleanupRunnable.disable()
                cleanable.clean()
            }
        }
    }
}
