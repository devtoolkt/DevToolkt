package dev.toolkt.core.platform

/**
 * A high-performance map implementation intended for use cases where maximum speed and minimal overhead are required.
 *
 * This class intentionally does NOT implement the Kotlin [Collection] interface to avoid unnecessary abstraction and overhead.
 * It is designed to be used only with primitive types or classes that use the default implementations of [Any.equals] and [Any.hashCode].
 *
 * Using custom [Any.equals] and [Any.hashCode] implementations may result in incorrect behavior.
 */
expect class PlatformNativeMap<K : Any, V : Any>() {
    val size: Int

    /**
     * Returns the value corresponding to the given [key], or `null` if such a key is not present in the map.
     */
    operator fun get(key: K): V?

    /**
     * Returns `true` if the map contains the specified [key].
     */
    fun containsKey(key: K): Boolean

    /**
     * Returns `true` if the map maps one or more keys to the specified [value].
     */
    fun containsValue(value: V): Boolean

    /**
     * Iterates over each entry in this map and applies the given callback function.
     *
     * @param callback the function to apply to each entry.
     */
    fun forEach(callback: (K, V) -> Unit)

    /**
     * Associates the specified [value] with the specified [key] in the map.
     *
     * @return the previous value associated with the key, or `null` if the key was not present in the map. If this
     * information is not needed, consider using [set] for potentially better performance.
     */
    fun put(key: K, value: V): V?

    /**
     * Associates the specified [value] with the specified [key] in the map. Unlike [put], there's no way to know if the
     * key was already present. This method might have slightly better performance than [put].
     */
    fun set(key: K, value: V)

    /**
     * Removes the specified key and its corresponding value from this map.
     *
     * @return the previous value associated with the key, or `null` if the key was not present in the map. If the
     * exact value is not important, consider using [remove] for potentially better performance.
     */
    fun extract(key: K): V?

    /**
     * Removes the specified key and its corresponding value from this map. Unlike [extract], there's no way to know
     * what the corresponding value was. This method might have slightly better performance than [extract].
     *
     * @return if the entry was removed, `false` if the key was not present in the map.
     */
    fun remove(key: K): Boolean

    /**
     * Clears all entries from this map.
     */
    fun clear()

    /**
     * Creates a copy of this map.
     *
     * @return a new instance of [PlatformNativeMap] containing the same entries.
     */
    fun copy(): PlatformNativeMap<K, V>
}
