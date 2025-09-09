package dev.toolkt.js.collections

import kotlin.js.collections.JsReadonlyMap
import kotlin.js.collections.JsMap

fun <K, V> JsMap(
    iterable: JsReadonlyMap<K, V>,
): JsMap<K, V> = JsMapImpl(
    iterable = iterable,
)

val <K, V> JsReadonlyMap<K, V>.size: Int
    get() = (this as JsMapImpl).size

fun <K, V> JsReadonlyMap<K, V>.has(
    key: K,
): Boolean = (this as JsMapImpl).has(key)

fun <K, V> JsReadonlyMap<K, V>.get(
    key: K,
): V? = (this as JsMapImpl).get(key)

fun <K, V> JsReadonlyMap<K, V>.forEach(
    callback: (K, V) -> Unit,
) {
    (this as JsMapImpl).forEach(callback)
}

fun <K, V> JsMap<K, V>.set(
    key: K,
    value: V,
): JsMap<K, V> = (this as JsMapImpl).set(key, value)

fun <K, V> JsMap<K, V>.delete(
    key: K,
): Boolean = (this as JsMapImpl).delete(key)

fun <K, V> JsMap<K, V>.clear() {
    (this as JsMapImpl).clear()
}

@Suppress("unused")
@JsName("Map")
private external class JsMapImpl<K, V> : JsMap<K, V> {
    constructor(iterable: JsReadonlyMap<K, V>)

    val size: Int

    fun set(key: K, value: V): JsMap<K, V>

    fun delete(key: K): Boolean

    fun has(key: K): Boolean

    fun get(key: K): V?

    fun clear()

    fun forEach(callback: (K, V) -> Unit)
}
