package dev.toolkt.core.collections

/**
 * A multivalued map providing stable handles to its elements.
 */
interface StableMultiValuedMap<K, out V> : MultiValuedMap<K, V>, StableAssociativeCollection<K, V> {
    /**
     * Returns handles to the entries corresponding to the given key.
     * Guarantees logarithmic time complexity or better (assuming a small number of values per key).
     */
    override fun resolveAll(key: K): Collection<EntryHandle<K, @UnsafeVariance V>>
}
