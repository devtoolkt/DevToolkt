package dev.toolkt.core.collections

import dev.toolkt.core.data_structures.binary_tree.MutableBalancedBinaryTree

internal class MutableBalancedBinaryTreeWeakEntrySet<K : Comparable<K>, V, ColorT>(
    private val entryTree: MutableBalancedBinaryTree<WeakMutableMapEntry<K, V>, ColorT>,
) : AbstractMutableCollection<MutableMap.MutableEntry<K, V>>(), MutableSet<MutableMap.MutableEntry<K, V>> {
    override val size: Int
        get() = entryTree.size

    override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>> = TODO() // Purging?

    override fun add(element: MutableMap.MutableEntry<K, V>): Boolean {
        throw UnsupportedOperationException()
    }
}
