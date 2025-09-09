package dev.toolkt.js

@Suppress("NOTHING_TO_INLINE")
inline fun jsObject(): dynamic = js("({})")

@Suppress("NOTHING_TO_INLINE")
inline fun <reified MutableObjectT> jsObject(
    block: MutableObjectT.() -> Unit,
): MutableObjectT {
    val obj = jsObject()

    (obj as MutableObjectT).block()

    return obj
}
