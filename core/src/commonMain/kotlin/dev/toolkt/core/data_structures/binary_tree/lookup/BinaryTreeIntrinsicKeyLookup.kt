package dev.toolkt.core.data_structures.binary_tree.lookup

import dev.toolkt.core.data_structures.binary_tree.BinaryTree

fun <PayloadT, KeyT : Comparable<KeyT>, ColorT> BinaryTree<PayloadT, ColorT>.findBy(
    key: KeyT,
    selector: (PayloadT) -> KeyT,
): BinaryTree.Location<PayloadT, ColorT> = findLocationGuided(
    navigator = KeyOrderBinaryTreeNavigator(
        locatedKey = key,
        selector = selector,
    ),
)

/**
 * A navigator locating a payload that's partially comparable (has a property called
 * "key" that is comparable). Assumes that the tree's structural order corresponds
 * to the natural order of the payload keys.
 */
private class KeyOrderBinaryTreeNavigator<PayloadT, KeyT : Comparable<KeyT>>(
    /**
     * The key that the navigator is looking for.
     */
    private val locatedKey: KeyT,
    /**
     * A function extracting a key from the payload.
     */
    private val selector: (PayloadT) -> KeyT,
) : BinaryTreeNavigator<PayloadT> {
    override fun instruct(
        payload: PayloadT,
    ): BinaryTreeNavigationCommand = BinaryTreeNavigationCommand.comparing(
        expected = locatedKey,
        actual = selector(payload),
    )
}
