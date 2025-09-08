package dev.toolkt.core.data_structures.binary_tree.test_utils

import dev.toolkt.core.data_structures.binary_tree.BinaryTree
import dev.toolkt.core.data_structures.binary_tree.getChild
import dev.toolkt.core.data_structures.binary_tree.traverse

fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.traverseNaively(): Sequence<BinaryTree.NodeHandle<PayloadT, ColorT>> =
    this.traverseNaivelyOrEmpty(
        subtreeRootHandle = currentRootHandle,
    )

fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.traverseNaively(
    subtreeRootHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
): Sequence<BinaryTree.NodeHandle<PayloadT, ColorT>> {
    val leftChild = getChild(
        nodeHandle = subtreeRootHandle,
        side = BinaryTree.Side.Left,
    )

    val rightChild = getChild(
        nodeHandle = subtreeRootHandle,
        side = BinaryTree.Side.Right,
    )

    return sequence {
        yieldAll(traverseNaivelyOrEmpty(subtreeRootHandle = leftChild))
        yield(subtreeRootHandle)
        yieldAll(traverseNaivelyOrEmpty(subtreeRootHandle = rightChild))
    }
}

private fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.traverseNaivelyOrEmpty(
    subtreeRootHandle: BinaryTree.NodeHandle<PayloadT, ColorT>?,
): Sequence<BinaryTree.NodeHandle<PayloadT, ColorT>> {
    if (subtreeRootHandle == null) return emptySequence()

    return this.traverseNaively(
        subtreeRootHandle = subtreeRootHandle,
    )
}

fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.getHandle(
    payload: PayloadT,
): BinaryTree.NodeHandle<PayloadT, ColorT> = traverse().single {
    getPayload(nodeHandle = it) == payload
}
