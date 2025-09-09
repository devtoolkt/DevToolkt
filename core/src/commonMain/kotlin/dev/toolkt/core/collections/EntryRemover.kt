package dev.toolkt.core.collections

import dev.toolkt.core.platform.PlatformWeakReference

interface EntryRemover<V : Any> {
    /**
     * Removes the entry associated with a given key from the map.
     *
     * @return the value that was associated with the key, or null if the key
     * was not present.
     */
    fun remove(): V?
}

/**
 * Removes the entry associated with the given key from the map, throwing an
 * exception if the key was not present.
 *
 * @throws IllegalStateException if the entry was not present in the map.
 */
fun <V : Any> EntryRemover<V>.removeEffectively() {
    this.remove() ?: throw IllegalStateException("The map didn't contain the given key")
}

/**
 * Inserts the entry into the map, throwing an exception if the key was already
 * present.
 *
 * @return an [ElementRemover] that can be used to remove the entry associated
 * with the given key later.
 *
 * @throws IllegalStateException if the key was already present in the map.
 */
fun <K, V : Any> MutableMap<K, V>.insertEffectively(
    key: K,
    value: V,
): EntryRemover<V> {
    val previousValue = this.put(
        key = key,
        value = value,
    )

    if (previousValue != null) {
        throw IllegalStateException("The map already contains the key: $key")
    }

    return object : EntryRemover<V> {
        override fun remove(): V? = this@insertEffectively.remove(key)
    }
}

/**
 * Inserts the entry into the map without keeping a strong reference to the key
 * (or the value), throwing an exception if the key was already present.
 *
 * @return an [ElementRemover] that can be used to remove the entry associated
 * with the given key later.
 *
 * @throws IllegalStateException if the key was already present in the map.
 */
fun <K: Any, V : Any> MutableMap<K, V>.insertEffectivelyWeak(
    key: K,
    value: V,
): EntryRemover<V> {
    val previousValue = this.put(
        key = key,
        value = value,
    )

    if (previousValue != null) {
        throw IllegalStateException("The map already contains the key: $key")
    }

    val keyWeakRef = PlatformWeakReference(key)

    return object : EntryRemover<V> {
        override fun remove(): V? {
            // If the key was collected, it's not possible that the map contains it
            val key = keyWeakRef.get() ?: return null

            return this@insertEffectivelyWeak.remove(key)
        }
    }
}
