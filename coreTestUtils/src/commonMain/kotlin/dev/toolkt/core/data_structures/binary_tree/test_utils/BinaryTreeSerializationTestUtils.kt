package dev.toolkt.core.data_structures.binary_tree.test_utils

import dev.toolkt.core.data_structures.binary_tree.BinaryTree
import dev.toolkt.core.data_structures.binary_tree.MutableUnconstrainedBinaryTree
import dev.toolkt.core.data_structures.binary_tree.getLeftChild
import dev.toolkt.core.data_structures.binary_tree.getLeftChildLocation
import dev.toolkt.core.data_structures.binary_tree.getRightChild
import dev.toolkt.core.data_structures.binary_tree.getRightChildLocation

data class NodeData<PayloadT, ColorT>(
    val payload: PayloadT,
    val color: ColorT,
    val leftChild: NodeData<PayloadT, ColorT>? = null,
    val rightChild: NodeData<PayloadT, ColorT>? = null,
) {
    fun put(
        tree: MutableUnconstrainedBinaryTree<PayloadT, ColorT>,
        location: BinaryTree.Location<PayloadT, ColorT>,
    ) {
        val nodeHandle = tree.attach(
            location = location,
            payload = payload,
            color = color,
        )

        leftChild?.put(
            tree = tree,
            location = nodeHandle.getLeftChildLocation(),
        )

        rightChild?.put(
            tree = tree,
            location = nodeHandle.getRightChildLocation(),
        )
    }
}

fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.dump(): NodeData<PayloadT, ColorT>? = currentRootHandle?.let { dump(it) }

fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.dump(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
): NodeData<PayloadT, ColorT> {
    val payload = getPayload(nodeHandle = nodeHandle)
    val color = getColor(nodeHandle = nodeHandle)
    val leftChild = getLeftChild(nodeHandle = nodeHandle)
    val rightChild = getRightChild(nodeHandle = nodeHandle)

    return NodeData(
        payload = payload,
        color = color,
        leftChild = leftChild?.let { dump(nodeHandle = it) },
        rightChild = rightChild?.let { dump(nodeHandle = it) },
    )
}

fun <PayloadT, ColorT> MutableUnconstrainedBinaryTree.Companion.load(
    rootData: NodeData<PayloadT, ColorT>
): MutableUnconstrainedBinaryTree<PayloadT, ColorT> {
    val tree = MutableUnconstrainedBinaryTree.create<PayloadT, ColorT>()

    rootData.put(
        tree = tree,
        location = BinaryTree.RootLocation,
    )

    return tree
}
