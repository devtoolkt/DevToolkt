package dev.toolkt.core.collections

import dev.toolkt.core.collections.maps.MapEntry

/**
 * A mutable collection associating a set of keys with a set of values.
 *
 * See [dev.toolkt.core.collections.maps.MutableAssociativeMap] for an implementation offering one-to-one relation.
 * See [dev.toolkt.core.collections.maps.MutableMultiValuedMap] for an implementation offering one-to-many relation.
 *
 * @param K the type of collection keys
 * @param V the type of collection values
 */
interface MutableAssociativeCollection<K, V> : AssociativeCollection<K, V>, MutableCollection<Map.Entry<K, V>> {
    /**
     * Adds the specified entry to the collection.
     *
     * @return `true` if the entry has been added, `false` if the entry wasn't added because it collided with another
     * entry
     */
    override fun add(element: Map.Entry<K, V>): Boolean

    /**
     * Adds all the entries of the specified collection to this collection.
     *
     * @return `true` if any of the specified elements was added to the collection, `false` if the collection was not modified.
     */
    override fun addAll(elements: Collection<Map.Entry<K, V>>): Boolean

    /**
     * Remove all entries with the given key from the collection.
     * Guarantees linear time complexity or better.
     *
     * @return `true` if the key was removed, or `false` if the collection contained no entry with such key
     */
    fun removeKey(key: K): Boolean
}

fun <K, V> MutableAssociativeCollection<K, V>.add(
    key: K,
    value: V,
): Boolean = this.add(
    element = MapEntry(key = key, value = value),
)

fun <K, V> MutableAssociativeCollection<K, V>.remove(
    key: K,
    value: V,
): Boolean = this.remove(
    element = MapEntry(key = key, value = value),
)
