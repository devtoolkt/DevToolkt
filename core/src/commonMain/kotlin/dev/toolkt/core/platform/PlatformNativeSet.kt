package dev.toolkt.core.platform

/**
 * A high-performance set implementation intended for use cases where maximum speed and minimal overhead are required.
 *
 * This class intentionally does NOT implement the Kotlin [Collection] interface to avoid unnecessary abstraction and overhead.
 * It is designed to be used only with primitive types or classes that use the default implementations of [Any.equals] and [Any.hashCode].
 *
 * Using custom [Any.equals] and [Any.hashCode] implementations may result in incorrect behavior.
 */
expect class PlatformNativeSet<E : Any>() {
    val size: Int

    /**
     * Adds the specified value to this set (if it is not already present).
     *
     * @return `true` if the value was added, `false` if it was already present.
     */
    fun add(value: E): Boolean

    /**
     * Ensures that the specified value is present in this set. If the value is
     * not present, it will be added.
     *
     * @param value the value to ensure is present in the set.
     */
    fun ensureContains(value: E)

    /**
     * Removes the specified value from this set (if it is present).
     *
     * @return `true` if the value was removed, `false` if it was not present.
     */
    fun remove(value: E): Boolean

    /**
     * Checks if the specified value is present in this set.
     *
     * @return `true` if the value is present, `false` otherwise.
     */
    operator fun contains(value: E): Boolean

    /**
     * Clears all elements from this set.
     */
    fun clear()

    /**
     * Iterates over each element in this set and applies the given callback function.
     *
     * @param callback the function to apply to each element.
     */
    fun forEach(callback: (E) -> Unit)

    /**
     * Creates a copy of this set.
     *
     * @return a new instance of [PlatformNativeSet] containing the same elements.
     */
    fun copy(): PlatformNativeSet<E>
}

fun <E : Any> PlatformNativeSet<E>.addAll(
    elements: Iterable<E>,
) {
    for (element in elements) {
        add(element)
    }
}

fun <E : Any> PlatformNativeSet<E>.addAll(
    elements: PlatformNativeSet<E>,
) {
    elements.forEach { element ->
        add(element)
    }
}

fun <E : Any> platformNativeSetOf(): PlatformNativeSet<E> = PlatformNativeSet()
