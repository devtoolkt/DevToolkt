package dev.toolkt.core.data_structures.binary_tree

abstract class BinaryTreeBalancingStrategy<PayloadT, ColorT> {
    data class RebalanceResult<PayloadT, ColorT>(
        /**
         * The highest location that was reached during rebalancing
         */
        val finalLocation: BinaryTree.Location<PayloadT, ColorT>,
        /**
         * The number of tree levels that were affected by the rebalancing, relative to the node that was pointed as
         * its starting point.
         */
        val retractionHeight: Int,
    )

    abstract val defaultColor: ColorT

    abstract fun rebalanceAfterAttach(
        internalTree: MutableUnbalancedBinaryTree<PayloadT, ColorT>,
        attachedNodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    ): RebalanceResult<PayloadT, ColorT>

    abstract fun rebalanceAfterCutOff(
        internalTree: MutableUnbalancedBinaryTree<PayloadT, ColorT>,
        cutOffLeafLocation: BinaryTree.RelativeLocation<PayloadT, ColorT>,
        cutOffLeafColor: ColorT,
    ): RebalanceResult<PayloadT, ColorT>

    abstract fun rebalanceAfterCollapse(
        internalTree: MutableUnbalancedBinaryTree<PayloadT, ColorT>,
        elevatedNodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    ): RebalanceResult<PayloadT, ColorT>
}
