package dev.toolkt.core.data_structures.binary_tree

import dev.toolkt.core.data_structures.binary_tree.balancing_strategies.red_black.RedBlackColor
import dev.toolkt.core.data_structures.binary_tree.test_utils.NodeData
import dev.toolkt.core.data_structures.binary_tree.test_utils.RedBlackTreeTestUtils
import dev.toolkt.core.data_structures.binary_tree.test_utils.getHandle
import kotlin.test.Test
import kotlin.test.assertEquals

class AbstractBalancedBinaryTreeTests {
    @Test
    fun testInsertAll() {
        val tree = RedBlackTreeTestUtils.loadVerified(
            // Grandparent's grandparent
            rootData = NodeData(
                payload = 1000.0,
                color = RedBlackColor.Black,
                // Grandparent's uncle
                leftChild = NodeData(
                    payload = 500.0,
                    color = RedBlackColor.Black,
                ),
                // Grandparent's parent
                rightChild = NodeData(
                    payload = 2000.0,
                    color = RedBlackColor.Black,
                ),
            ),
        )

        val handle500 = tree.getHandle(payload = 500.0)

        tree.insertAll(
            location = handle500.getRightChildLocation(),
            payloads = listOf(600.0, 700.0, 800.0),
        )

        assertEquals(
            expected = listOf(
                500.0,
                600.0,
                700.0,
                800.0,
                1000.0,
                2000.0,
            ),
            actual = tree.traverse().map {
                tree.getPayload(nodeHandle = it)
            }.toList(),
        )
    }
}
