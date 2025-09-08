package dev.toolkt.core.data_structures.binary_tree.lookup

import dev.toolkt.core.data_structures.binary_tree.BinaryTree

/**
 * Finds the location of the [payload] in a binary tree, assuming that the payloads
 * are fully comparable and that the tree's structural order is the same as the
 * natural order of the payloads.
 */
fun <PayloadT : Comparable<PayloadT>, ColorT> BinaryTree<PayloadT, ColorT>.find(
    payload: PayloadT,
): BinaryTree.Location<PayloadT, ColorT> = findLocationGuided(
    navigator = IntrinsicOrderBinaryTreeNavigator(
        locatedPayload = payload,
    ),
)

private class IntrinsicOrderBinaryTreeNavigator<PayloadT : Comparable<PayloadT>>(
    private val locatedPayload: PayloadT,
) : BinaryTreeNavigator<PayloadT> {
    override fun instruct(
        payload: PayloadT,
    ): BinaryTreeNavigationCommand = BinaryTreeNavigationCommand.comparing(
        expected = locatedPayload,
        actual = payload,
    )
}
