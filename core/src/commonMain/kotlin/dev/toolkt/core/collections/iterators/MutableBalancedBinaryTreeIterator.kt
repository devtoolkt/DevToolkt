package dev.toolkt.core.collections.iterators

import dev.toolkt.core.data_structures.binary_tree.BinaryTree
import dev.toolkt.core.data_structures.binary_tree.MutableBalancedBinaryTree
import dev.toolkt.core.data_structures.binary_tree.getMinimalDescendant

internal class MutableBalancedBinaryTreeIterator<PayloadT, ColorT>(
    private val tree: MutableBalancedBinaryTree<PayloadT, ColorT>,
) : HandleIterator<PayloadT, BinaryTree.NodeHandle<PayloadT, ColorT>>(
    initialAdvancement = tree.startAhead(),
) {
    override fun goAhead(
        handle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    ): Advancement.Ahead<PayloadT, BinaryTree.NodeHandle<PayloadT, ColorT>>? = tree.goAhead(
        handle = handle,
    )

    override fun remove(
        handle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    ) {
        tree.remove(nodeHandle = handle)
    }
}

private fun <PayloadT, ColorT> MutableBalancedBinaryTree<PayloadT, ColorT>.startAhead(): HandleIterator.Advancement.Ahead<PayloadT, BinaryTree.NodeHandle<PayloadT, ColorT>>? {
    val startHandle = getMinimalDescendant() ?: return null

    return this@startAhead.buildAhead(handle = startHandle)
}

private fun <PayloadT, ColorT> MutableBalancedBinaryTree<PayloadT, ColorT>.goAhead(
    handle: BinaryTree.NodeHandle<PayloadT, ColorT>,
): HandleIterator.Advancement.Ahead<PayloadT, BinaryTree.NodeHandle<PayloadT, ColorT>>? {
    val nextHandle = getInOrderNeighbour(
        nodeHandle = handle,
        side = BinaryTree.Side.Right,
    ) ?: return null

    return buildAhead(handle = nextHandle)
}

private fun <PayloadT, ColorT> MutableBalancedBinaryTree<PayloadT, ColorT>.buildAhead(
    handle: BinaryTree.NodeHandle<PayloadT, ColorT>,
): HandleIterator.Advancement.Ahead<PayloadT, BinaryTree.NodeHandle<PayloadT, ColorT>>? {
    val payload = getPayload(nodeHandle = handle)

    return HandleIterator.Advancement.Ahead(
        nextHandle = handle,
        nextElement = payload,
    )
}
