package dev.toolkt.core.collections

/**
 * Bag is a collection that allows duplicates, but doesn't guarantee meaningful
 * order.
 */
interface Bag<out E> : Collection<E>
