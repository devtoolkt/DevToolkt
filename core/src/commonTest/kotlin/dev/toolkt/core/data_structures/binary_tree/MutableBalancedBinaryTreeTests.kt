package dev.toolkt.core.data_structures.binary_tree

import dev.toolkt.core.data_structures.binary_tree.lookup.findByVolatile
import dev.toolkt.core.data_structures.binary_tree.test_utils.NodeData
import dev.toolkt.core.data_structures.binary_tree.test_utils.RedBlackTreeTestUtils
import dev.toolkt.core.data_structures.binary_tree.test_utils.getHandle
import dev.toolkt.core.data_structures.binary_tree.test_utils.verify
import dev.toolkt.core.data_structures.binary_tree.test_utils.verifyOrderBy
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class MutableBalancedBinaryTreeTests {
    private class VolatileEntry(
        key: Int,
        val value: String,
    ) {
        private var mutableKey: Int? = key

        val key: Int?
            get() = mutableKey

        fun clear() {
            mutableKey = null
        }
    }

    @Test
    @Ignore // FIXME: Implement `findByVolatile` properly
    fun testFindByVolatile_simple() {
        val entryA = VolatileEntry(
            key = 1000,
            value = "A",
        )

        val entryB = VolatileEntry(
            key = 500,
            value = "B",
        )

        val entryC = VolatileEntry(
            key = 1500,
            value = "C",
        )

        val tree = RedBlackTreeTestUtils.loadVerified(
            rootData = NodeData(
                payload = entryA,
                color = RedBlackColor.Black,
                leftChild = NodeData(
                    payload = entryB,
                    color = RedBlackColor.Black,
                ),
                rightChild = NodeData(
                    payload = entryC,
                    color = RedBlackColor.Black,
                ),
            ),
        )

        tree.verifyOrderBy { it.key!! }

        val handleB = tree.getHandle(payload = entryB)
        val locationB = tree.locate(handleB)

        assertEquals(
            expected = locationB,
            actual = tree.findByVolatile(
                key = 500,
            ) { it.key },
        )
    }

    @Test
    @Ignore // FIXME: Implement `findByVolatile` properly
    fun testFindByVolatile_cleared() {
        val entryA = VolatileEntry(
            key = 1000,
            value = "A",
        )

        val entryB = VolatileEntry(
            key = 500,
            value = "B",
        )

        val entryC = VolatileEntry(
            key = 1500,
            value = "C",
        )

        val entryD = VolatileEntry(
            key = 1200,
            value = "D",
        )

        val entryE = VolatileEntry(
            key = 1600,
            value = "E",
        )

        val tree = RedBlackTreeTestUtils.loadVerified(
            rootData = NodeData(
                payload = entryA,
                color = RedBlackColor.Black,
                leftChild = NodeData(
                    payload = entryB,
                    color = RedBlackColor.Black,
                ),
                rightChild = NodeData(
                    payload = entryC,
                    color = RedBlackColor.Red,
                    leftChild = NodeData(
                        payload = entryD,
                        color = RedBlackColor.Black,
                    ),
                    rightChild = NodeData(
                        payload = entryE,
                        color = RedBlackColor.Black,
                    ),
                ),
            ),
        )

        tree.verifyOrderBy { it.key!! }

        entryC.clear()

        val handleE = tree.getHandle(payload = entryE)

        val foundLocationE = tree.findByVolatile(
            key = 1600,
        ) { it.key }

        assertEquals(
            expected = handleE,
            actual = tree.resolve(foundLocationE),
        )

        tree.verify()

        assertEquals(
            expected = listOf(
                entryB,
                entryA,
                entryD,
                entryE,
            ),
            actual = tree.traverse().toList().map {
                tree.getPayload(nodeHandle = it)
            },
        )
    }
}
