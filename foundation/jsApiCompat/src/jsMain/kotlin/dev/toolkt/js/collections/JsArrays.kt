package dev.toolkt.js.collections

/**
 * Exposes the JavaScript [Array] static properties and methods.
 */
@JsName("Array")
external object JsArrays {
    fun <E> of(vararg items: E): kotlin.js.collections.JsArray<E>
}
