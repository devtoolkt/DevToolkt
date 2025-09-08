package dev.toolkt.core.collections

import dev.toolkt.core.data_structures.binary_tree.BinaryTree
import dev.toolkt.core.data_structures.binary_tree.MutableBalancedBinaryTree
import dev.toolkt.core.data_structures.binary_tree.getInOrderSuccessor
import dev.toolkt.core.data_structures.binary_tree.getMinimalDescendant

class MutableBalancedBinaryTreeStableIterator<E, ColorT>(
    private val mutableTree: MutableBalancedBinaryTree<E, ColorT>,
    private val nodeHandle: BinaryTree.NodeHandle<E, ColorT>,
) : MutableStableIterator<E> {
    companion object {
        fun <E, ColorT> iterate(
            mutableTree: MutableBalancedBinaryTree<E, ColorT>,
        ): MutableStableIterator<E>? {
            val firstNodeHandle = mutableTree.getMinimalDescendant() ?: return null

            return MutableBalancedBinaryTreeStableIterator(
                mutableTree = mutableTree,
                nodeHandle = firstNodeHandle,
            )
        }
    }

    override fun remove() {
        if (!nodeHandle.isValid) {
            throw IllegalStateException("The iterator is invalid")
        }

        mutableTree.remove(nodeHandle = nodeHandle)
    }

    override fun get(): E {
        if (!nodeHandle.isValid) {
            throw IllegalStateException("The iterator is invalid")
        }

        return mutableTree.getPayload(
            nodeHandle = nodeHandle,
        )
    }

    override fun next(): MutableStableIterator<E>? {
        if (!nodeHandle.isValid) {
            throw IllegalStateException("The iterator is invalid")
        }

        val successorHandle = mutableTree.getInOrderSuccessor(
            nodeHandle = nodeHandle,
        ) ?: return null

        return MutableBalancedBinaryTreeStableIterator(
            mutableTree = mutableTree,
            nodeHandle = successorHandle,
        )
    }
}
