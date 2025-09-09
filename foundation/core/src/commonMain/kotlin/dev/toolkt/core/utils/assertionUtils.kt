package dev.toolkt.core.utils

fun assert(
    condition: Boolean,
    message: () -> String,
) {
    if (!condition) {
        throw AssertionError(message())
    }
}
