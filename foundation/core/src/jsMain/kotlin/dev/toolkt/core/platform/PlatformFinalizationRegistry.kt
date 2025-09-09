@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package dev.toolkt.core.platform

import kotlin.js.JsName

actual class PlatformFinalizationRegistry {
    private val finalizationRegistry = FinalizationRegistry { heldCleanup ->
        heldCleanup()
    }

    @JsName("register")
    actual fun register(
        target: Any,
        cleanup: () -> Unit,
    ): PlatformCleanable {
        finalizationRegistry.register(
            target = target,
            heldValue = cleanup,
            unregisterToken = cleanup,
        )

        return object : PlatformCleanable {
            private var wasCleaned = false

            override fun clean() {
                if (wasCleaned) {
                    return
                }

                finalizationRegistry.unregister(
                    unregisterToken = cleanup,
                )

                cleanup()

                wasCleaned = true
            }

            override fun unregister() {
                if (wasCleaned) {
                    return
                }

                finalizationRegistry.unregister(
                    unregisterToken = cleanup,
                )

                wasCleaned = true
            }
        }
    }
}
