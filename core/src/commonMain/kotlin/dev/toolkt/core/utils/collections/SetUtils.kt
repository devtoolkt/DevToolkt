package dev.toolkt.core.utils.collections

object SetUtils {
    fun <E> backed(
        collection: Collection<E>,
    ): Set<E> = CollectionBackedSet(
        backingCollection = collection,
    )
}
