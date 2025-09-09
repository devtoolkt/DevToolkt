package dev.toolkt.js

@Suppress("NOTHING_TO_INLINE")
inline fun jsObject(): dynamic = js("({})")

inline fun jsObject(
    block: dynamic.() -> Unit,
): dynamic {
    val obj = jsObject()

    obj.block()

    return obj
}
