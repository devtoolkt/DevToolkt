package dev.toolkt.core.collections.maps

import dev.toolkt.core.collections.AssociativeCollection

/**
 * A collection associating a set of keys with a set of values in a one-to-many relation.
 */
interface MultiValuedMap<K, out V> : AssociativeCollection<K, V> {
    /**
     * Returns a view of this multivalued map as a Map from each distinct key to the non-empty collection of that key's associated values.
     */
    fun asMap(): Map<K, Collection<V>>

    /**
     * Returns true if this map contains a mapping for the specified key.
     *
     * Guarantees logarithmic time complexity or better.
     */
    override fun containsKey(key: K): Boolean

    /**
     * Gets all values associated with the specified key.
     * Guarantees logarithmic time complexity or better.
     */
    override fun getAll(key: K): Collection<V>

    /**
     * A Set view of the keys contained in this multivalued map.
     */
    val keys: Set<K>

    /**
     * A collection view of all values contained in this multivalued map.
     */
    val values: Collection<V>
}

/**
 * Checks whether the map contains a mapping for the specified key and value.
 *
 * Guarantees logarithmic time complexity or better (assuming a small number of values per key).
 */
fun <K, V> MultiValuedMap<K, V>.containsMapping(key: K, value: V): Boolean {
    val values = getAll(key = key)
    return values.contains(value)
}
