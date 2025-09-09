package dev.toolkt.core.data_structures.binary_tree

import dev.toolkt.core.data_structures.binary_tree.lookup.findBy
import dev.toolkt.core.data_structures.binary_tree.test_utils.NodeData
import dev.toolkt.core.data_structures.binary_tree.test_utils.getHandle
import dev.toolkt.core.data_structures.binary_tree.test_utils.load
import dev.toolkt.core.data_structures.binary_tree.test_utils.verifyOrderBy
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("ClassName")
class intrinsicIndirectLookupUtils_tests {
    private enum class TestColor {
        Pink, Orange, Green,
    }

    @Test
    fun testFindBy() {
        fun <K, V> selectKey(pair: Pair<K, V>): K = pair.first

        val tree: BinaryTree<Pair<Int, String>, TestColor> = MutableUnconstrainedBinaryTree.load(
            rootData = NodeData(
                payload = 100 to "A",
                color = TestColor.Green,
                leftChild = NodeData(
                    payload = 50 to "B",
                    color = TestColor.Orange,
                    leftChild = NodeData(
                        payload = 25 to "C",
                        color = TestColor.Orange,
                    ),
                    rightChild = NodeData(
                        payload = 75 to "D",
                        color = TestColor.Orange,
                    ),
                ),
                rightChild = NodeData(
                    payload = 150 to "E",
                    color = TestColor.Orange,
                    leftChild = NodeData(
                        payload = 125 to "F",
                        color = TestColor.Pink,
                        leftChild = NodeData(
                            payload = 115 to "G",
                            color = TestColor.Pink,
                            leftChild = NodeData(
                                payload = 110 to "H",
                                color = TestColor.Pink,
                            ),
                        ),
                    ),
                    rightChild = NodeData(
                        payload = 175 to "I",
                        color = TestColor.Orange,
                    ),
                ),
            ),
        )

        tree.verifyOrderBy(selector = ::selectKey)

        val handle115 = tree.getHandle(payload = 115 to "G")
        val location115 = tree.locate(handle115)

        assertEquals(
            expected = location115,
            actual = tree.findBy(
                key = 115,
                selector = ::selectKey,
            ),
        )

        val handle175 = tree.getHandle(payload = 175 to "I")
        val location180 = handle175.getRightChildLocation()

        assertEquals(
            expected = location180,
            actual = tree.findBy(
                key = 180,
                selector = ::selectKey,
            ),
        )
    }
}
