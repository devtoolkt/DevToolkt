package dev.toolkt.core.data_structures.binary_tree

import dev.toolkt.core.data_structures.binary_tree.lookup.find
import dev.toolkt.core.data_structures.binary_tree.test_utils.NodeData
import dev.toolkt.core.data_structures.binary_tree.test_utils.getHandle
import dev.toolkt.core.data_structures.binary_tree.test_utils.load
import dev.toolkt.core.data_structures.binary_tree.test_utils.verifyOrder
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("ClassName")
class intrinsicDirectLookupUtils_tests {
    private enum class TestColor {
        Yellow, Orange, Red,
    }

    @Test
    fun testFind() {
        val tree: BinaryTree<Int, TestColor> = MutableUnconstrainedBinaryTree.load(
            rootData = NodeData(
                payload = 100,
                color = TestColor.Red,
                leftChild = NodeData(
                    payload = 50,
                    color = TestColor.Orange,
                    leftChild = NodeData(
                        payload = 25,
                        color = TestColor.Orange,
                    ),
                    rightChild = NodeData(
                        payload = 75,
                        color = TestColor.Orange,
                    ),
                ),
                rightChild = NodeData(
                    payload = 150,
                    color = TestColor.Orange,
                    leftChild = NodeData(
                        payload = 125,
                        color = TestColor.Yellow,
                        leftChild = NodeData(
                            payload = 115,
                            color = TestColor.Yellow,
                            leftChild = NodeData(
                                payload = 110,
                                color = TestColor.Yellow,
                            ),
                        ),
                    ),
                    rightChild = NodeData(
                        payload = 175,
                        color = TestColor.Orange,
                    ),
                ),
            ),
        )

        tree.verifyOrder()

        val handle115 = tree.getHandle(payload = 115)
        val location115 = tree.locate(handle115)

        assertEquals(
            expected = location115, actual = tree.find(115)
        )

        val handle175 = tree.getHandle(payload = 175)
        val location180 = handle175.getRightChildLocation()

        assertEquals(
            expected = location180,
            actual = tree.find(180),
        )
    }
}
