package dev.toolkt.core.data_structures.binary_tree

import dev.toolkt.core.iterable.uncons

/**
 * A generic balanced binary tree that supports adding and removing elements. See [BinaryTree] for more details on the
 * read-only aspects of this interface.
 *
 * This interface requires the implementation to maintain the balancing of the tree after insertions and removals and
 * specifies the operations that can affect the balancing.
 */
interface MutableBalancedBinaryTree<PayloadT, ColorT> : BinaryTree<PayloadT, ColorT> {
    companion object {
        /**
         * Creates mutable red-black tree, taking ownership of the given [internalTree].
         *
         * @param internalTree Binary tree that's assumed to be balanced according to the [RedBlackTreeBalancingStrategy].
         * The ownership of this tree is transferred to the [MutableBalancedBinaryTree] object being created. The
         * constructed object will not behave correctly if this tree is not properly balanced or if the ownership is not
         * truly transferred.
         */
        fun <PayloadT> internalizeRedBlack(
            internalTree: MutableUnbalancedBinaryTree<PayloadT, RedBlackColor>,
        ): MutableBalancedBinaryTree<PayloadT, RedBlackColor> = MutableBalancedBinaryTreeImpl.internalize(
            internalTree = internalTree,
            balancingStrategy = RedBlackTreeBalancingStrategy(),
        )

        /**
         * Creates an empty mutable red-black tree.
         */
        fun <PayloadT> createRedBlack(): MutableBalancedBinaryTree<PayloadT, RedBlackColor> =
            MutableBalancedBinaryTreeImpl.internalize(
                internalTree = MutableUnbalancedBinaryTree.create(),
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
