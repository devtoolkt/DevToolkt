package dev.toolkt.core.utils

fun <T : Comparable<T>> Pair<T, T>.sorted(): Pair<T, T> = when {
    first <= second -> this
    else -> Pair(second, first)
}
