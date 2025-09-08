package dev.toolkt.core.data_structures.binary_tree.test_utils

import dev.toolkt.core.data_structures.binary_tree.BinaryTree
import dev.toolkt.core.data_structures.binary_tree.MutableBalancedBinaryTree
import dev.toolkt.core.data_structures.binary_tree.MutableUnbalancedBinaryTree
import dev.toolkt.core.data_structures.binary_tree.RedBlackColor
import dev.toolkt.core.data_structures.binary_tree.getLeftChild
import dev.toolkt.core.data_structures.binary_tree.getRightChild
import dev.toolkt.core.range.split
import kotlin.random.Random

private data class ColorVerificationResult(
    val blackHeight: Int,
)

fun <PayloadT : Comparable<PayloadT>> MutableBalancedBinaryTree<PayloadT, RedBlackColor>.insertVerified(
    location: BinaryTree.Location<PayloadT, RedBlackColor>,
    payload: PayloadT,
): BinaryTree.NodeHandle<PayloadT, RedBlackColor> {
    val insertedNodeHandle = insert(
        location = location,
        payload = payload,
    )

    verify()

    return insertedNodeHandle
}

fun <PayloadT : Comparable<PayloadT>> MutableBalancedBinaryTree<PayloadT, RedBlackColor>.removeVerified(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, RedBlackColor>,
) {
    remove(
        nodeHandle = nodeHandle,
    )

    verify()
}


fun <PayloadT> BinaryTree<PayloadT, RedBlackColor>.verify() {
    verifyIntegrity()
    verifyBalance()
    verifyColor()
}

private fun <PayloadT> BinaryTree<PayloadT, RedBlackColor>.verifyColor() {
    val rootHandle = this.currentRootHandle ?: return

    verifySubtreeColor(parentColor = null, rootHandle)
}

private fun <PayloadT> BinaryTree<PayloadT, RedBlackColor>.verifySubtreeColor(
    parentColor: RedBlackColor?,
    nodeHandle: BinaryTree.NodeHandle<PayloadT, RedBlackColor>,
): ColorVerificationResult {
    val nodeColor = getColor(nodeHandle = nodeHandle)

    if (parentColor == RedBlackColor.Red && nodeColor == RedBlackColor.Red) {
        throw AssertionError("Red node cannot have a red parent")
    }

    val leftChildHandle = getLeftChild(nodeHandle = nodeHandle)
    val rightChildHandle = getRightChild(nodeHandle = nodeHandle)


    val leftSubtreeVerificationResult = leftChildHandle?.let {
        verifySubtreeColor(
            parentColor = nodeColor,
            nodeHandle = it,
        )
    }

    val rightSubtreeVerificationResult = rightChildHandle?.let {
        verifySubtreeColor(
            parentColor = nodeColor,
            nodeHandle = it,
        )
    }

    val leftSubtreeBlackHeight = leftSubtreeVerificationResult?.blackHeight ?: 1
    val rightSubtreeBlackHeight = rightSubtreeVerificationResult?.blackHeight ?: 1

    val ownBlackHeight = when (nodeColor) {
        RedBlackColor.Red -> 0
        RedBlackColor.Black -> 1
    }

    if (leftSubtreeBlackHeight != rightSubtreeBlackHeight) {
        throw AssertionError("Left and right subtrees must have the same black height, but left: $leftSubtreeBlackHeight, right: $rightSubtreeBlackHeight")
    } else {
        return ColorVerificationResult(
            blackHeight = leftSubtreeBlackHeight + ownBlackHeight,
        )
    }
}

object RedBlackTreeTestUtils {
    fun <PayloadT> loadVerified(
        rootData: NodeData<PayloadT, RedBlackColor>,
    ): MutableBalancedBinaryTree<PayloadT, RedBlackColor> {
        val internalTree = MutableUnbalancedBinaryTree.load(
            rootData = rootData,
        )

        internalTree.verify()

        return MutableBalancedBinaryTree.internalizeRedBlack(
            internalTree = internalTree,
        )
    }

    fun buildBalance(
        requiredBlackDepth: Int,
        payloadRange: ClosedFloatingPointRange<Double>,
    ): NodeData<Double, RedBlackColor>? = buildBalance(
        random = Random(seed = 0), // Pass an explicit seed to make things deterministic
        requiredBlackDepth = requiredBlackDepth,
        payloadRange = payloadRange,
        parentColor = RedBlackColor.Red, // Assume a red parent to avoid red violation
    )

    private fun buildBalance(
        random: Random,
        requiredBlackDepth: Int,
        payloadRange: ClosedFloatingPointRange<Double>,
        parentColor: RedBlackColor = RedBlackColor.Red,
    ): NodeData<Double, RedBlackColor>? {
        require(requiredBlackDepth >= 1)

        if (requiredBlackDepth == 1) {
            return null
        }

        val (leftPayloadRange, rightPayloadRange) = payloadRange.split()

        val color = when (parentColor) {
            RedBlackColor.Red -> RedBlackColor.Black

            else -> {
                val x = random.nextDouble()

                when {
                    x < 0.4 -> RedBlackColor.Red
                    else -> RedBlackColor.Black
                }
            }
        }

        val newRequiredBlackDepth = when (color) {
            RedBlackColor.Black -> requiredBlackDepth - 1
            else -> requiredBlackDepth
        }

        return NodeData(
            payload = rightPayloadRange.start,
            color = color,
            leftChild = buildBalance(
                random = random,
                requiredBlackDepth = newRequiredBlackDepth,
                payloadRange = leftPayloadRange,
                parentColor = color,
            ),
            rightChild = buildBalance(
                random = random,
                requiredBlackDepth = newRequiredBlackDepth,
                payloadRange = rightPayloadRange,
                parentColor = color,
            ),
        )
    }
}
