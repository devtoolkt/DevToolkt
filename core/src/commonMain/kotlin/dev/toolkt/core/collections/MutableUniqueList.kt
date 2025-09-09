package dev.toolkt.core.collections

import dev.toolkt.core.order.OrderRelation

/**
 * A hybrid collection that combines the properties of a mutable list and a set.
 * Each element is guaranteed to be unique, and the order of elements is maintained.
 * All fundamental operations have logarithmic time complexity. This collection
 * consumes roughly twice the memory as a regular collection of the given size.
 */
class MutableUniqueList<E>() : AbstractMutableList<E>() {
    private val mutableTotalOrder = MutableTotalOrder<E>()

    private val handleIndex = mutableMapOf<E, MutableTotalOrder.Handle<E>>()

    override val size: Int
        get() = handleIndex.size

    /**
     * Returns the element at the specified index in the list.
     * Guarantees logarithmic time complexity.
     */
    override fun get(
        index: Int,
    ): E {
        val handle = mutableTotalOrder.get(index = index) ?: throw IndexOutOfBoundsException(
            "Index $index is out of bounds for size ${handleIndex.size}."
        )

        return mutableTotalOrder.get(handle = handle)
    }

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
        val handle = mutableTotalOrder.get(index = index) ?: throw IndexOutOfBoundsException(
            "Index $index is out of bounds for size ${handleIndex.size}."
        )

        val previousElement = mutableTotalOrder.get(handle = handle)

        if (previousElement == element) {
            return previousElement
        }

        val previousElementHandle = handleIndex.remove(previousElement)

        if (previousElementHandle != handle) {
            throw AssertionError("Inconsistent element handle")
        }

        val newElementHandle = handleIndex[element]

        if (newElementHandle != null) {
            throw IllegalStateException("The list already contains $element")
        }

        mutableTotalOrder.set(
            handle = handle,
            element = element,
        )

        handleIndex[element] = handle

        return previousElement
    }

    /**
     * Adds the specified element to the end of this list, if it is not already present.
     * Guarantees logarithmic time complexity.
     *
     * @return `true` if the element was added, `false` if it was already present.
     */
    override fun add(
        element: E,
    ): Boolean {
        when (handleIndex[element]) {
            null -> {
                val newHandle = mutableTotalOrder.addExtremal(
                    relation = OrderRelation.Greater,
                    element = element,
                )

                handleIndex.put(element, newHandle)

                return true
            }

            else -> {
                return false
            }
        }
    }

    // FIXME: Don't add if it's already present.
    /**
     * Inserts an element into the list at the specified [index].
     * Guarantees logarithmic time complexity.
     */
    override fun add(
        index: Int,
        element: E,
    ) {
        val existingHandle = handleIndex[element]

        if (existingHandle != null) {
            throw IllegalStateException("The list already contains $element")
        }

        val newHandle = when (index) {
            0 -> mutableTotalOrder.addExtremal(
                relation = OrderRelation.Smaller,
                element = element,
            )

            handleIndex.size -> mutableTotalOrder.addExtremal(
                relation = OrderRelation.Greater,
                element = element,
            )

            else -> {
                require(index in indices) {
                    "Index $index is out of bounds for size ${handleIndex.size}."
                }

                val neighbourHandle = mutableTotalOrder.get(index = index) ?: throw IndexOutOfBoundsException(
                    "Index $index is out of bounds for size ${handleIndex.size}."
                )

                mutableTotalOrder.addRelative(
                    handle = neighbourHandle,
                    relation = OrderRelation.Smaller,
                    element = element,
                )
            }
        }

        handleIndex.put(element, newHandle)
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
        val handle = mutableTotalOrder.get(index = index) ?: throw IndexOutOfBoundsException(
            "Index $index is out of bounds for size ${handleIndex.size}."
        )

        val removedElement = mutableTotalOrder.get(handle = handle)

        mutableTotalOrder.remove(handle = handle)

        val removedHandle = handleIndex.remove(key = removedElement)

        if (removedHandle == null) {
            throw AssertionError("Handle for removed element $removedElement was not found in handleIndex")
        }

        return removedElement
    }

    /**
     * Adds the specified element to the end of this list.
     * Guarantees logarithmic time complexity.
     *
     * @return `true` if the element has been successfully removed; `false` if it was not present in the list.
     */
    override fun remove(
        element: E,
    ): Boolean {
        val removedHandle = handleIndex.remove(element) ?: return false

        mutableTotalOrder.remove(handle = removedHandle)

        return true
    }

    /**
     * Checks if the specified element is contained in this list.
     * Guarantees logarithmic time complexity.
     */
    override fun contains(
        element: E,
    ): Boolean = handleIndex.containsKey(element)

    /**
     * Returns the index of the occurrence of the specified element in the list, or -1 if the specified
     * element is not contained in the list.
     * Guarantees logarithmic time complexity.
     */
    override fun indexOf(
        element: E,
    ): Int {
        val handle = handleIndex[element] ?: return -1
        return mutableTotalOrder.indexOf(handle = handle)
    }

    /**
     * Returns the index of the last occurrence of the specified element in the list, or -1 if the specified
     * element is not contained in the list.
     * Guarantees logarithmic time complexity.
     *
     * As this list does not allow duplicates, this method is equivalent to [indexOf].
     */
    override fun lastIndexOf(
        element: E,
    ): Int = indexOf(
        element = element,
    )

    /**
     * Returns a set view of the elements contained in this list.
     */
    val asSet: Set<E>
        get() = SetUtils.backed(this)
}

fun <E> mutableUniqueListOf(
    vararg elements: E,
): MutableUniqueList<E> {
    val mutableUniqueList = MutableUniqueList<E>()

    elements.forEach { element ->
        mutableUniqueList.add(element)
    }

    return mutableUniqueList
}
