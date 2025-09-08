package dev.toolkt.core.iterable

fun <T, R : Comparable<R>> List<T>.indexOfMaxBy(
    fromIndex: Int = 0,
    toIndex: Int = size,
    selector: (T) -> R,
): Int {
    val (index, _) = withIndex().toList().subList(
        fromIndex = fromIndex,
        toIndex = toIndex,
    ).maxBy { (_, v) -> selector(v) }

    return index
}

/**
 * Returns the index of the first occurrence of the specified [element] in this iterable,
 * or `null` if the iterable does not contain the element.
 */
fun <E> Iterable<E>.indexOfOrNull(
    element: E,
): Int? {
    // This calls `Iterable<T>.indexOf` extension method
    val index = indexOf(element)

    return when {
        index < 0 -> null
        else -> index
    }
}

/**
 * Returns the index of the first occurrence of the specified [element] in this sequence,
 * or `null` if the sequence does not contain the element.
 */
fun <E> Sequence<E>.indexOfOrNull(
    element: E,
): Int? {
    // This calls `Sequence<T>.indexOf` extension method
    val index = indexOf(element)

    return when {
        index < 0 -> null
        else -> index
    }
}
