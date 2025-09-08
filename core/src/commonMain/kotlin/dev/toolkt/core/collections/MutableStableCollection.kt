package dev.toolkt.core.collections

import dev.toolkt.core.collections.StableCollection.Handle

/**
 * A generic mutable collection that provides stable handles to its elements.
 *
 * @param E the type of elements contained in the collection
 */
interface MutableStableCollection<E> : MutableCollection<E>, StableCollection<E> {
    /**
     * Adds the specified element to the collection in exchange for a handle.
     *
     * The specific collection implementation might provide constraints (like
     * uniqueness of elements or uniqueness of keys), so the given element might
     * not actually be added if it breaks that constraint.
     *
     * Guarantees logarithmic time complexity or better.
     *
     * @return the handle to the added element or `null` if the element wasn't
     * added because it collided with another element
     */
    fun addEx(
        element: E,
    ): Handle<E>?

    /**
     * Removes the element corresponding to the given handle from the collection.
     *
     * If the element corresponding to the handle is still present in the collection,
     * it can always be removed (collection constraints cannot prevent that).
     *
     * Guarantees logarithmic time complexity or better.
     *
     * @return the element that has been removed, or null if the corresponding element
     * has already been removed
     */
    fun removeVia(
        handle: Handle<E>,
    ): E?

    fun mutableStableIterator(): MutableStableIterator<E>?
}
