package dev.toolkt.core.collections.lists

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Suppress("ClassName")
class MutableTreeList_tests {
    private enum class Fruit {
        Apple, Raspberry, Banana, Orange, Kiwi, Mango, Pineapple, Strawberry, Watermelon, Grape,
    }

    @Test
    fun testInitial() {
        val mutableTreeList = MutableTreeList<Fruit>()

        mutableTreeList.verifyContent()
    }

    @Test
    fun testSet() {
        val mutableTreeList = mutableTreeListOf(
            Fruit.Banana,
            Fruit.Orange,
            Fruit.Kiwi,
            Fruit.Strawberry,
            Fruit.Pineapple,
        )

        mutableTreeList[1] = Fruit.Raspberry

        mutableTreeList.verifyContent(
            Fruit.Banana,
            Fruit.Raspberry,
            Fruit.Kiwi,
            Fruit.Strawberry,
            Fruit.Pineapple,
        )
    }

    @Test
    fun testAddEx() {
        val mutableTreeList = mutableTreeListOf(
            Fruit.Banana,
            Fruit.Orange,
            Fruit.Pineapple,
        )

        val bananaHandle = mutableTreeList.addEx(Fruit.Banana)

        mutableTreeList.verifyContent(
            Fruit.Banana,
            Fruit.Orange,
            Fruit.Pineapple,
            Fruit.Banana,
        )

        assertEquals(
            expected = Fruit.Banana,
            actual = mutableTreeList.getVia(handle = bananaHandle),
        )
    }

    @Test
    fun testSet_duplicate() {
        val mutableTreeList = mutableTreeListOf(
            Fruit.Banana,
            Fruit.Orange,
            Fruit.Kiwi,
            Fruit.Strawberry,
            Fruit.Watermelon,
        )

        mutableTreeList[3] = Fruit.Orange

        mutableTreeList.verifyContent(
            Fruit.Banana,
            Fruit.Orange,
            Fruit.Kiwi,
            Fruit.Orange,
            Fruit.Watermelon,
        )
    }

    @Test
    fun testAdd_append_empty() {
        val mutableTreeList = MutableTreeList<Fruit>()

        assertTrue(
            actual = mutableTreeList.add(
                Fruit.Grape,
            )
        )

        mutableTreeList.verifyContent(
            Fruit.Grape,
        )
    }

    @Test
    fun testAdd_append_nonEmpty() {
        val mutableTreeList = mutableTreeListOf(
            Fruit.Grape,
            Fruit.Strawberry,
        )

        assertTrue(
            actual = mutableTreeList.add(
                Fruit.Orange,
            ),
        )

        mutableTreeList.verifyContent(
            Fruit.Grape,
            Fruit.Strawberry,
            Fruit.Orange,
        )
    }

    @Test
    fun testAdd_append_duplicate() {
        val mutableTreeList = mutableTreeListOf(
            Fruit.Grape,
            Fruit.Strawberry,
            Fruit.Apple,
            Fruit.Mango,
        )

        assertTrue(
            actual = mutableTreeList.add(
                Fruit.Apple,
            ),
        )

        mutableTreeList.verifyContent(
            Fruit.Grape,
            Fruit.Strawberry,
            Fruit.Apple,
            Fruit.Mango,
            Fruit.Apple,
        )
    }

    @Test
    fun testAdd_atIndex_first() {
        val mutableTreeList = mutableTreeListOf(
            Fruit.Grape,
            Fruit.Strawberry,
            Fruit.Orange,
        )

        mutableTreeList.add(
            index = 0,
            element = Fruit.Apple,
        )

        mutableTreeList.verifyContent(
            Fruit.Apple,
            Fruit.Grape,
            Fruit.Strawberry,
            Fruit.Orange,
        )
    }

    @Test
    fun testAddEx_atIndex() {
        val mutableTreeList = mutableTreeListOf(
            Fruit.Grape,
            Fruit.Strawberry,
            Fruit.Orange,
        )

        val appleHandle = mutableTreeList.addAtEx(
            index = 1,
            element = Fruit.Apple,
        )

        mutableTreeList.verifyContent(
            Fruit.Grape,
            Fruit.Apple,
            Fruit.Strawberry,
            Fruit.Orange,
        )

        assertEquals(
            expected = Fruit.Apple,
            actual = mutableTreeList.getVia(handle = appleHandle),
        )
    }

    @Test
    fun testAdd_atIndex_middle() {
        val mutableTreeList = mutableTreeListOf(
            Fruit.Strawberry,
            Fruit.Grape,
            Fruit.Watermelon,
            Fruit.Orange,
        )

        mutableTreeList.add(
            index = 2,
            element = Fruit.Apple,
        )

        mutableTreeList.verifyContent(
            Fruit.Strawberry,
            Fruit.Grape,
            Fruit.Apple,
            Fruit.Watermelon,
            Fruit.Orange,
        )
    }

    @Test
    fun testAdd_atIndex_duplicate() {
        val mutableTreeList = mutableTreeListOf(
            Fruit.Strawberry,
            Fruit.Grape,
            Fruit.Watermelon,
            Fruit.Orange,
        )

        mutableTreeList.add(
            index = 2,
            element = Fruit.Watermelon,
        )

        mutableTreeList.verifyContent(
            Fruit.Strawberry,
            Fruit.Grape,
            Fruit.Watermelon,
            Fruit.Watermelon,
            Fruit.Orange,
        )
    }

    @Test
    fun testAdd_atIndex_last() {
        val mutableTreeList = mutableTreeListOf(
            Fruit.Strawberry,
            Fruit.Grape,
            Fruit.Orange,
        )

        mutableTreeList.add(
            index = 2,
            element = Fruit.Apple,
        )

        mutableTreeList.verifyContent(
            Fruit.Strawberry,
            Fruit.Grape,
            Fruit.Apple,
            Fruit.Orange,
        )
    }

    @Test
    fun testAdd_atIndex_onePastLast() {
        val mutableTreeList = mutableTreeListOf(
            Fruit.Strawberry,
            Fruit.Grape,
            Fruit.Orange,
        )

        mutableTreeList.add(
            index = 3,
            element = Fruit.Apple,
        )

        mutableTreeList.verifyContent(
            Fruit.Strawberry,
            Fruit.Grape,
            Fruit.Orange,
            Fruit.Apple,
        )
    }

    @Test
    fun testAddAllAt() {
        val mutableTreeList = mutableTreeListOf(
            Fruit.Strawberry,
            Fruit.Grape,
            Fruit.Orange,
        )

        mutableTreeList.addAllAt(
            index = 2,
            elements = listOf(
                Fruit.Pineapple,
                Fruit.Apple,
                Fruit.Raspberry,
            ),
        )

        mutableTreeList.verifyContent(
            Fruit.Strawberry,
            Fruit.Grape,
            Fruit.Pineapple,
            Fruit.Apple,
            Fruit.Raspberry,
            Fruit.Orange,
        )
    }

    @Test
    fun testRemoveAt_first() {
        val mutableTreeList = mutableTreeListOf(
            Fruit.Strawberry,
            Fruit.Grape,
            Fruit.Orange,
        )

        mutableTreeList.removeAt(0)

        mutableTreeList.verifyContent(
            Fruit.Grape,
            Fruit.Orange,
        )
    }

    @Test
    fun testRemoveAt_middle() {
        val mutableTreeList = mutableTreeListOf(
            Fruit.Strawberry,
            Fruit.Kiwi,
            Fruit.Orange,
        )

        mutableTreeList.removeAt(1)

        mutableTreeList.verifyContent(
            Fruit.Strawberry,
            Fruit.Orange,
        )
    }

    @Test
    fun testRemoveAt_last() {
        val mutableTreeList = mutableTreeListOf(
            Fruit.Strawberry,
            Fruit.Grape,
            Fruit.Orange,
        )

        mutableTreeList.removeAt(2)

        mutableTreeList.verifyContent(
            Fruit.Strawberry,
            Fruit.Grape,
        )
    }

    @Test
    fun testRemoveAt_pastLast() {
        val mutableTreeList = mutableTreeListOf(
            Fruit.Strawberry,
            Fruit.Grape,
            Fruit.Orange,
        )

        assertIs<IndexOutOfBoundsException>(
            assertFails {
                mutableTreeList.removeAt(3)
            },
        )
    }

    @Test
    fun testRemove_contained() {
        val mutableTreeList = mutableTreeListOf(
            Fruit.Strawberry,
            Fruit.Grape,
            Fruit.Orange,
        )

        assertTrue(
            actual = mutableTreeList.remove(
                Fruit.Strawberry,
            ),
        )

        mutableTreeList.verifyContent(
            Fruit.Grape,
            Fruit.Orange,
        )
    }

    @Test
    fun testRemove_nonContained() {
        val mutableTreeList = mutableTreeListOf(
            Fruit.Strawberry,
            Fruit.Kiwi,
            Fruit.Orange,
        )

        assertFalse(
            actual = mutableTreeList.remove(
                Fruit.Apple,
            ),
        )

        mutableTreeList.verifyContent(
            Fruit.Strawberry,
            Fruit.Kiwi,
            Fruit.Orange,
        )
    }

    @Test
    fun testRemoveVia() {
        val mutableTreeList = mutableTreeListOf(
            Fruit.Strawberry,
            Fruit.Kiwi,
            Fruit.Orange,
        )

        val mangoHandle = mutableTreeList.addAtEx(
            index = 2,
            element = Fruit.Mango,
        )

        assertEquals(
            expected = Fruit.Mango,
            actual = mutableTreeList.removeVia(
                handle = mangoHandle,
            ),
        )

        assertNull(
            actual = mutableTreeList.getVia(handle = mangoHandle),
        )

        assertNull(
            actual = mutableTreeList.removeVia(handle = mangoHandle),
        )

        assertNull(
            actual = mutableTreeList.indexOfVia(handle = mangoHandle),
        )

        mutableTreeList.verifyContent(
            Fruit.Strawberry,
            Fruit.Kiwi,
            Fruit.Orange,
        )
    }

    @Test
    fun testIndexOfVia() {
        val mutableTreeList = mutableTreeListOf(
            Fruit.Strawberry,
            // +Mango
            Fruit.Kiwi,
            Fruit.Orange,
            // +Pineapple
            Fruit.Grape,
            Fruit.Raspberry,
            // +Kiwi
            Fruit.Orange,
        )
        val stableList: StableList<Fruit> = mutableTreeList

        val kiwiHandle = mutableTreeList.addAtEx(
            index = 5,
            element = Fruit.Kiwi,
        )

        assertEquals(
            expected = 5,
            actual = mutableTreeList.indexOfVia(
                handle = kiwiHandle,
            ),
        )

        assertEquals(
            expected = 5,
            actual = stableList.indexOfVia(
                handle = kiwiHandle,
            ),
        )

        val pineappleHandle = mutableTreeList.addAtEx(
            index = 3,
            element = Fruit.Pineapple,
        )

        assertEquals(
            expected = 3,
            actual = mutableTreeList.indexOfVia(
                handle = pineappleHandle,
            ),
        )

        assertEquals(
            expected = 3,
            actual = stableList.indexOfVia(
                handle = pineappleHandle,
            ),
        )

        val mangoHandle = mutableTreeList.addAtEx(
            index = 1,
            element = Fruit.Mango,
        )

        assertEquals(
            expected = 1,
            actual = mutableTreeList.indexOfVia(
                handle = mangoHandle,
            ),
        )

        assertEquals(
            expected = 1,
            actual = stableList.indexOfVia(
                handle = mangoHandle,
            ),
        )

        assertEquals(
            expected = 4,
            actual = mutableTreeList.indexOfVia(
                handle = pineappleHandle,
            ),
        )

        assertEquals(
            expected = 4,
            actual = stableList.indexOfVia(
                handle = pineappleHandle,
            ),
        )

        assertEquals(
            expected = 7,
            actual = mutableTreeList.indexOfVia(
                handle = kiwiHandle,
            ),
        )

        assertEquals(
            expected = 7,
            actual = stableList.indexOfVia(
                handle = kiwiHandle,
            ),
        )
    }

    @Test
    fun testFind() {
        val mutableTreeList = mutableTreeListOf(
            Fruit.Strawberry,
            Fruit.Kiwi,
            Fruit.Orange,
            Fruit.Kiwi,
        )

        val handle = assertNotNull(
            mutableTreeList.findEx(Fruit.Kiwi),
        )

        assertEquals(
            expected = Fruit.Kiwi,
            actual = mutableTreeList.getVia(handle = handle),
        )

        assertEquals(
            expected = 1,
            actual = mutableTreeList.indexOfVia(handle = handle),
        )
    }
}
