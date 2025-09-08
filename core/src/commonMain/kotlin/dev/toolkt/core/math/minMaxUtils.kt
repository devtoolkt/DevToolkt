package dev.toolkt.core.math

fun <T, R : Comparable<R>> maxBy(
    a: T,
    b: T,
    selector: (T) -> R,
): T = when {
    selector(a) >= selector(b) -> a

    else -> b
}
