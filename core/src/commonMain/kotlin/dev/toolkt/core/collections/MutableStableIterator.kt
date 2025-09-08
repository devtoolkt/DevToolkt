package dev.toolkt.core.collections

/**
 * An iterator over a mutable stable collection. Provides the ability to remove elements while iterating.
 * Unlike [MutableIterator], does not have its own internal mutable state.
 */
interface MutableStableIterator<out E> : StableIterator<E> {
    override fun next(): MutableStableIterator<E>?

    /**
     * Removes the current element from the underlying collection. Invalidates the iterator. To guarantee
     * undisturbed iteration, `next()` must be called to retrieve the next iterator _before_ calling this method.
     *
     * @throws IllegalStateException if the iterator is invalid
     */
    fun remove()

}

fun <E> MutableStableIterator<E>.nextAndRemove(): MutableStableIterator<E>? {
    val nextIterator = next()

    remove()

    return nextIterator
}
