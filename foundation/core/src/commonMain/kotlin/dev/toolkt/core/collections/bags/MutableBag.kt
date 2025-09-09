package dev.toolkt.core.collections.bags

interface MutableBag<E> : Bag<E>, MutableCollection<E> {
    /**
     * Adds all the elements of the specified collection to this bag.
     * Guarantees logarithmic time complexity or better (assuming a small number of added elements)
     *
     * @return `true` because the bag is always modified as the result of this operation.
     */
    override fun addAll(elements: Collection<E>): Boolean

    /**
     * Adds the specified element to the bag.
     *
     * @return `true` because the bag is always modified as the result of this operation.
     */
    override fun add(element: E): Boolean
}
