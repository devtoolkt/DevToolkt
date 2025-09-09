package dev.toolkt.core.collections

import dev.toolkt.core.collections.maps.StableMap

typealias EntryHandle<K, V> = StableCollection.Handle<Map.Entry<K, V>>

/**
 * A read-only associative collection providing stable handles to its entries.
 */
interface StableAssociativeCollection<K, out V> : StableCollection<Map.Entry<K, V>>, AssociativeCollection<K, V> {
    /**
     * Returns handles to the entries corresponding to the given key.
     * Guarantees linear time complexity or better.
     */
    fun resolveAll(
        key: K,
    ): Collection<EntryHandle<K, @UnsafeVariance V>>
}

/**
 * Returns the value corresponding to the given handle.
 * Guarantees constant time complexity.
 *
 * @return the value corresponding to the handle, or null if the corresponding
 * entry was already removed
 */
fun <K, V : Any> StableAssociativeCollection<K, V>.getValueVia(
    handle: EntryHandle<K, V>,
): V? {
    val entry = getVia(handle = handle)
    return entry?.value
}

/**
 * Returns the value corresponding to the given key, along with a handle to the entry.
 *
 * @return a pair containing the value and the handle to the entry, or null if there is no such entry
 */
fun <K, V : Any> StableMap<K, V>.getWithHandle(
    key: K,
): Pair<V, EntryHandle<K, V>>? {
    val entryHandle = resolve(key = key) ?: return null

    val value = getValueVia(handle = entryHandle) ?: throw AssertionError("The handle is immediately invalid")

    return Pair(value, entryHandle)
}
