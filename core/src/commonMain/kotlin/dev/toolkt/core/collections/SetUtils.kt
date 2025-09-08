package dev.toolkt.core.collections

object SetUtils {
    fun <E> backed(
        collection: Collection<E>,
    ): Set<E> = CollectionBackedSet(collection)
}

private class CollectionBackedSet<E>(
    private val backingCollection: Collection<E>,
) : Set<E> {
    override val size: Int
        get() = backingCollection.size

    override fun isEmpty(): Boolean = backingCollection.isEmpty()

    override fun contains(element: E): Boolean = backingCollection.contains(element)

    override fun iterator(): Iterator<E> = backingCollection.iterator()

    override fun containsAll(elements: Collection<E>): Boolean = backingCollection.containsAll(elements)

    override fun equals(other: Any?): Boolean {
        val otherSet = other as? Set<*> ?: return false
        return toSet() == otherSet
    }

    override fun hashCode(): Int = toSet().hashCode()
}
