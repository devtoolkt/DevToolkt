package dev.toolkt.core.collections

import dev.toolkt.core.platform.mutableWeakMapOf

interface MutableMultiValuedMap<K, V> : MultiValuedMap<K, V>, MutableAssociativeCollection<K, V> {
    companion object {
        fun <K, V> newFromMap(
            bucketMap: MutableMap<K, MutableSet<V>>,
        ): MutableMultiValuedMap<K, V> = MapBackedMultiValuedMap(
            bucketMap = bucketMap,
        )


        fun <K, V> new(): MutableMultiValuedMap<K, V> = newFromMap(mutableMapOf())
    }

    /**
     * Remove all entries with the given key from the map
     * Guarantees logarithmic time complexity or better.
     *
     * @return `true` if the key was removed, or `false` if the collection contained no entry with such key
     */
    override fun removeKey(key: K): Boolean
}

/**
 * Adds a key-value mapping to this multivalued map.
 */
fun <K, V> MutableMultiValuedMap<K, V>.put(
    key: K,
    value: V,
) {
    this.add(key, value)
}

@Suppress("NOTHING_TO_INLINE")
inline fun <K, V> mutableMultiValuedMapOf(
    vararg pairs: Pair<K, V>,
): MutableMultiValuedMap<K, V> = MutableMultiValuedMap.newFromMap(
    bucketMap = pairs.groupBy { (key, _) -> key }.mapValues { (_, keyPairs) ->
        keyPairs.map { (_, value) -> value }.toMutableSet()
    }.toMutableMap(),
)

@Suppress("NOTHING_TO_INLINE")
inline fun <K : Any, V : Any> mutableWeakMultiValuedMapOf(): MutableMultiValuedMap<K, V> =
    MutableMultiValuedMap.newFromMap(
        bucketMap = mutableWeakMapOf(),
    )
