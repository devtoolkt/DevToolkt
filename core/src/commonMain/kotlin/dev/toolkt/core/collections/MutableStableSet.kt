package dev.toolkt.core.collections

/**
 * A mutable set providing stable handles to its elements.
 */
interface MutableStableSet<E> : MutableStableCollection<E>, MutableSet<E>, StableSet<E>
