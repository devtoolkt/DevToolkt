package dev.toolkt.core.data_structures.binary_tree

import dev.toolkt.core.data_structures.binary_tree.balancing_strategies.red_black.RedBlackColor
import dev.toolkt.core.data_structures.binary_tree.balancing_strategies.red_black.RedBlackTreeBalancingStrategy
import dev.toolkt.core.iterable.uncons

/**
 * A generic balanced binary tree that supports adding, removing and updating the payloads, while guaranteeing that the
 * tree remains balanced. See [BinaryTree] for more details on the read-only aspects of this interface.
 *
 * See [MutableUnconstrainedBinaryTree] for a similar interface that does not guarantee balancing.
 *
 * In practice, simplest best way to implement this interface is to compose a [MutableUnconstrainedBinaryTree] instance
 * and restore the balance as needed.
 */
interface MutableBalancedBinaryTree<PayloadT, ColorT> : BalancedBinaryTree<PayloadT, ColorT> {
    companion object {
        /**
         * Creates mutable red-black tree, taking ownership of the given [internalTree].
         *
         * @param internalTree Binary tree that's assumed to be balanced according to the [dev.toolkt.core.data_structures.binary_tree.balancing_strategies.red_black.RedBlackTreeBalancingStrategy].
         * The ownership of this tree is transferred to the [MutableBalancedBinaryTree] object being created. The
         * constructed object will not behave correctly if this tree is not properly balanced or if the ownership is not
         * truly transferred.
         */
        fun <PayloadT> internalizeRedBlack(
            internalTree: MutableUnconstrainedBinaryTree<PayloadT, RedBlackColor>,
        ): MutableBalancedBinaryTree<PayloadT, RedBlackColor> = MutableBalancedBinaryTreeImpl.internalize(
            internalTree = internalTree,
            balancingStrategy = RedBlackTreeBalancingStrategy(),
        )

        /**
         * Creates an empty mutable red-black tree.
         */
        fun <PayloadT> createRedBlack(): MutableBalancedBinaryTree<PayloadT, RedBlackColor> =
            MutableBalancedBinaryTreeImpl.internalize(
                internalTree = MutableUnconstrainedBinaryTree.create(),
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
     */
    fun remove(
        nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    )
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
