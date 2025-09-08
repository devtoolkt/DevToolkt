package dev.toolkt.js.collections

/**
 * The [JsSet] object lets you store unique values of any type, whether primitive values or object references.
 *
 * Kotlin's [Any.equals] / [Any.hashCode] methods are not honored.
 */
@JsName("Set")
external class JsSet<E> {
    /**
     * Creates a new empty JsSet.
     */
    constructor()

    /**
     * Creates a new JsSet with the elements of the given set.
     *
     * @param other the set to copy elements from
     */
    constructor(other: JsSet<E>)

    val size: Int

    fun add(value: E): JsSet<E>

    fun delete(value: E): Boolean

    fun has(value: E): Boolean

    fun clear()

    fun forEach(callback: (E) -> Unit)
}
