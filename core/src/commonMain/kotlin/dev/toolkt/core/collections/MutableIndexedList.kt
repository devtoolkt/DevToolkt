package dev.toolkt.core.collections

interface MutableIndexedList<E> : MutableStableList<E>, IndexedList<E>

fun <E> mutableIndexedListOf(
    vararg elements: E,
): MutableIndexedList<E> = mutableTreeListOf(*elements)
