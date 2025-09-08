package dev.toolkt.core.collections.lists

import dev.toolkt.core.collections.StableCollection.Handle
import dev.toolkt.core.collections.bags.MutableStableBag

/**
 * A mutable list providing stable handles to its elements.
 */
interface MutableStableList<E> : MutableList<E>, MutableStableBag<E>, StableList<E> {
    /**
     * Inserts an element into the list at the specified [index] in exchange for a handle.
     *
     * @return the handle to the added element.
     */
    fun addAtEx(
        index: Int,
        element: E,
    ): Handle<E>
}

@Suppress("NOTHING_TO_INLINE")
inline fun <E> mutableStableListOf(
    vararg elements: E,
): MutableStableList<E> = mutableTreeListOf(*elements)
