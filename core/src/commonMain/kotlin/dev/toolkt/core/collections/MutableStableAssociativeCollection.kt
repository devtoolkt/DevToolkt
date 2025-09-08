package dev.toolkt.core.collections

import dev.toolkt.core.collections.maps.MapEntry

/**
 * A mutable associative collection providing stable handles to its elements.
 */
interface MutableStableAssociativeCollection<K, V> : MutableStableCollection<Map.Entry<K, V>>,
    StableAssociativeCollection<K, V>, MutableAssociativeCollection<K, V>

/**
 * Adds the specified key-value entry to the collection in exchange for a handle.
 * Guarantees logarithmic time complexity or better.
 *
 * @return the handle to the added entry or `null` if the entry wasn't added because it conflicts with another entry.
 */
fun <K, V> MutableStableAssociativeCollection<K, V>.addEx(
    key: K,
    value: V,
): EntryHandle<K, @UnsafeVariance V>? = this.addEx(
    element = MapEntry(key = key, value = value),
)
