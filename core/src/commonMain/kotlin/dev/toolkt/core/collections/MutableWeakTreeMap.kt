package dev.toolkt.core.collections

import dev.toolkt.core.data_structures.binary_tree.BinaryTree
import dev.toolkt.core.data_structures.binary_tree.RedBlackColor
import dev.toolkt.core.data_structures.binary_tree.MutableBalancedBinaryTree
import dev.toolkt.core.data_structures.binary_tree.lookup.findByVolatile
import dev.toolkt.core.data_structures.binary_tree.takeOut
import dev.toolkt.core.data_structures.binary_tree.traverse
import dev.toolkt.core.platform.PlatformWeakReference
import kotlin.jvm.JvmInline

class MutableWeakTreeMap<K : Comparable<K>, V> internal constructor(
    private val entryTree: MutableBalancedBinaryTree<WeakMutableMapEntry<K, V>, RedBlackColor> = MutableBalancedBinaryTree.createRedBlack(),
) : AbstractMutableStableMap<K, V>(
    MutableBalancedBinaryTreeWeakEntrySet(entryTree = entryTree),
) {
    @JvmInline
    internal value class WeakTreeMapHandle<K : Comparable<K>, V> internal constructor(
        internal val nodeHandle: WeakEntryNodeHandle<K, V>,
    ) : EntryHandle<K, V>

    override val size: Int
        get() = entryTree.size

    override fun put(
        key: K,
        value: V,
    ): V? {
        val (location, existingNodeHandle) = findByKey(key = key)

        return when (existingNodeHandle) {
            null -> {
                entryTree.insert(
                    location = location,
                    payload = WeakMutableMapEntry(
                        key = PlatformWeakReference(key),
                        initialValue = value,
                    ),
                )

                null
            }

            else -> {
                val existingEntry = entryTree.getPayload(existingNodeHandle)
                val previousValue = existingEntry.value

                existingEntry.setValue(value)

                previousValue
            }
        }
    }

    override fun addEx(
        element: Map.Entry<K, V>,
    ): EntryHandle<K, V>? {
        val (key, value) = element

        val (location, existingNodeHandle) = findByKey(key = key)

        if (existingNodeHandle != null) {
            return null
        }

        val insertedNodeHandle = entryTree.insert(
            location = location,
            payload = WeakMutableMapEntry(
                key = PlatformWeakReference(key),
                initialValue = value,
            ),
        )

        return insertedNodeHandle.pack()
    }

    override fun removeVia(
        handle: EntryHandle<K, V>,
    ): Map.Entry<K, V>? {
        val nodeHandle = handle.unpack() ?: return null

        // Let's remove the weak entry, no matter if the key was collected or not.
        // If the key wasn't collected, we have to remove the entry because we're
        // being asked to do that. Otherwise, the entry is garbage.
        val removedWeakEntry = entryTree.takeOut(nodeHandle = nodeHandle)

        val key = removedWeakEntry.key.get()
        val value = removedWeakEntry.value

        // If the key was collected, return `null` (indicating that the entry was
        // independently removed earlier) or build a fresh map entry (indicating
        // that we removed the entry just now)
        return key?.let {
            MapEntry(key = it, value = value)
        }
    }

    override fun mutableStableIterator(): MutableStableIterator<Map.Entry<K, V>>? {
        TODO("Not yet implemented")
    }

    override fun resolve(
        key: K,
    ): EntryHandle<K, V>? {
        val (_, nodeHandle) = findByKey(key = key)
        return nodeHandle?.pack()
    }

    override val handles: Sequence<EntryHandle<K, V>>
        get() = entryTree.traverse().map { it.pack() }

    override fun getVia(
        handle: EntryHandle<K, V>,
    ): Map.Entry<K, V>? {
        val nodeHandle = handle.unpack() ?: return null
        val weakEntry = entryTree.getPayload(nodeHandle = nodeHandle)

        val key = weakEntry.key.get()
        val value = weakEntry.value

        if (key == null) {
            // If the key was collected in the meanwhile, remove the weak entry,
            // as it's now just garbage
            entryTree.remove(nodeHandle = nodeHandle)
        }

        // If the key was collected, return `null` (indicating that the entry was
        // removed earlier) or build a fresh map entry (indicating that the entry
        // was present in the map)
        return key?.let {
            MapEntry(key = it, value = value)
        }
    }

    override fun stableIterator(): StableIterator<Map.Entry<K, V>>? = mutableStableIterator()

    private fun findByKey(
        key: K,
    ): Pair<WeakEntryLocation<K, V>, WeakEntryNodeHandle<K, V>?> {
        // TODO: Figure out the contract of `findByVolatile`
        val location = entryTree.findByVolatile(
            key = key,
            selector = { it.key.get() },
        )

        val existingNodeHandle = entryTree.resolve(
            location = location,
        )

        return Pair(location, existingNodeHandle)
    }

    override fun removeKey(key: K): Boolean {
        val (_, existingNodeHandle) = findByKey(key = key)

        if (existingNodeHandle == null) return false

        val removedWeakEntry = entryTree.takeOut(nodeHandle = existingNodeHandle)

        // If the key was collected, return `false` (indicating that the entry was
        // removed earlier), otherwise return `true` (indicating that we removed
        // the entry just now)
        return removedWeakEntry.key.get() != null
    }
}

fun <K : Comparable<K>, V> mutableWeakTreeMapOf(
    vararg pairs: Pair<K, V>,
): MutableWeakTreeMap<K, V> {
    val map = MutableWeakTreeMap<K, V>()

    for ((key, value) in pairs) {
        map.put(key, value)
    }

    return map
}

internal typealias WeakMutableMapEntry<K, V> = MutableTreeMap.MutableMapEntry<PlatformWeakReference<K>, V>

private typealias WeakEntryLocation<K, V> = BinaryTree.Location<WeakMutableMapEntry<K, V>, RedBlackColor>

private typealias WeakEntryNodeHandle<K, V> = BinaryTree.NodeHandle<WeakMutableMapEntry<K, V>, RedBlackColor>

private fun <K : Comparable<K>, V> EntryHandle<K, V>.unpack(): WeakEntryNodeHandle<K, V>? {
    this as? MutableWeakTreeMap.WeakTreeMapHandle<K, V> ?: throw IllegalArgumentException(
        "Handle is not a WeakTreeMapHandle: $this"
    )

    return when {
        nodeHandle.isValid -> nodeHandle
        else -> null
    }
}

private fun <K : Comparable<K>, V> WeakEntryNodeHandle<K, V>.pack(): EntryHandle<K, V> =
    MutableWeakTreeMap.WeakTreeMapHandle(
        nodeHandle = this,
    )
