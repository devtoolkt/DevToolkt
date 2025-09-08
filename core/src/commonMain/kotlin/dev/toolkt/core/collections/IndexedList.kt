package dev.toolkt.core.collections

import dev.toolkt.core.collections.StableCollection.Handle

/**
 * A stable list providing efficient operations for selecting elements by index
 * and retrieving their indices via handles.
 */
interface IndexedList<out E> : StableList<E> {
    /**
     * Returns the handle to the element at the specified [index] in the list.
     * Guarantees logarithmic time complexity or better.
     *
     * @return the handle to the element or `null` if the index is out of bounds
     */
    override fun getEx(
        index: Int,
    ): Handle<@UnsafeVariance E>?

    /**
     * Returns the index of the element corresponding to the given handle in the list.
     * Guarantees logarithmic time complexity.
     *
     * @return the index of the element or null if the corresponding element has
     * already been removed
     */
    fun indexOfVia(
        handle: StableCollection.Handle<@UnsafeVariance E>,
    ): Int?
}
