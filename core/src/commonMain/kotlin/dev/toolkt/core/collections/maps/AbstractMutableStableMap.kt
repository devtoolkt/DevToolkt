package dev.toolkt.core.collections.maps

import dev.toolkt.core.collections.EntryHandle
import dev.toolkt.core.collections.MutableStableCollection
import dev.toolkt.core.collections.StableCollection
import dev.toolkt.core.collections.maps.StableMap

abstract class AbstractMutableStableMap<K, V>(
    final override val entries: MutableSet<MutableMap.MutableEntry<K, V>>,
) : AbstractMutableMap<K, V>(), MutableStableMap<K, V>, Collection<Map.Entry<K, V>> by entries {

    final override fun iterator(): MutableIterator<Map.Entry<K, V>> = entries.iterator()

    final override fun isEmpty(): Boolean = size == 0

    final override fun get(key: K): V? {
        val entryHandle = resolve(key = key) ?: return null

        return getVia(handle = entryHandle)?.value
    }

    final override fun remove(
        element: Map.Entry<K, V>,
    ): Boolean = entries.remove(element)

    final override fun add(
        element: Map.Entry<K, V>,
    ): Boolean {
        val (key, value) = element

        val previousValue = put(
            key = key,
            value = value,
        )

        return previousValue == null
    }

    final override fun addAll(
        elements: Collection<Map.Entry<K, V>>,
    ): Boolean = elements.any {
        val insertedHandle = addEx(it)

        insertedHandle != null
    }

    final override fun removeAll(
        elements: Collection<Map.Entry<K, V>>,
    ): Boolean = entries.removeAll(elements)

    final override fun retainAll(
        elements: Collection<Map.Entry<K, V>>,
    ): Boolean = entries.retainAll(elements)

    final override fun resolveAll(
        key: K,
    ): Collection<EntryHandle<K, V>> = listOfNotNull(resolve(key = key))

    final override fun lookup(
        element: Map.Entry<K, V>,
    ): EntryHandle<K, V>? {
        val key = element.key
        return resolve(key = key)
    }

    final override fun getAll(
        key: K,
    ): Collection<V> = listOfNotNull(this[key])

    abstract override val size: Int
}
