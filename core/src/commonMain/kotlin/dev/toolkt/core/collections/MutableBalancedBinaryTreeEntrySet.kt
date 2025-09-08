package dev.toolkt.core.collections

import dev.toolkt.core.data_structures.binary_tree.MutableBalancedBinaryTree

internal class MutableBalancedBinaryTreeEntrySet<K : Comparable<K>, V, ColorT>(
    private val entryTree: MutableBalancedBinaryTree<MutableMap.MutableEntry<K, V>, ColorT>,
) : AbstractMutableCollection<MutableMap.MutableEntry<K, V>>(), MutableSet<MutableMap.MutableEntry<K, V>> {
    override val size: Int
        get() = entryTree.size

    override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>> =
        MutableBalancedBinaryTreeIterator(tree = entryTree)

    override fun add(element: MutableMap.MutableEntry<K, V>): Boolean {
        throw UnsupportedOperationException()
    }
}
