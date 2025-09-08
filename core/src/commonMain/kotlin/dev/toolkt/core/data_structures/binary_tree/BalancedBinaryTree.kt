package dev.toolkt.core.data_structures.binary_tree

import dev.toolkt.core.data_structures.binary_tree.BinaryTreeBalancingStrategy.RebalanceResult
import dev.toolkt.core.errors.assert
import dev.toolkt.core.iterable.uncons

/**
 * @constructor The constructor that accepts an existing mutable [internalTree]
 * is a low-level functionality. The ownership of that tree passes to this object.
 * The given tree is assumed to initially be a properly balanced tree.
 */
class BalancedBinaryTree<PayloadT, ColorT>(
    private val internalTree: MutableUnbalancedBinaryTree<PayloadT, ColorT>,
    private val balancingStrategy: BinaryTreeBalancingStrategy<PayloadT, ColorT>,
) : MutableBalancedBinaryTree<PayloadT, ColorT>, BinaryTree<PayloadT, ColorT> by internalTree {
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
    ): BinaryTree.Location<PayloadT, ColorT> {
        val leftChildHandle = internalTree.getLeftChild(nodeHandle = nodeHandle)
        val rightChildHandle = internalTree.getRightChild(nodeHandle = nodeHandle)

        val swapResult = if (leftChildHandle != null && rightChildHandle != null) {
            // If the node has two children, we can't directly remove it, but we can swap it with its
            // successor
            // After the swap, the node has at most one child (as the successor was guaranteed to have at most one child)
            internalTree.swap(
                nodeHandle = nodeHandle,
                side = BinaryTree.Side.Right,
            )
        } else null

        val rebalanceResult = removeDirectly(nodeHandle = nodeHandle)

        return when (swapResult) {
            null -> rebalanceResult.finalLocation

            else -> when {
                rebalanceResult.retractionHeight > swapResult.neighbourDepth -> rebalanceResult.finalLocation

                else -> locate(swapResult.neighbourHandle)
            }
        }
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

fun <PayloadT, ColorT> BalancedBinaryTree<PayloadT, ColorT>.insertAll(
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
