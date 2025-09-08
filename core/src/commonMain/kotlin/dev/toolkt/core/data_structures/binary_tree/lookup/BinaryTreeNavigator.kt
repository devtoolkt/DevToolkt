package dev.toolkt.core.data_structures.binary_tree.lookup

import dev.toolkt.core.data_structures.binary_tree.BinaryTree
import dev.toolkt.core.data_structures.binary_tree.getChildLocation

/**
 * A guide that can be used to find a specific location in the binary tree. A
 * guide (typically) takes some assumptions about the structure of the tree,
 * i.e. how the node's index in the order corresponds to its payload.
 */
interface BinaryTreeNavigator<in PayloadT> {
    /**
     * Instructs on how to proceed with the given [payload].
     */
    fun instruct(
        payload: PayloadT,
    ): BinaryTreeNavigationCommand
}

fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.findLocationGuided(
    navigator: BinaryTreeNavigator<PayloadT>,
): BinaryTree.Location<PayloadT, ColorT> = this.findLocationGuided(
    location = BinaryTree.RootLocation,
    navigator = navigator,
)

private tailrec fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.findLocationGuided(
    location: BinaryTree.Location<PayloadT, ColorT>,
    navigator: BinaryTreeNavigator<PayloadT>,
): BinaryTree.Location<PayloadT, ColorT> {
    val nodeHandle = resolve(
        location = location,
    ) ?: return location

    val payload = getPayload(
        nodeHandle = nodeHandle,
    )

    val instruction = navigator.instruct(
        payload = payload,
    )

    when (instruction) {
        BinaryTreeNavigationCommand.Stop -> return location

        is BinaryTreeNavigationCommand.Turn -> {
            val childLocation = nodeHandle.getChildLocation(
                side = instruction.side,
            )

            return findLocationGuided(
                location = childLocation,
                navigator = navigator,
            )
        }
    }
}
