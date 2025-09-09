package dev.toolkt.core.platform

actual class PlatformNativeSet<E : Any>(
    private val mutableSet: MutableSet<E> = mutableSetOf<E>(),
) {
    actual constructor() : this(mutableSet = mutableSetOf<E>())

    actual val size: Int
        get() = mutableSet.size

    actual fun add(value: E): Boolean = mutableSet.add(value)

    actual fun ensureContains(value: E) {
        mutableSet.add(value)
    }

    actual fun remove(value: E): Boolean = mutableSet.remove(value)

    actual operator fun contains(value: E): Boolean = mutableSet.contains(value)

    actual fun clear() {
        mutableSet.clear()
    }

    actual fun forEach(callback: (E) -> Unit) {
        mutableSet.forEach(callback)
    }

    actual fun copy(): PlatformNativeSet<E> = PlatformNativeSet(mutableSet = mutableSet.toMutableSet())
}
