package dev.toolkt.core.platform

import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

actual object PlatformSystem {
    // These constants were increased whenever the lower value wasn't enough on the JS target
    // It's still not clear what's the "right" combination and how system-specific is it.
    private const val gcTriggerCount = 2
    private const val garbageObjectCount = 8_000_000

    private var garbage: Any? = null

    actual fun collectGarbage() {
        // On JavaScript (unlike Java) there's no explicit garbage collection call, so this is just a hacky way
        // to create a lot of garbage objects, hoping to trigger the garbage collector.

        garbage = DummyGarbage.build(size = garbageObjectCount)
    }

    actual suspend fun collectGarbageForced() {
        // On the JS target, multiple calls with a big garbage object are needed to trigger the GC reliably.

        repeat(gcTriggerCount) {
            collectGarbage()
            delay(1.milliseconds)
        }
    }

    actual fun log(value: Any?) {
        console.log(value)
    }

    actual fun logError(value: Any?) {
        console.error(value)
    }
}

private class DummyGarbage(
    val number: Int = 1,
) {
    companion object {
        fun build(
            size: Int,
        ): Array<DummyGarbage> = Array(size) { DummyGarbage() }
    }
}
