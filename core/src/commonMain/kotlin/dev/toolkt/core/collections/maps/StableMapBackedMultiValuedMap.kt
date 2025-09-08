package dev.toolkt.core.collections.maps

import dev.toolkt.core.collections.EntryHandle
import dev.toolkt.core.collections.bags.MutableStableBag
import dev.toolkt.core.collections.MutableStableIterator
import dev.toolkt.core.collections.StableCollection.Handle
import dev.toolkt.core.collections.StableIterator
import dev.toolkt.core.collections.forceMutable
import dev.toolkt.core.collections.getValueVia
import dev.toolkt.core.collections.getWithHandle
import dev.toolkt.core.collections.bags.mutableStableBagOf
import dev.toolkt.core.collections.lists.mutableStableListOf

class StableMapBackedMultiValuedMap<K, V>(
    private val bucketMap: MutableStableMap<K, MutableStableBag<V>>,
) : AbstractMutableCollection<Map.Entry<K, V>>(), MutableStableMultiValuedMap<K, V> {
    internal data class HandleImpl<K, V>(
        val bucketHandle: BucketHandle<K, V>,
        val valueHandle: Handle<V>,
    ) : EntryHandle<K, V>

    internal class StableIteratorImpl<K, V>(
        val bucketIterator: MutableStableIterator<Map.Entry<K, MutableStableBag<V>>>,
        val valueIterator: MutableStableIterator<V>,
    ) : MutableStableIterator<Map.Entry<K, V>> {
        companion object {
            fun <K, V> iterate(
                bucketMap: MutableStableMap<K, MutableStableBag<V>>,
            ): MutableStableIterator<Map.Entry<K, V>>? {
                val firstBucketIterator = bucketMap.mutableStableIterator() ?: return null
                val (_, firstBucket) = firstBucketIterator.get()

                val firstValueIterator =
                    firstBucket.mutableStableIterator() ?: throw AssertionError("The first bucket should not be empty")

                return StableIteratorImpl(
                    bucketIterator = firstBucketIterator,
                    valueIterator = firstValueIterator,
                )
            }
        }

        override fun remove() {
            val (_, bucket) = bucketIterator.get()

            valueIterator.remove()

            if (bucket.isEmpty()) {
                bucketIterator.remove()
            }
        }

        override fun get(): Map.Entry<K, V> {
            val (key, _) = bucketIterator.get()
            val value = valueIterator.get()

            return MapEntry(
                key = key,
                value = value,
            )
        }

        override fun next(): MutableStableIterator<Map.Entry<K, V>>? {
            when (val nextValueIterator = valueIterator.next()) {
                null -> {
                    val nextBucketIterator = bucketIterator.next() ?: return null
                    val (_, nextBucket) = nextBucketIterator.get()

                    val nextBucketValueIterator = nextBucket.mutableStableIterator()
                        ?: throw AssertionError("The next bucket should not be empty")

                    return StableIteratorImpl(
                        bucketIterator = nextBucketIterator,
                        valueIterator = nextBucketValueIterator,
                    )
                }

                else -> {
                    return StableIteratorImpl(
                        bucketIterator = bucketIterator,
                        valueIterator = nextValueIterator,
                    )
                }
            }
        }
    }

    companion object {
        private fun <K, V> pack(
            bucketHandle: BucketHandle<K, V>,
            valueHandle: Handle<V>,
        ): EntryHandle<K, V> = HandleImpl(
            bucketHandle = bucketHandle,
            valueHandle = valueHandle,
        )
    }

    override fun clear() {
        bucketMap.clear()
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
        get() = bucketMap.values.sumOf { it.size }

    override fun iterator(): MutableIterator<Map.Entry<K, V>> {
        val bucketMap: Map<K, Collection<V>> = bucketMap

        return bucketMap.asSequence().flatMap { (key, bucket) ->
            bucket.asSequence().map { value ->
                MapEntry(key, value)
            }
        }.iterator().forceMutable()
    }

    override fun add(
        element: Map.Entry<K, V>,
    ): Boolean {
        val (key, value) = element

        val bucket = getFreshBucket(key = key)

        return bucket.add(value)
    }

    override val values: Collection<V>
        get() = bucketMap.values.flatten()

    override fun resolveAll(
        key: K,
    ): Collection<EntryHandle<K, V>> {
        val (bucket, bucketHandle) = bucketMap.getWithHandle(
            key = key,
        ) ?: return emptyList()

        return bucket.handles.map { valueHandle ->
            pack(
                bucketHandle = bucketHandle,
                valueHandle = valueHandle,
            )
        }.toList()
    }

    override val handles: Sequence<EntryHandle<K, V>>
        get() = bucketMap.handles.flatMap { bucketHandle ->
            val bucket = bucketMap.getValueVia(handle = bucketHandle)
                ?: throw AssertionError("The handle is immediately invalid")

            bucket.handles.map { valueHandle ->
                pack(bucketHandle, valueHandle)
            }
        }

    override fun getVia(
        handle: EntryHandle<K, V>,
    ): Map.Entry<K, V>? {
        val (key, _, value) = handle.unpackFully() ?: return null

        return MapEntry(
            key = key,
            value = value,
        )
    }

    override fun stableIterator(): StableIterator<Map.Entry<K, V>>? = mutableStableIterator()

    override fun addEx(
        element: Map.Entry<K, V>,
    ): EntryHandle<K, V> {
        val (key, value) = element

        val (existingBucket, existingBucketHandle) = bucketMap.getWithHandle(
            key = key,
        ) ?: return run {
            val newBucket = mutableStableBagOf<V>()

            val newBucketHandle = bucketMap.addEx(
                key = key,
                value = newBucket,
            ) ?: throw AssertionError("The new bucket wasn't effectively added")

            val addedValueHandle = newBucket.addEx(value)

            return@run pack(
                bucketHandle = newBucketHandle,
                valueHandle = addedValueHandle,
            )
        }

        val addedValueHandle = existingBucket.addEx(value)

        return pack(
            bucketHandle = existingBucketHandle,
            valueHandle = addedValueHandle,
        )
    }

    override fun removeVia(
        handle: EntryHandle<K, V>,
    ): Map.Entry<K, V>? {
        val handleImpl = handle.unpack()
        val bucketHandle = handleImpl.bucketHandle
        val valueHandle = handleImpl.valueHandle

        val (key, bucket) = bucketMap.getVia(bucketHandle) ?: return null

        val removedValue = bucket.removeVia(handle = valueHandle) ?: return null

        if (bucket.isEmpty()) {
            bucketMap.removeVia(handle = bucketHandle)
        }

        return MapEntry(
            key = key,
            value = removedValue,
        )
    }

    override fun mutableStableIterator(): MutableStableIterator<Map.Entry<K, V>>? = StableIteratorImpl.iterate(
        bucketMap = bucketMap,
    )

    override fun removeKey(key: K): Boolean {
        val removedBucket = bucketMap.remove(key)
        return removedBucket != null
    }

    private fun getFreshBucket(
        key: K,
    ): MutableStableBag<V> = bucketMap.getOrPut(key) {
        mutableStableListOf()
    }

    private fun EntryHandle<K, V>.unpackFully(): UnpackedHandle<K, V>? {
        val handleImpl = this.unpack()

        val bucketEntry = bucketMap.getVia(
            handle = handleImpl.bucketHandle,
        ) ?: return null

        val key = bucketEntry.key

        val bucket = bucketEntry.value

        val value = bucket.getVia(
            handle = handleImpl.valueHandle,
        ) ?: return null

        return UnpackedHandle(
            key = key,
            bucket = bucket,
            value = value,
        )
    }
}

private typealias BucketHandle<K, V> = EntryHandle<K, MutableStableBag<V>>

private data class UnpackedHandle<K, V>(
    val key: K,
    val bucket: MutableStableBag<V>,
    val value: V,
)

private fun <K, V> EntryHandle<K, V>.unpack(): StableMapBackedMultiValuedMap.HandleImpl<K, V> =
    this as? StableMapBackedMultiValuedMap.HandleImpl<K, V> ?: throw IllegalArgumentException(
        "Handle is not a StableMapBackedMultiValuedMap.HandleImpl: $this"
    )
