package dev.toolkt.core.platform

import dev.toolkt.js.collections.JsSetImpl

actual class PlatformNativeSet<E : Any>(
    private val jsSet: JsSetImpl<E>,
) {
    actual constructor() : this(jsSet = JsSetImpl())

    actual val size: Int
        get() = jsSet.size

    actual fun add(value: E): Boolean = when {
        contains(value) -> false

        else -> {
            jsSet.add(value)

            true
        }
    }

    actual fun ensureContains(value: E) {
        jsSet.add(value)
    }

    actual fun remove(value: E): Boolean = jsSet.delete(value)

    actual operator fun contains(value: E): Boolean = jsSet.has(value)

    actual fun clear() {
        jsSet.clear()
    }

    actual fun forEach(callback: (E) -> Unit) {
        jsSet.forEach(callback)
    }

    actual fun copy(): PlatformNativeSet<E> = PlatformNativeSet(jsSet = JsSetImpl(jsSet))
}
