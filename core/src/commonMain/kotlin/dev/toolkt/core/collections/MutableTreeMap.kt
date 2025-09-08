package dev.toolkt.core.collections

import dev.toolkt.core.data_structures.binary_tree.BinaryTree
import dev.toolkt.core.data_structures.binary_tree.RedBlackColor
import dev.toolkt.core.data_structures.binary_tree.MutableBalancedBinaryTree
import dev.toolkt.core.data_structures.binary_tree.lookup.findBy
import dev.toolkt.core.data_structures.binary_tree.traverse
import kotlin.jvm.JvmInline

class MutableTreeMap<K : Comparable<K>, V> internal constructor(
    private val entryTree: MutableBalancedBinaryTree<MutableMap.MutableEntry<K, V>, RedBlackColor> = MutableBalancedBinaryTree.redBlack(),
) : AbstractMutableStableMap<K, V>(
    MutableBalancedBinaryTreeEntrySet(entryTree = entryTree),
) {
    internal class MutableMapEntry<K, V>(
        override val key: K,
        initialValue: V,
    ) : MutableMap.MutableEntry<K, V> {
        companion object {
            fun <K : Comparable<K>, V> selectKey(
                entry: MutableMap.MutableEntry<K, V>,
            ): K = entry.key
        }

        private var mutableValue: V = initialValue

        override val value: V
            get() = mutableValue

        override fun setValue(newValue: V): V {
            val previousValue = mutableValue

            mutableValue = newValue

            return previousValue
        }
    }

    @JvmInline
    internal value class TreeMapHandle<K : Comparable<K>, V> internal constructor(
        internal val nodeHandle: EntryNodeHandle<K, V>,
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
                    payload = MutableMapEntry(
                        key = key,
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
            payload = MutableMapEntry(
                key = key,
                initialValue = value,
            ),
        )

        return insertedNodeHandle.pack()
    }

    override fun removeVia(
        handle: EntryHandle<K, V>,
    ): Map.Entry<K, V>? {
        val nodeHandle = handle.unpack() ?: return null
        val removedEntry = entryTree.getPayload(nodeHandle = nodeHandle)

        entryTree.remove(nodeHandle = nodeHandle)

        return removedEntry
    }

    override fun mutableStableIterator(): MutableStableIterator<MutableMap.MutableEntry<K, V>>? =
        MutableBalancedBinaryTreeStableIterator.iterate(
            mutableTree = entryTree,
        )

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
        return entryTree.getPayload(nodeHandle = nodeHandle)
    }

    override fun stableIterator(): StableIterator<MutableMap.MutableEntry<K, V>>? = mutableStableIterator()

    private fun findByKey(
        key: K,
    ): Pair<EntryLocation<K, V>, EntryNodeHandle<K, V>?> {
        val location = entryTree.findBy(
            key = key,
            selector = MutableMapEntry.Companion::selectKey,
        )

        val existingNodeHandle = entryTree.resolve(
            location = location,
        )

        return Pair(location, existingNodeHandle)
    }

    override fun removeKey(key: K): Boolean {
        val (_, existingNodeHandle) = findByKey(key = key)

        if (existingNodeHandle == null) return false

        entryTree.remove(nodeHandle = existingNodeHandle)

        return true
    }
}

fun <K : Comparable<K>, V> mutableTreeMapOf(
    vararg pairs: Pair<K, V>,
): MutableTreeMap<K, V> {
    val map = MutableTreeMap<K, V>()

    for ((key, value) in pairs) {
        map.put(key, value)
    }

    return map
}

private typealias EntryLocation<K, V> = BinaryTree.Location<MutableMap.MutableEntry<K, V>, RedBlackColor>

private typealias EntryNodeHandle<K, V> = BinaryTree.NodeHandle<MutableMap.MutableEntry<K, V>, RedBlackColor>

private fun <K : Comparable<K>, V> EntryHandle<K, V>.unpack(): EntryNodeHandle<K, V>? {
    this as? MutableTreeMap.TreeMapHandle<K, V> ?: throw IllegalArgumentException(
        "Handle is not a TreeMapHandle: $this"
    )

    return when {
        nodeHandle.isValid -> nodeHandle
        else -> null
    }
}

private fun <K : Comparable<K>, V> EntryNodeHandle<K, V>.pack(): EntryHandle<K, V> = MutableTreeMap.TreeMapHandle(
    nodeHandle = this,
)

