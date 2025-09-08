package dev.toolkt.core.collections.maps

import dev.toolkt.core.collections.EntryHandle
import dev.toolkt.core.collections.bags.MutableStableBag
import dev.toolkt.core.collections.MutableStableIterator
import dev.toolkt.core.collections.StableIterator
import dev.toolkt.core.collections.nextAndRemove
import dev.toolkt.core.platform.PlatformWeakReference
import kotlin.jvm.JvmInline

class StableBagBackedWeakMultiValuedMap<K : Any, V>(
    private val weakEntryBag: MutableStableBag<Map.Entry<PlatformWeakReference<K>, V>>,
) : AbstractMutableCollection<Map.Entry<K, V>>(), MutableStableMultiValuedMap<K, V> {
    @JvmInline
    internal value class HandleImpl<K : Any, V>(
        val weakEntryHandle: EntryHandle<PlatformWeakReference<K>, V>,
    ) : EntryHandle<K, V>

    internal class StableIteratorImpl<K : Any, V>(
        val key: K,
        val weakEntryIterator: MutableStableIterator<Map.Entry<PlatformWeakReference<K>, V>>,
    ) : MutableStableIterator<Map.Entry<K, V>> {
        companion object {
            fun <K : Any, V> iterate(
                weakEntryBag: MutableStableBag<Map.Entry<PlatformWeakReference<K>, V>>,
            ): MutableStableIterator<Map.Entry<K, V>>? {
                val firstWeakEntryIterator = weakEntryBag.mutableStableIterator() ?: return null

                return forward(
                    weakEntryIterator = firstWeakEntryIterator,
                )
            }

            private tailrec fun <K : Any, V> forward(
                weakEntryIterator: MutableStableIterator<Map.Entry<PlatformWeakReference<K>, V>>,
            ): StableIteratorImpl<K, V>? {
                val weakEntry = weakEntryIterator.get()

                when (val key = weakEntry.key.get()) {
                    null -> {
                        val nextWeakEntryIterator = weakEntryIterator.nextAndRemove() ?: return null

                        return forward(
                            weakEntryIterator = nextWeakEntryIterator,
                        )
                    }

                    else -> {
                        return StableIteratorImpl(
                            key,
                            weakEntryIterator,
                        )
                    }
                }
            }
        }

        override fun remove() {
            weakEntryIterator.remove()
        }

        override fun get(): Map.Entry<K, V> {
            val (_, value) = weakEntryIterator.get()

            return MapEntry(
                key = key,
                value = value,
            )
        }

        override fun next(): MutableStableIterator<Map.Entry<K, V>>? {
            val nextWeakEntryIterator = weakEntryIterator.next() ?: return null

            return forward(
                weakEntryIterator = nextWeakEntryIterator,
            )
        }
    }

    override fun clear() {
        weakEntryBag.clear()
    }

    override fun remove(
        element: Map.Entry<K, V>,
    ): Boolean = weakEntryBag.removeAll {
        it.key.get() == element.key && it.value == element.value
    }

    override fun asMap(): Map<K, Collection<V>> = TODO()

    override fun containsKey(
        key: K,
    ): Boolean = any { (actualKey, _) ->
        actualKey == key
    }

    override fun getAll(
        key: K,
    ): Collection<V> = weakEntryBag.mapNotNull {
        when {
            it.key.get() == key -> it.value
            else -> null
        }
    }

    override fun isEmpty(): Boolean = weakEntryBag.isEmpty()

    override val keys: Set<K>
        get() = weakEntryBag.mapNotNull { it.key.get() }.toSet()

    override val size: Int
        get() = weakEntryBag.size

    override fun iterator(): MutableIterator<Map.Entry<K, V>> {
        TODO()
    }

    override fun add(
        element: Map.Entry<K, V>,
    ): Boolean {
        val (key, value) = element

        weakEntryBag.addEx(
            MapEntry(
                key = PlatformWeakReference(key),
                value = value,
            ),
        )

        return true
    }

    override val values: Collection<V>
        get() = weakEntryBag.map { it.value }

    override fun resolveAll(
        key: K,
    ): Collection<EntryHandle<K, V>> {
        TODO()
    }

    override val handles: Sequence<EntryHandle<K, V>>
        get() = TODO()

    override fun getVia(
        handle: EntryHandle<K, V>,
    ): Map.Entry<K, V>? {
        val weakEntryHandle = handle.unpack()

        val weakEntry = weakEntryBag.getVia(
            handle = weakEntryHandle,
        ) ?: return null

        return when (val key = weakEntry.key.get()) {
            null -> {
                weakEntryBag.removeVia(handle = weakEntryHandle)

                null
            }

            else -> {
                MapEntry(
                    key = key,
                    value = weakEntry.value,
                )
            }
        }
    }

    override fun stableIterator(): StableIterator<Map.Entry<K, V>>? = mutableStableIterator()

    override fun addEx(
        element: Map.Entry<K, V>,
    ): EntryHandle<K, V> {
        val (key, value) = element

        val weakEntryHandle = weakEntryBag.addEx(
            MapEntry(
                key = PlatformWeakReference(key),
                value = value,
            ),
        )

        return weakEntryHandle.pack()
    }

    override fun removeVia(
        handle: EntryHandle<K, V>,
    ): Map.Entry<K, V>? {
        val weakEntryHandle = handle.unpack()

        val weakEntry = weakEntryBag.removeVia(
            handle = weakEntryHandle,
        ) ?: return null

        return weakEntry.key.get()?.let { key ->
            MapEntry(
                key = key,
                value = weakEntry.value,
            )
        }
    }

    override fun mutableStableIterator(): MutableStableIterator<Map.Entry<K, V>>? = StableIteratorImpl.iterate(
        weakEntryBag = weakEntryBag,
    )

    override fun removeKey(
        key: K,
    ): Boolean = removeAll { (actualKey, _) ->
        actualKey == key
    }
}

private typealias WeakEntryHandle<K, V> = EntryHandle<PlatformWeakReference<K>, V>

private fun <K : Any, V> EntryHandle<K, V>.unpack(): WeakEntryHandle<K, V> {
    this as? StableBagBackedWeakMultiValuedMap.HandleImpl<K, V> ?: throw IllegalArgumentException(
        "Handle is not a StableBagBackedWeakMultiValuedMap.HandleImpl: $this"
    )

    return weakEntryHandle
}

private fun <K : Any, V> WeakEntryHandle<K, V>.pack(): StableBagBackedWeakMultiValuedMap.HandleImpl<K, V> {
    return StableBagBackedWeakMultiValuedMap.HandleImpl(
        weakEntryHandle = this,
    )
}
