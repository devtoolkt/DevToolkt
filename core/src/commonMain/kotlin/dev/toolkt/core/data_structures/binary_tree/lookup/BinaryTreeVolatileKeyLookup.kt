package dev.toolkt.core.data_structures.binary_tree.lookup

import dev.toolkt.core.data_structures.binary_tree.BinaryTree
import dev.toolkt.core.data_structures.binary_tree.MutableBalancedBinaryTree

fun <PayloadT, KeyT : Comparable<KeyT>, ColorT> MutableBalancedBinaryTree<PayloadT, ColorT>.findByVolatile(
    key: KeyT,
    selector: (PayloadT) -> KeyT?,
): BinaryTree.Location<PayloadT, ColorT> = findLocationGuided(
    navigator = VolatileKeyOrderBinaryTreeNavigator(
        tree = this,
        locatedKey = key,
        selector = selector,
    ),
)

private class VolatileKeyOrderBinaryTreeNavigator<PayloadT, ColorT, KeyT : Comparable<KeyT>>(
    /**
     * The tree that the navigator is operating on.
     */
    private val tree: MutableBalancedBinaryTree<PayloadT, ColorT>,
    /**
     * The key that the navigator is looking for.
     */
    private val locatedKey: KeyT,
    /**
     * A function extracting a key from the payload.
     */
    private val selector: (PayloadT) -> KeyT?,
) : BinaryTreeNavigator<PayloadT> {
    override fun instruct(
        payload: PayloadT,
    ): BinaryTreeNavigationCommand {
        val key = selector(payload)

        if (key == null) {
            // TODO: Swap with either in-order neighbour, cut-off otherwise
            // Currently, balanced trees don't expose the swap operation
        }

        TODO()
    }
}
