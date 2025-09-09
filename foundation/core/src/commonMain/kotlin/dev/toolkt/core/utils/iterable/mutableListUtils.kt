package dev.toolkt.core.utils.iterable

// Thought: Rename to `removeAll`?
fun <ElementT> MutableList<ElementT>.removeRange(indexRange: IntRange) {
    val startIndex = indexRange.first
    val endIndex = indexRange.last

    if (startIndex < 0 || endIndex >= size) {
        throw IndexOutOfBoundsException("Index range $indexRange is out of bounds for list of size $size")
    }

    for (index in endIndex downTo startIndex) {
        removeAt(index)
    }
}

fun <ElementT> MutableList<ElementT>.updateRange(
    indexRange: IntRange,
    elements: Collection<ElementT>,
) {
    if (!indexRange.isEmpty()) {
        removeRange(indexRange = indexRange)
    }

    addAll(indexRange.start, elements)
}


fun <ElementT> MutableList<ElementT>.updateRange(
    indexRange: IntRange,
    dispose: (ElementT) -> Unit,
    newElements: List<ElementT>,
) {
    subList(
        indexRange = indexRange,
    ).forEach { element ->
        dispose(element)
    }

    removeRange(
        indexRange = indexRange,
    )

    addAll(
        index = indexRange.first,
        elements = newElements,
    )
}

/**
 * Appends the given [element] to the end of this mutable list and returns the
 * index of the newly added element.
 */
fun <ElementT> MutableList<ElementT>.append(element: ElementT): Int {
    add(element)
    return indices.last
}

fun <ElementT> MutableList<ElementT>.subList(
    indexRange: OpenEndRange<Int>,
): List<ElementT> = subList(
    fromIndex = indexRange.start,
    toIndex = indexRange.endExclusive,
)
