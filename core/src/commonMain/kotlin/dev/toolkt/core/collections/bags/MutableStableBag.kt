package dev.toolkt.core.collections.bags

import dev.toolkt.core.collections.MutableStableCollection
import dev.toolkt.core.collections.StableCollection.Handle
import dev.toolkt.core.collections.mutableStableListOf

interface MutableStableBag<E> : MutableBag<E>, StableBag<E>, MutableStableCollection<E> {
    /**
     * Replaces the element corresponding to the given handle with the specified element. Doesn't invalidate the handle.
     * If the original element was already removed, this operation isn't effective.
     *
     * @return the element previously at the specified position, or null if the original element was already removed
     */
    fun setVia(
        handle: Handle<E>,
        // TODO: Rename to `newElement`?
        element: E,
    ): E?

    /**
     * Adds the specified element to the bag in exchange for a handle.
     * Guarantees logarithmic time complexity or better.
     *
     * @return the handle to the added element
     */
    override fun addEx(element: E): Handle<E>
}

// TODO: Make this a proper method
fun <E> MutableStableBag<E>.updateVia(
    handle: Handle<E>,
    update: (E) -> E,
): E? {
    val oldValue = getVia(handle = handle) ?: return null

    val newValue = update(oldValue)

    return setVia(
        handle = handle,
        element = newValue
    )
}

@Suppress("NOTHING_TO_INLINE")
inline fun <E> mutableStableBagOf(
    vararg elements: E,
): MutableStableBag<E> = mutableStableListOf(*elements)
