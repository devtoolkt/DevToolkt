package dev.toolkt.core.collections

/**
 * A read-only map providing stable handles to its entries.
 */
interface StableMap<K, out V> : StableAssociativeCollection<K, V>, StableSet<Map.Entry<K, V>>, AssociativeMap<K, V> {
    /**
     * Returns a handle to the entry corresponding to the given key. The handle
     * is valid right after being resolved.
     * Guarantees linear time complexity or better.
     *
     * @return the handle to the entry corresponding to the key, or null if there is no such entry
     */
    fun resolve(
        key: K,
    ): EntryHandle<K, @UnsafeVariance V>?
}
