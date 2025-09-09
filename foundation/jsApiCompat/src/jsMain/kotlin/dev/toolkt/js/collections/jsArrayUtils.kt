package dev.toolkt.js.collections

import kotlin.js.collections.JsArray
import kotlin.js.collections.JsReadonlyArray

/**
 * Gets the element at the specified index in the array.
 *
 * @return The element at the specified index, or null if the index is out of bounds.
 */
operator fun <E> JsReadonlyArray<E>.get(index: Int): E? = asDynamic()[index]

/**
 * Represents the number of elements in that array. The value is an unsigned, 32-bit integer that is always numerically
 * greater than the highest index in the array.
 */
val <E> JsReadonlyArray<E>.length: Int
    get() = (this as JsArrayImpl<E>).length

/**
 * Executes a provided function once for each array element.
 */
fun <E, JsArrayT : JsReadonlyArray<E>> JsArrayT.forEach(
    /**
     * A function to execute for each element in the array. Its return value is discarded.
     */
    callback: (
        /**
         * The current element being processed in the array.
         */
        E,
        /**
         * The index of the current element being processed in the array.
         */
        Int,
        /**
         * The array [forEach] was called upon.
         */
        JsArrayT,
    ) -> Unit,
) {
    @Suppress("UNCHECKED_CAST") val callback = callback as (E, Int, JsReadonlyArray<E>) -> Unit
    @Suppress("UNCHECKED_CAST") (this as JsArrayImpl<E>).forEach(callback)
}

/**
 * Adds the specified elements to the end of an array and returns the new length of the array.
 *
 * @param value The element to add to the end of the array.
 *
 * @return The new [length] property of the object upon which the method was called.
 */
fun <E> JsArray<E>.push(
    value: E,
): Int = (this as JsArrayImpl<E>).push(value)

/**
 * Removes the last element from an array and returns that element. This method changes the length of the array.
 *
 * @return The removed element from the array; null if the array is empty.
 */
fun <E> JsArray<E>.pop(): E? = (this as JsArrayImpl<E>).pop()

@Suppress("unused")
@JsName("Array")
private external class JsArrayImpl<E> : JsArray<E> {
    // The constructor accepting parameters is cursed and should not be exposed.
    // https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/Array#parameters

    val length: Int

    fun push(value: E): Int

    fun pop(): E?

    fun forEach(
        callback: (E, Int, JsArrayImpl<E>) -> Unit,
    )
}
