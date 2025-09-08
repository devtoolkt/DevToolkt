package dev.toolkt.core.data_structures.binary_tree.test_utils

import dev.toolkt.core.data_structures.binary_tree.BinaryTree
import dev.toolkt.core.data_structures.binary_tree.getInOrderPredecessor
import dev.toolkt.core.data_structures.binary_tree.getInOrderSuccessor
import dev.toolkt.core.data_structures.binary_tree.getLeftChild
import dev.toolkt.core.data_structures.binary_tree.getRank
import dev.toolkt.core.data_structures.binary_tree.getRightChild
import dev.toolkt.core.data_structures.binary_tree.select
import dev.toolkt.core.data_structures.binary_tree.traverse
import dev.toolkt.core.iterable.withNeighboursOrNull
import dev.toolkt.core.pairs.sorted

private data class IntegrityVerificationResult(
    val computedSubtreeSize: Int,
)

private data class BalanceVerificationResult(
    val computedSubtreeHeight: Int,
)

fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.verifyIntegrity() {
    val rootResult = this.currentRootHandle?.let { rootHandle ->
        verifySubtreeIntegrity(
            subtreeRootHandle = rootHandle,
            expectedParentHandle = null,
        )
    }

    val computedTreeSize = rootResult?.computedSubtreeSize ?: 0

    val naivelyTraversedNodeHandles = traverseNaively().toList()

    val traversedNodeHandles = traverse().toList()

    if (naivelyTraversedNodeHandles != traversedNodeHandles) {
        throw AssertionError("Inconsistent traversal")
    }

    if (traversedNodeHandles.size != size) {
        throw AssertionError("Inconsistent tree size, computed: ${computedTreeSize}, declared: $size")
    }

    if (traversedNodeHandles.size != computedTreeSize) {
        throw AssertionError("Inconsistent tree size, computed: ${computedTreeSize}, traversal: ${traversedNodeHandles.size}")
    }

    val uniqueNodeHandles = traversedNodeHandles.toSet()

    if (traversedNodeHandles.size != uniqueNodeHandles.size) {
        throw AssertionError("Traversal contains duplicate nodes")
    }

    naivelyTraversedNodeHandles.asSequence().withNeighboursOrNull()
        .forEachIndexed { index, (naivePredecessorHandle, nodeHandle, naiveSuccessorHandle) ->
            val predecessorHandle = getInOrderPredecessor(
                nodeHandle = nodeHandle,
            )

            if (predecessorHandle != naivePredecessorHandle) {
                throw AssertionError("Inconsistent predecessor for node $nodeHandle, naive: $naivePredecessorHandle, actual: $predecessorHandle")
            }

            val successorHandle = getInOrderSuccessor(
                nodeHandle = nodeHandle,
            )

            if (successorHandle != naiveSuccessorHandle) {
                throw AssertionError("Inconsistent successor for node $nodeHandle, naive: $naiveSuccessorHandle, actual: $successorHandle")
            }

            val selectedNodeHandle = select(index = index)

            if (selectedNodeHandle != nodeHandle) {
                throw AssertionError("Inconsistent selection for index $index, naive: $nodeHandle, actual: $selectedNodeHandle")
            }

            val rank = getRank(nodeHandle = nodeHandle)

            if (rank != index) {
                throw AssertionError("Inconsistent rank for node $nodeHandle, expected: $index, actual: $rank")
            }
        }
}

private fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.verifySubtreeIntegrity(
    subtreeRootHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    expectedParentHandle: BinaryTree.NodeHandle<PayloadT, ColorT>?,
): IntegrityVerificationResult {
    val actualParentHandle = getParent(nodeHandle = subtreeRootHandle)

    if (!subtreeRootHandle.isValid) {
        throw AssertionError("Invalid node handle: $subtreeRootHandle")
    }

    if (actualParentHandle != expectedParentHandle) {
        throw AssertionError("Inconsistent parent")
    }

    val leftChildHandle = getLeftChild(nodeHandle = subtreeRootHandle)
    val rightChildHandle = getRightChild(nodeHandle = subtreeRootHandle)

    val leftSubtreeVerificationResult = leftChildHandle?.let {
        verifySubtreeIntegrity(
            subtreeRootHandle = it,
            expectedParentHandle = subtreeRootHandle,
        )
    }

    val rightSubtreeVerificationResult = rightChildHandle?.let {
        verifySubtreeIntegrity(
            subtreeRootHandle = it,
            expectedParentHandle = subtreeRootHandle,
        )
    }

    val computedLeftSubtreeSize = leftSubtreeVerificationResult?.computedSubtreeSize ?: 0
    val computedRightSubtreeSize = rightSubtreeVerificationResult?.computedSubtreeSize ?: 0
    val computedTotalSubtreeSize = computedLeftSubtreeSize + computedRightSubtreeSize + 1

    val cachedSubtreeSize = getSubtreeSize(subtreeRootHandle = subtreeRootHandle)

    if (cachedSubtreeSize != computedTotalSubtreeSize) {
        throw AssertionError("Inconsistent subtree size, computed: $computedTotalSubtreeSize, cached: $cachedSubtreeSize")
    }

    return IntegrityVerificationResult(
        computedSubtreeSize = computedTotalSubtreeSize,
    )
}

fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.verifyBalance() {
    val rootHandle = this.currentRootHandle ?: return

    verifySubtreeBalance(
        subtreeRootHandle = rootHandle,
    )
}

private fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.verifySubtreeBalance(
    subtreeRootHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
): BalanceVerificationResult {
    val leftChildHandle = getLeftChild(nodeHandle = subtreeRootHandle)
    val rightChildHandle = getRightChild(nodeHandle = subtreeRootHandle)

    val leftSubtreeVerificationResult = leftChildHandle?.let {
        verifySubtreeBalance(
            subtreeRootHandle = it,
        )
    }

    val rightSubtreeVerificationResult = rightChildHandle?.let {
        verifySubtreeBalance(
            subtreeRootHandle = it,
        )
    }

    val (minPathLength, maxPathLength) = Pair(
        leftSubtreeVerificationResult?.computedSubtreeHeight ?: 1,
        rightSubtreeVerificationResult?.computedSubtreeHeight ?: 1,
    ).sorted()

    if (maxPathLength > 2 * minPathLength) {
        throw AssertionError("Unbalanced subtree, min subtree height: $minPathLength, max subtree height: $maxPathLength")
    }

    return BalanceVerificationResult(
        computedSubtreeHeight = maxPathLength,
    )
}

fun <PayloadT : Comparable<PayloadT>, ColorT> BinaryTree<PayloadT, ColorT>.verifyOrder() {
    verifyOrderBy { it }
}

fun <PayloadT, KeyT : Comparable<KeyT>, ColorT> BinaryTree<PayloadT, ColorT>.verifyOrderBy(
    selector: (PayloadT) -> KeyT,
) {
    val rootHandle = this.currentRootHandle ?: return

    verifySubtreeOrder(
        subtreeRootHandle = rootHandle,
        selector = selector,
    )
}

private fun <PayloadT, KeyT : Comparable<KeyT>, ColorT> BinaryTree<PayloadT, ColorT>.verifySubtreeOrder(
    subtreeRootHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    selector: (PayloadT) -> KeyT,
) {
    val payload = getPayload(nodeHandle = subtreeRootHandle)
    val key = selector(payload)

    val leftChildHandle = getLeftChild(nodeHandle = subtreeRootHandle)
    val rightChildHandle = getRightChild(nodeHandle = subtreeRootHandle)

    leftChildHandle?.let {
        val leftPayload = getPayload(nodeHandle = it)
        val leftKey = selector(leftPayload)

        if (leftKey >= key) {
            throw AssertionError("Left child payload $leftPayload is not less than parent payload $payload")
        }

        verifySubtreeOrder(
            subtreeRootHandle = it,
            selector = selector,
        )
    }

    rightChildHandle?.let {
        val rightPayload = getPayload(nodeHandle = it)
        val rightKey = selector(rightPayload)

        if (rightKey <= key) {
            throw AssertionError("Right child payload $rightPayload is not greater than parent payload $payload")
        }

        verifySubtreeOrder(
            subtreeRootHandle = it,
            selector = selector,
        )
    }
}
