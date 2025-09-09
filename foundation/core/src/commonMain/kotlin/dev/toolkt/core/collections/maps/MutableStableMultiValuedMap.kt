package dev.toolkt.core.collections.maps

import dev.toolkt.core.collections.MutableStableAssociativeCollection
import dev.toolkt.core.collections.bags.MutableStableBag

/**
 * A mutable multivalued map providing stable handles to its elements.
 */
interface MutableStableMultiValuedMap<K, V> : StableMultiValuedMap<K, V>, MutableStableAssociativeCollection<K, V>,
    MutableMultiValuedMap<K, V> {
    companion object {
        fun <K, V> newFromStableMap(
            bucketMap: MutableStableMap<K, MutableStableBag<V>>,
        ): MutableStableMultiValuedMap<K, V> = StableMapBackedMultiValuedMap(
            bucketMap = bucketMap,
        )

        fun <K, V> newFromStableBag(
            entryBag: MutableStableBag<Map.Entry<K, V>>,
        ): MutableStableMultiValuedMap<K, V> = TODO()
    }
}
