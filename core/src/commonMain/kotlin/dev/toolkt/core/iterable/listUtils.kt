package dev.toolkt.core.iterable

/**
 * Returns the index of the first occurrence of the specified [element] in this list,
 * or `null` if the list does not contain the element.
 */
fun <E> List<E>.indexOfOrNull(
    element: E,
): Int? {
    // This calls `List<T>.indexOf` polymorphic method
    val index = indexOf(element)

    return when {
        index < 0 -> null
        else -> index
    }
}
