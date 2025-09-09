package dev.toolkt.core.collections.maps

import dev.toolkt.core.collections.EntryHandle
import dev.toolkt.core.collections.maps.MapEntry
import dev.toolkt.core.collections.MutableStableAssociativeCollection
import dev.toolkt.core.collections.maps.StableMap

/**
 * A mutable map providing stable handles to its elements.
 */
interface MutableStableMap<K, V> : MutableAssociativeMap<K, V>, MutableStableAssociativeCollection<K, V>,
    StableMap<K, V>

fun <K : Comparable<K>, V> mutableStableMapOf(
    vararg pairs: Pair<K, V>,
): MutableTreeMap<K, V> = mutableTreeMapOf(*pairs)

fun <K, V> MutableStableMap<K, V>.addEx(
    key: K,
    value: V,
): EntryHandle<K, V>? = addEx(
    element = MapEntry(
        key = key,
        value = value,
    ),
)
