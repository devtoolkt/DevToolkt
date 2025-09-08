package dev.toolkt.core.collections.maps

import dev.toolkt.core.collections.maps.MapEntry
import dev.toolkt.core.collections.forceMutable

class MapBackedMultiValuedMap<K, V>(
    // TODO: Switch to a mutable collection for the buckets!
    private val bucketMap: MutableMap<K, MutableSet<V>>,
) : AbstractMutableCollection<Map.Entry<K, V>>(), MutableMultiValuedMap<K, V> {
    // TODO: Figure out if caching breaks the contract (do we claim ownership?)
    private var cachedSize: Int = bucketMap.values.sumOf { it.size }

    override fun clear() {
        bucketMap.clear()

        cachedSize = 0
    }

    override fun remove(
        element: Map.Entry<K, V>,
    ): Boolean {
        val (key, value) = element

        val bucket = bucketMap[key] ?: return false

        if (bucket.isEmpty()) {
            throw AssertionError("Buckets aren't supposed to be empty")
        }

        val wasRemoved = bucket.remove(value)

        if (wasRemoved) {
            cachedSize -= 1
        }

        if (bucket.isEmpty()) {
            val removedBucket = bucketMap.remove(key)

            if (removedBucket == null) {
                throw AssertionError("The bucket wasn't successfully removed")
            }
        }

        return wasRemoved
    }

    override fun asMap(): Map<K, Collection<V>> = bucketMap

    override fun containsKey(
        key: K,
    ): Boolean = bucketMap.containsKey(key)

    override fun getAll(
        key: K,
    ): Collection<V> = bucketMap[key] ?: emptySet()

    override fun isEmpty(): Boolean = bucketMap.isEmpty()

    override val keys: Set<K>
        get() = bucketMap.keys

    override val size: Int
        get() = cachedSize

    override fun iterator(): MutableIterator<Map.Entry<K, V>> = bucketMap.asSequence().flatMap { (key, bucket) ->
        bucket.asSequence().map { value ->
            MapEntry(key, value)
        }
    }.iterator().forceMutable()

    override fun add(
        element: Map.Entry<K, V>,
    ): Boolean {
        val (key, value) = element

        val bucket = bucketMap.getOrPut(key) { mutableSetOf() }

        val wasAdded = bucket.add(value)

        if (wasAdded) {
            cachedSize += 1
        }

        return wasAdded
    }

    override fun removeKey(key: K): Boolean {
        val removedBucket = bucketMap.remove(key)
        return removedBucket != null
    }

    override val values: Collection<V>
        get() = bucketMap.values.flatten()
}
