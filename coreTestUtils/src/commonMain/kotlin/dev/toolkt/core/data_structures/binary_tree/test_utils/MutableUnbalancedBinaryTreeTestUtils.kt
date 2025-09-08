package dev.toolkt.core.data_structures.binary_tree.test_utils

import dev.toolkt.core.data_structures.binary_tree.BinaryTree
import dev.toolkt.core.data_structures.binary_tree.MutableUnconstrainedBinaryTree

fun <PayloadT : Comparable<PayloadT>, ColorT> MutableUnconstrainedBinaryTree<PayloadT, ColorT>.attachVerified(
    location: BinaryTree.Location<PayloadT, ColorT>,
    payload: PayloadT,
    color: ColorT,
): BinaryTree.NodeHandle<PayloadT, ColorT> {
    val insertedNodeHandle = this.attach(
        location = location,
        payload = payload,
        color = color,
    )

    verifyIntegrity()

    return insertedNodeHandle
}

fun <PayloadT : Comparable<PayloadT>, ColorT> MutableUnconstrainedBinaryTree<PayloadT, ColorT>.swapVerified(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    side: BinaryTree.Side,
) {
    this.swap(
        nodeHandle = nodeHandle,
        side = side,
    )

    verifyIntegrity()
}

fun <PayloadT : Comparable<PayloadT>, ColorT> MutableUnconstrainedBinaryTree<PayloadT, ColorT>.cutOffVerified(
    leafHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
): BinaryTree.Location<PayloadT, ColorT> {
    val cutOffLeafLocation = this.cutOff(
        leafHandle = leafHandle,
    )

    if (leafHandle.isValid) {
        throw AssertionError("The leaf handle should be invalid after being cut off")
    }

    verifyIntegrity()

    return cutOffLeafLocation
}

fun <PayloadT : Comparable<PayloadT>, ColorT> MutableUnconstrainedBinaryTree<PayloadT, ColorT>.rotateVerified(
    pivotNodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    direction: BinaryTree.RotationDirection,
): BinaryTree.NodeHandle<PayloadT, ColorT> {
    val newSubtreeRootHandle = this.rotate(
        pivotNodeHandle = pivotNodeHandle,
        direction = direction,
    )

    verifyIntegrity()

    return newSubtreeRootHandle
}

fun <PayloadT : Comparable<PayloadT>, ColorT> MutableUnconstrainedBinaryTree<PayloadT, ColorT>.collapseVerified(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
): BinaryTree.NodeHandle<PayloadT, ColorT> {
    val elevatedChildHandle = this.collapse(
        nodeHandle = nodeHandle,
    )

    if (nodeHandle.isValid) {
        throw AssertionError("The node should be invalid after collapsing")
    }

    verifyIntegrity()

    return elevatedChildHandle
}
