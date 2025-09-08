package dev.toolkt.core.data_structures.binary_tree

/**
 * Select the node at the given [index] in the binary tree's order.
 *
 * Performance of this operation depends on the performance of [BinaryTree.getSubtreeSize] implementation. If it's
 * constant (the size is cached), this operation is logarithmic in the size of the tree.
 *
 * @return the handle to the node at the given index, or null if the index is out of bounds.
 */
fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.select(
    index: Int,
): BinaryTree.NodeHandle<PayloadT, ColorT>? {
    val rootHandle = this.currentRootHandle ?: return null

    return this.select(
        nodeHandle = rootHandle,
        index = index,
    )
}

/**
 * Get the rank of the node corresponding to the given [nodeHandle] in the whole tree (the number of nodes that are
 * preceding it in an in-order traversal)
 *
 * Performance of this operation depends on the performance of [BinaryTree.getSubtreeSize] implementation. If it's
 * constant (the size is cached), this operation is logarithmic in the size of the tree.
 */
fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.getRank(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
): Int {
    val downRank = getDownRank(
        nodeHandle = nodeHandle,
    )

    val upRank = getUpRank(
        nodeHandle = nodeHandle,
    )

    return downRank + upRank
}

private tailrec fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.select(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    index: Int,
): BinaryTree.NodeHandle<PayloadT, ColorT>? {
    val downRank = getDownRank(nodeHandle = nodeHandle)

    val leftChildHandle = getLeftChild(nodeHandle = nodeHandle)
    val rightChildHandle = getRightChild(nodeHandle = nodeHandle)

    return when {
        index == downRank -> nodeHandle

        index < downRank -> when (leftChildHandle) {
            null -> null

            else -> select(
                nodeHandle = leftChildHandle,
                index = index,
            )
        }

        else -> when (rightChildHandle) {
            null -> null

            else -> select(
                nodeHandle = rightChildHandle,
                index = index - downRank - 1,
            )
        }
    }
}

/**
 * Get the rank of the node corresponding to the given [nodeHandle] in its supertree (the whole tree minus the node's
 * descendants)
 */
private tailrec fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.getUpRank(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
): Int {
    val relativeLocation = locateRelatively(
        nodeHandle = nodeHandle,
    ) ?: return 0

    val parentHandle = relativeLocation.parentHandle
    val side = relativeLocation.side

    return when (side) {
        BinaryTree.Side.Left -> getUpRank(nodeHandle = parentHandle)
        BinaryTree.Side.Right -> getRank(nodeHandle = parentHandle) + 1
    }
}

/**
 * Get the rank of the node corresponding to the given [nodeHandle] in its own subtree
 */
private fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.getDownRank(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
): Int {
    val leftChildHandle = getLeftChild(nodeHandle = nodeHandle)

    return leftChildHandle?.let {
        getSubtreeSize(subtreeRootHandle = it)
    } ?: 0
}
