package dev.toolkt.core.collections

/**
 * An iterator over a stable collection. Allows to sequentially access the elements.
 * Unlike [Iterator], does not have its own internal mutable state.
 */
interface StableIterator<out E> {
    /**
     * Returns the current element in the iteration. Specific implementations of this class may cache the element at
     * the time of the iterator object creation.
     *
     * @throws IllegalStateException if the iterator is invalid
     */
    fun get(): E

    /**
     * Returns the next iterator in the iteration, or `null` if there are no more elements. Does not invalidate any
     * iterators.
     *
     * @throws IllegalStateException if the iterator is invalid
     */
    fun next(): StableIterator<E>?
}
