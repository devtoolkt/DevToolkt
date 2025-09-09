package dev.toolkt.js.collections

import kotlin.js.collections.JsArray
import kotlin.js.collections.JsReadonlyArray

/**
 * Creates [Array] objects.
 *
 * @param elements A JavaScript array is initialized with the given elements, except in the case where a single argument
 * is passed to the Array constructor and that argument is a number (see the arrayLength parameter below). Note that
 * this special case only applies to JavaScript arrays created with the Array constructor, not array literals created
 * with the square bracket syntax.
 */
@Deprecated("This constructor is cursed")
fun <E> JsArray(
    vararg elements: E,
): JsArray<E> = JsArrayImpl(
    elements = elements,
)

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
    /**
     * This constructor is cursed. See the sample.
     *
     * @sample jsArrayImplConstructorSample
     *
     * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/Array#parameters
     */
    @Deprecated("This constructor is cursed and should not be used")
    constructor(
        vararg elements: E,
    )

    val length: Int

    fun push(value: E): Int

    fun pop(): E?

    fun forEach(
        callback: (E, Int, JsArrayImpl<E>) -> Unit,
    )
}

@Suppress("unused")
private fun jsArrayImplConstructorSample() {
    JsArrayImpl("foo", "bar")
    // Array [ "foo", "bar" ]

    JsArrayImpl("foo")
    // Array [ "foo" ]

    JsArrayImpl(2, 3)
    // Array [ 2, 3 ]

    JsArrayImpl(2)
    // Array [ <2 empty slots> ]

    JsArrayImpl(-1)
    // RangeError: invalid array length
}
