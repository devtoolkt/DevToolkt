package dev.toolkt.core.data_structures.binary_tree

import dev.toolkt.core.data_structures.binary_tree.balancing_strategies.BinaryTreeBalancingStrategy
import dev.toolkt.core.data_structures.binary_tree.balancing_strategies.BinaryTreeBalancingStrategy.RebalanceResult
import dev.toolkt.core.utils.assert

internal class MutableBalancedBinaryTreeImpl<PayloadT, ColorT> private constructor(
    private val internalTree: MutableUnconstrainedBinaryTree<PayloadT, ColorT>,
    private val balancingStrategy: BinaryTreeBalancingStrategy<PayloadT, ColorT>,
) : MutableBalancedBinaryTree<PayloadT, ColorT>, BinaryTree<PayloadT, ColorT> by internalTree {
    companion object {
        /**
         * Creates a balanced binary tree that uses the given [balancingStrategy] for maintaining balance.
         *
         * @param internalTree Binary tree that's assumed to be balanced according to the [balancingStrategy]. The ownership
         * of this tree is transferred to the [MutableBalancedBinaryTreeImpl] object being created. The constructed object will not
         * behave correctly if this tree is not properly balanced or if the ownership is not truly transferred.
         * @param balancingStrategy The strategy to use for balancing the tree.
         */
        fun <PayloadT, ColorT> internalize(
            internalTree: MutableUnconstrainedBinaryTree<PayloadT, ColorT>,
            balancingStrategy: BinaryTreeBalancingStrategy<PayloadT, ColorT>,
        ): MutableBalancedBinaryTreeImpl<PayloadT, ColorT> = MutableBalancedBinaryTreeImpl(
            internalTree = internalTree,
            balancingStrategy = balancingStrategy,
        )
    }

    override fun setPayload(
        nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
        payload: PayloadT,
    ) {
        internalTree.setPayload(
            nodeHandle = nodeHandle,
            payload = payload,
        )
    }

    override fun insert(
        location: BinaryTree.Location<PayloadT, ColorT>,
        payload: PayloadT,
    ): BinaryTree.NodeHandle<PayloadT, ColorT> {
        val attachedNodeHandle = internalTree.attach(
            location = location,
            payload = payload,
            color = balancingStrategy.defaultColor,
        )

        // Rebalance the tree after insertion
        balancingStrategy.rebalanceAfterAttach(
            internalTree = internalTree,
            attachedNodeHandle = attachedNodeHandle,
        )

        return attachedNodeHandle
    }

    override fun remove(
        nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    ) {
        val leftChildHandle = internalTree.getLeftChild(nodeHandle = nodeHandle)
        val rightChildHandle = internalTree.getRightChild(nodeHandle = nodeHandle)

        if (leftChildHandle != null && rightChildHandle != null) {
            // If the node has two children, we can't directly remove it, but we can swap it with its
            // successor
            // After the swap, the node has at most one child (as the successor was guaranteed to have at most one child)
            internalTree.swap(
                nodeHandle = nodeHandle,
                side = BinaryTree.Side.Right,
            )
        }

        removeDirectly(nodeHandle = nodeHandle)
    }

    /**
     * Remove the node directly, which is possible only if it has at most one child.
     */
    private fun removeDirectly(
        nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    ): RebalanceResult<PayloadT, ColorT> {
        val leftChildHandle = internalTree.getLeftChild(nodeHandle = nodeHandle)
        val rightChildHandle = internalTree.getRightChild(nodeHandle = nodeHandle)

        assert(leftChildHandle == null || rightChildHandle == null) {
            "The node must have at most one child, but has both left and right children"
        }

        val singleChildHandle = leftChildHandle ?: rightChildHandle

        return when (singleChildHandle) {
            null -> {
                val relativeLocation = internalTree.locateRelatively(nodeHandle = nodeHandle)
                val leafColor = internalTree.getColor(nodeHandle = nodeHandle)

                internalTree.cutOff(leafHandle = nodeHandle)

                when {
                    relativeLocation != null -> balancingStrategy.rebalanceAfterCutOff(
                        internalTree = internalTree,
                        cutOffLeafLocation = relativeLocation,
                        cutOffLeafColor = leafColor,
                    )

                    // If we cut off the root, there's no need to rebalance
                    else -> RebalanceResult(
                        retractionHeight = 0,
                        finalLocation = BinaryTree.RootLocation,
                    )
                }
            }

            else -> {
                val elevatedNodeHandle = internalTree.collapse(nodeHandle = nodeHandle)

                balancingStrategy.rebalanceAfterCollapse(
                    internalTree = internalTree,
                    elevatedNodeHandle = elevatedNodeHandle,
                )
            }
        }
    }
}
