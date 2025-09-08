package dev.toolkt.core.data_structures.binary_tree.lookup

import dev.toolkt.core.data_structures.binary_tree.BinaryTree

/**
 * Finds the location of a payload in a binary tree by a comparable key. Assumes
 * that the tree's structural order corresponds to the natural order of the payload
 * keys.
 *
 * In other words, this method assumes that the tree is a binary search tree ordered bt the natural ordering of the
 * payload keys.
 *
 * @param key The key to look for.
 * @param selector A function extracting a comparable key from the payload.
 */
fun <PayloadT, KeyT : Comparable<KeyT>, ColorT> BinaryTree<PayloadT, ColorT>.findBy(
    key: KeyT,
    selector: (PayloadT) -> KeyT,
): BinaryTree.Location<PayloadT, ColorT> = findLocationGuided(
    navigator = KeyOrderBinaryTreeNavigator(
        locatedKey = key,
        selector = selector,
    ),
)

private class KeyOrderBinaryTreeNavigator<PayloadT, KeyT : Comparable<KeyT>>(
    private val locatedKey: KeyT,
    private val selector: (PayloadT) -> KeyT,
) : BinaryTreeNavigator<PayloadT> {
    override fun instruct(
        payload: PayloadT,
    ): BinaryTreeNavigationCommand = BinaryTreeNavigationCommand.comparing(
        expected = locatedKey,
        actual = selector(payload),
    )
}
