package dev.toolkt.core.collections

import dev.toolkt.core.collections.StableCollection.Handle

/**
 * A read-only set providing stable handles to its elements.
 */
interface StableSet<out E> : StableCollection<E> {
    /**
     * Returns a handle to the given [element].
     * Guarantees logarithmic time complexity or better.
     *
     * @return the handle to the element or `null` if the set does not contain this element
     */
    fun lookup(
        element: @UnsafeVariance E,
    ): Handle<@UnsafeVariance E>?
}
