package dev.toolkt.core.collections.maps

import dev.toolkt.core.collections.AssociativeCollection

/**
 * A collection associating a set of keys with a set of values in a one-to-one relation.
 */
interface AssociativeMap<K, out V> : AssociativeCollection<K, V>, Map<K, V> {
    /**
     * Gets all values associated with the specified key.
     * Guarantees logarithmic time complexity or better (assuming a small number of values per key).
     */
    override fun getAll(key: K): Collection<V>
}

fun <K, V : Any> Map<K, V>.asAssociativeMap(): AssociativeMap<K, V> = AssociativeMapView(map = this)

abstract class AbstractAssociativeMap<K, out V> : Map<K, V>, Collection<Map.Entry<K, V>>, AssociativeMap<K, V> {
    override fun contains(
        element: Map.Entry<K, @UnsafeVariance V>,
    ): Boolean = entries.contains(element)

    final override fun iterator(): Iterator<Map.Entry<K, V>> = entries.iterator()

    final override fun containsAll(elements: Collection<Map.Entry<K, @UnsafeVariance V>>): Boolean =
        entries.containsAll(elements)

    final override fun getAll(key: K): Collection<V> = listOfNotNull(this[key])
}

private class AssociativeMapView<K, out V : Any>(
    private val map: Map<K, V>,
) : AbstractAssociativeMap<K, V>(), Map<K, V> by map
