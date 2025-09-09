package dev.toolkt.js.collections

import kotlin.js.collections.JsReadonlySet
import kotlin.js.collections.JsSet

fun <E> JsSet(
    iterable: JsReadonlySet<E>,
): JsSet<E> = JsSetImpl(
    iterable = iterable,
)

val <E> JsReadonlySet<E>.size: Int
    get() = (this as JsSetImpl).size

fun <E> JsReadonlySet<E>.has(
    value: E,
): Boolean = (this as JsSetImpl).has(value)

fun <E> JsReadonlySet<E>.forEach(
    callback: (E) -> Unit,
) {
    (this as JsSetImpl).forEach(callback)
}

fun <E> JsSet<E>.add(
    value: E,
): JsSet<E> = (this as JsSetImpl).add(value)

fun <E> JsSet<E>.delete(
    value: E,
): Boolean = (this as JsSetImpl).delete(value)

fun <E> JsSet<E>.clear() {
    (this as JsSetImpl).clear()
}

@Suppress("unused")
@JsName("Set")
private external class JsSetImpl<E> : JsSet<E> {
    constructor(iterable: JsReadonlySet<E>)

    val size: Int

    fun add(value: E): JsSet<E>

    fun delete(value: E): Boolean

    fun has(value: E): Boolean

    fun clear()

    fun forEach(callback: (E) -> Unit)
}
