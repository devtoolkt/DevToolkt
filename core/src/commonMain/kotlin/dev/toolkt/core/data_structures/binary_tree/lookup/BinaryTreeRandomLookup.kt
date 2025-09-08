package dev.toolkt.core.data_structures.binary_tree.lookup

import dev.toolkt.core.data_structures.binary_tree.BinaryTree
import kotlin.random.Random

/**
 * Finds a random free location in the binary tree by navigating randomly
 * until a free location is reached. Takes no actual assumptions about the
 * structure of the tree.
 *
 * The probability distribution is non-uniform, meaning that the chance on reaching
 * a given free location might be different for different locations.
 */
fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.getRandomFreeLocation(
    random: Random,
): BinaryTree.Location<PayloadT, ColorT> = findLocationGuided(
    navigator = RandomBinaryTreeNavigator(
        random = random,
    ),
)

private class RandomBinaryTreeNavigator<PayloadT>(
    private val random: Random,
) : BinaryTreeNavigator<PayloadT> {
    override fun instruct(
        payload: PayloadT,
    ): BinaryTreeNavigationCommand = BinaryTreeNavigationCommand.Turn(
        side = random.nextSide(),
    )
}

private fun Random.nextSide(): BinaryTree.Side = when (nextBoolean()) {
    true -> BinaryTree.Side.Left
    false -> BinaryTree.Side.Right
}
