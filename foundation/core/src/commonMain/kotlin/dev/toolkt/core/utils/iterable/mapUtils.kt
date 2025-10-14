package dev.toolkt.core.utils.iterable

fun <K, V> mapOfNotNull(
    vararg pairs: Pair<K, V>?,
): Map<K, V> {
    val result = mutableMapOf<K, V>()

    for (pair in pairs) {
        if (pair != null) {
            result[pair.first] = pair.second
        }
    }

    return result
}
