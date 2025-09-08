package dev.toolkt.core.collections.maps

import dev.toolkt.core.collections.maps.MapEntry
import dev.toolkt.core.collections.MutableStableAssociativeCollection
import dev.toolkt.core.collections.MutableStableBag
import dev.toolkt.core.collections.mutableStableBagOf
import dev.toolkt.core.platform.PlatformWeakReference

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

        fun <K : Any, V> newWeakFromStableBag(
            weakEntryBag: MutableStableBag<Map.Entry<PlatformWeakReference<K>, V>>,
        ): MutableStableMultiValuedMap<K, V> = StableBagBackedWeakMultiValuedMap(
            weakEntryBag = weakEntryBag,
        )
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun <K : Any, V : Any> mutableStableWeakMultiValuedMapOf(
    vararg pairs: Pair<K, V>,
): MutableStableMultiValuedMap<K, V> = MutableStableMultiValuedMap.newWeakFromStableBag(
    weakEntryBag = mutableStableBagOf(
        *pairs.map { (key, value) ->
            MapEntry(
                PlatformWeakReference(key),
                value,
            )
        }.toTypedArray(),
    )
)

@Suppress("NOTHING_TO_INLINE")
inline fun <K : Any, V : Any> mutableStableWeakMapOf(): MutableStableMap<K, V> =
    TODO("Implement mutable stable weak map")
