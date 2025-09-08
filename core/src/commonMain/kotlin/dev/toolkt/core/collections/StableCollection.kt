package dev.toolkt.core.collections

interface StableCollection<out E> : Collection<E> {
    interface Handle<E>

    /**
     * A sequence of handles to the elements of this collection, in the order
     * defined by the collection (potentially not a meaningful order).
     */
    val handles: Sequence<Handle<@UnsafeVariance E>>

    /**
     * Returns the element corresponding to the given handle.
     * Guarantees constant time complexity.
     *
     * @return the element corresponding to the handle, or null if the corresponding
     * element was already removed
     */
    fun getVia(
        handle: Handle<@UnsafeVariance E>,
    ): E?

    fun stableIterator(): StableIterator<E>?
}
