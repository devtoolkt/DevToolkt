package dev.toolkt.core.platform

import kotlinx.coroutines.yield

actual object PlatformSystem {
    actual fun collectGarbage() {
        System.gc()
    }

    actual suspend fun collectGarbageForced() {
        yield()

        System.gc()
    }

    actual fun log(value: Any?) {
        System.console().writer().print(value)
    }

    actual fun logError(value: Any?) {
        System.err.print(value)
    }
}
