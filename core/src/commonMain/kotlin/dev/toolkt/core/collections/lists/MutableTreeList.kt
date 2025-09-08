package dev.toolkt.core.collections.lists

import dev.toolkt.core.collections.iterators.MutableBalancedBinaryTreeStableIterator
import dev.toolkt.core.collections.iterators.MutableStableIterator
import dev.toolkt.core.collections.StableCollection.Handle
import dev.toolkt.core.collections.iterators.StableIterator
import dev.toolkt.core.data_structures.binary_tree.BinaryTree
import dev.toolkt.core.data_structures.binary_tree.balancing_strategies.red_black.RedBlackColor
import dev.toolkt.core.data_structures.binary_tree.MutableBalancedBinaryTree
import dev.toolkt.core.data_structures.binary_tree.getNextInOrderFreeLocation
import dev.toolkt.core.data_structures.binary_tree.getRank
import dev.toolkt.core.data_structures.binary_tree.getSideMostFreeLocation
import dev.toolkt.core.data_structures.binary_tree.insertAll
import dev.toolkt.core.data_structures.binary_tree.insertRelative
import dev.toolkt.core.data_structures.binary_tree.select
import dev.toolkt.core.data_structures.binary_tree.takeOut
import dev.toolkt.core.data_structures.binary_tree.traverse
import kotlin.jvm.JvmInline

/**
 * A list variant that provides stable handles to the elements, but also focuses
 * on providing efficient order statistic.
 */
class MutableTreeList<E>() : AbstractMutableList<E>(), MutableIndexedList<E> {
    @JvmInline
    internal value class TreeListHandle<E> internal constructor(
        val nodeHandle: BinaryTree.NodeHandle<E, RedBlackColor>,
    ) : Handle<E>

    private val elementTree = MutableBalancedBinaryTree.createRedBlack<E>()

    override val size: Int
        get() = elementTree.size

    override val handles: Sequence<Handle<E>>
        get() = elementTree.traverse().map { it.pack() }

    override fun findEx(
        element: E,
    ): Handle<E>? = elementTree.traverse().find { nodeHandle ->
        elementTree.getPayload(nodeHandle) == element
    }?.pack()

    override fun getEx(
        index: Int,
    ): Handle<E>? {
        val nodeHandle = elementTree.select(index = index) ?: return null

        return nodeHandle.pack()
    }

    /**
     * Returns the element at the specified index in the list.
     * Guarantees logarithmic time complexity.
     */
    override fun get(
        index: Int,
    ): E {
        val nodeHandle = elementTree.select(index = index) ?: throw IndexOutOfBoundsException(
            "Index $index is out of bounds for size ${size}."
        )

        return elementTree.getPayload(nodeHandle = nodeHandle)
    }

    /**
     * Returns the element corresponding to the given handle.
     * Guarantees constant time complexity.
     */
    override fun getVia(
        handle: Handle<E>,
    ): E? {
        val nodeHandle = handle.unpack() ?: return null

        return elementTree.getPayload(nodeHandle = nodeHandle)
    }

    override fun stableIterator(): StableIterator<E>? = mutableStableIterator()

    /**
     * Replaces the element at the specified position in this list with the specified element.
     * Guarantees logarithmic time complexity.
     *
     * @return the element previously at the specified position.
     */
    override fun set(
        index: Int,
        element: E,
    ): E {
        val nodeHandle = elementTree.select(index = index) ?: throw IndexOutOfBoundsException(
            "Index $index is out of bounds for size ${size}."
        )

        val previousElement = elementTree.getPayload(nodeHandle = nodeHandle)

        elementTree.setPayload(
            nodeHandle = nodeHandle,
            payload = element,
        )

        return previousElement
    }

    /**
     * Replaces the element corresponding to the given handle with the specified element. Doesn't invalidate the handle.
     * Guarantees constant time complexity.
     *
     * @return the element previously at the specified position.
     */
    override fun setVia(
        handle: Handle<E>,
        element: E,
    ): E? {
        val nodeHandle = handle.unpack() ?: return null

        val previousElement = elementTree.getPayload(nodeHandle = nodeHandle)

        elementTree.setPayload(
            nodeHandle = nodeHandle,
            payload = element,
        )

        return previousElement
    }

    /**
     * Adds the specified element to the end of this list.
     * Guarantees logarithmic time complexity.
     *
     * @return `true` because the list is always modified as the result of this operation.
     */
    override fun add(
        element: E,
    ): Boolean {
        addEx(element = element)

        return true
    }

    /**
     * Adds the specified element to the end of this list in exchange for a handle.
     * Guarantees logarithmic time complexity.
     *
     * @return the handle to the added element.
     */
    override fun addEx(
        element: E,
    ): Handle<E> {
        val insertedNodeHandle = elementTree.insert(
            location = elementTree.getSideMostFreeLocation(
                side = BinaryTree.Side.Right,
            ),
            payload = element,
        )

        return insertedNodeHandle.pack()
    }

    /**
     * Inserts an element into the list at the specified [index].
     * Guarantees logarithmic time complexity.
     */
    override fun add(
        index: Int,
        element: E,
    ) {
        addAtEx(
            index = index,
            element = element,
        )
    }

    /**
     * Inserts all the elements of the specified collection [elements] into this list at the specified [index].
     * Guarantees logarithmic time complexity.
     *
     * @return `true` because the list is always modified as the result of this operation.
     */
    override fun addAll(
        index: Int,
        elements: Collection<E>,
    ): Boolean {
        addAllAt(
            index = index,
            elements = elements.toList(),
        )

        return true
    }

    /**
     * Inserts all the elements of the specified collection [elements] into this list at the specified [index].
     * Guarantees logarithmic time complexity.
     */
    fun addAllAt(
        index: Int,
        elements: List<E>,
    ) {
        if (index < 0 || index > size) {
            throw IndexOutOfBoundsException(
                "Index $index is out of bounds for size ${size}."
            )
        }

        val location = when (val nodeHandle = elementTree.select(index = index)) {
            // index == size, we have to append the elements
            null -> elementTree.getSideMostFreeLocation(side = BinaryTree.Side.Right)

            // Otherwise, we'll start inserting on the left side of the given node
            else -> elementTree.getNextInOrderFreeLocation(
                nodeHandle = nodeHandle,
                side = BinaryTree.Side.Left,
            )
        }

        elementTree.insertAll(
            location = location,
            payloads = elements,
        )
    }

    /**
     * Inserts an element into the list at the specified [index] in exchange for a handle.
     * Guarantees logarithmic time complexity.
     *
     * @return the handle to the added element.
     */
    override fun addAtEx(
        index: Int,
        element: E,
    ): Handle<E> {
        if (index < 0 || index > size) {
            throw IndexOutOfBoundsException(
                "Index $index is out of bounds for size ${size}."
            )
        }

        val referenceNodeHandle = elementTree.select(index = index)

        val insertedNodeHandle = when (referenceNodeHandle) {
            null -> elementTree.insert(
                location = elementTree.getSideMostFreeLocation(
                    side = BinaryTree.Side.Right,
                ),
                payload = element,
            )

            else -> elementTree.insertRelative(
                nodeHandle = referenceNodeHandle,
                side = BinaryTree.Side.Left,
                payload = element,
            )
        }

        return insertedNodeHandle.pack()
    }

    /**
     * Removes an element at the specified [index] from the list.
     * Guarantees logarithmic time complexity.
     *
     * @return the element that has been removed.
     */
    override fun removeAt(
        index: Int,
    ): E {
        val nodeHandle = elementTree.select(index = index) ?: throw IndexOutOfBoundsException(
            "Index $index is out of bounds for size ${size}."
        )

        return elementTree.takeOut(nodeHandle = nodeHandle)
    }

    /**
     * Removes the element corresponding to the given handle from the list.
     * Guarantees logarithmic time complexity.
     *
     * @return the element that has been removed.
     */
    override fun removeVia(
        handle: Handle<E>,
    ): E? {
        val nodeHandle = handle.unpack() ?: return null

        return elementTree.takeOut(nodeHandle = nodeHandle)
    }

    override fun mutableStableIterator(): MutableStableIterator<E>? = MutableBalancedBinaryTreeStableIterator.Companion.iterate(
        mutableTree = elementTree,
    )

    /**
     * Returns the index of the element corresponding to the given handle in the list.
     * Guarantees logarithmic time complexity.
     *
     * @return the index of the element or null if the corresponding element has
     * already been removed
     */
    override fun indexOfVia(
        handle: Handle<E>,
    ): Int? {
        val nodeHandle = handle.unpack() ?: return null

        return elementTree.getRank(nodeHandle = nodeHandle)
    }

    /**
     * Returns the index of the first occurrence of the specified element in the list, or -1 if the specified
     * element is not contained in the list.
     *
     * Guarantees only linear time complexity, as the list can contain duplicates. For logarithmic time complexity,
     * use [indexOfVia] with a handle.
     */
    @Suppress("RedundantOverride")
    override fun indexOf(element: E): Int {
        return super.indexOf(element)
    }
}

fun <E> mutableTreeListOf(
    vararg elements: E,
): MutableTreeList<E> {
    val mutableTreeList = MutableTreeList<E>()

    elements.forEach { element ->
        mutableTreeList.add(element)
    }

    return mutableTreeList
}

private fun <E> Handle<E>.unpack(): BinaryTree.NodeHandle<E, RedBlackColor>? {
    this as? MutableTreeList.TreeListHandle<E> ?: throw IllegalArgumentException(
        "Handle is not a TreeListHandle: $this"
    )

    return when {
        nodeHandle.isValid -> nodeHandle
        else -> null
    }
}

private fun <E> BinaryTree.NodeHandle<E, RedBlackColor>.pack(): Handle<E> = MutableTreeList.TreeListHandle(
    nodeHandle = this,
)
