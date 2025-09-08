package dev.toolkt.core.collections.sets

import dev.toolkt.core.collections.MutableStableCollection

/**
 * A mutable set providing stable handles to its elements.
 */
interface MutableStableSet<E> : MutableStableCollection<E>, MutableSet<E>, StableSet<E>
