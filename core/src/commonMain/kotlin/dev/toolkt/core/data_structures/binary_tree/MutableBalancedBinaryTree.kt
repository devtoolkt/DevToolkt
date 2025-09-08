package dev.toolkt.core.data_structures.binary_tree

import dev.toolkt.core.iterable.uncons

interface MutableBalancedBinaryTree<PayloadT, ColorT> : BinaryTree<PayloadT, ColorT> {
    companion object {
        fun <PayloadT> redBlack(
            internalTree: MutableUnbalancedBinaryTree<PayloadT, RedBlackColor> = MutableUnbalancedBinaryTree.create(),
        ): MutableBalancedBinaryTree<PayloadT, RedBlackColor> = BalancedBinaryTree(
            internalTree = internalTree,
            balancingStrategy = RedBlackTreeBalancingStrategy(),
        )
    }

    /**
     * Set the payload of the node corresponding to the given [nodeHandle].
     */
    fun setPayload(
        nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
        payload: PayloadT,
    )

    /**
     * Insert a new node with the given [payload] at the given free [location].
     *
     * May result in the tree re-balancing.
     *
     * @return A handle to the new inserted node
     * @throws IllegalArgumentException if the location is taken
     */
    fun insert(
        location: BinaryTree.Location<PayloadT, ColorT>,
        payload: PayloadT,
    ): BinaryTree.NodeHandle<PayloadT, ColorT>

    /**
     * Removes a node corresponding to the given [nodeHandle] from the tree
     * without otherwise affecting the tree's order.
     *
     * May result in the tree re-balancing.
     *
     * @throws IllegalArgumentException if the node is not a leaf
     * @return the location of the highest subtree root that was rotated
     */
    // TODO: Figure out if it's actually needed to return the location
    fun remove(
        nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    ): BinaryTree.Location<PayloadT, ColorT>
}

fun <PayloadT, ColorT> MutableBalancedBinaryTree<PayloadT, ColorT>.insertAll(
    location: BinaryTree.Location<PayloadT, ColorT>,
    payloads: List<PayloadT>,
) {
    val (firstPayload, trailingPayloads) = payloads.uncons() ?: return

    val nodeHandle = insert(
        location = location,
        payload = firstPayload,
    )

    insertAll(
        location = getNextInOrderFreeLocation(
            nodeHandle = nodeHandle,
            side = BinaryTree.Side.Right,
        ),
        payloads = trailingPayloads,
    )
}

fun <PayloadT, ColorT> MutableBalancedBinaryTree<PayloadT, ColorT>.takeOut(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
): PayloadT {
    val takenPayload = getPayload(nodeHandle = nodeHandle)

    remove(nodeHandle = nodeHandle)

    return takenPayload
}

fun <PayloadT, ColorT> MutableBalancedBinaryTree<PayloadT, ColorT>.insertRelative(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    side: BinaryTree.Side,
    payload: PayloadT,
): BinaryTree.NodeHandle<PayloadT, ColorT> {
    val location = getNextInOrderFreeLocation(
        nodeHandle = nodeHandle,
        side = side,
    )

    return insert(
        location = location,
        payload = payload,
    )
}
