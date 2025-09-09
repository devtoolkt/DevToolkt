package dev.toolkt.core.collections.lists

import dev.toolkt.core.collections.StableCollection
import dev.toolkt.core.collections.StableCollection.Handle
import dev.toolkt.core.utils.iterable.indexOfOrNull

/**
 * A read-only list providing stable handles to its elements.
 */
interface StableList<out E> : StableCollection<E>, List<E> {
    /**
     * Returns a handle to the first instance of the given [element] in the list.
     * Guarantees linear time complexity or better.
     *
     * @return the handle to the element or `null` if the list does not contain such element
     */
    fun findEx(
        element: @UnsafeVariance E,
    ): Handle<@UnsafeVariance E>?

    /**
     * Returns the handle to the element at the specified [index] in the list.
     * Guarantees linear time complexity or better.
     *
     * @return the handle to the element or `null` if the index is out of bounds
     */
    fun getEx(
        index: Int,
    ): Handle<@UnsafeVariance E>?
}

/**
 * Returns the index of the element corresponding to the given handle in the list.
 * Guarantees linear time complexity.
 */
fun <E> StableList<E>.indexOfVia(
    handle: Handle<@UnsafeVariance E>,
): Int = handles.indexOfOrNull(handle) ?: throw IllegalArgumentException(
    "The handle does not belong to this list."
)
