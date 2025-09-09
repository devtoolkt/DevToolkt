package dev.toolkt.js

abstract external class TypedArray<T> {
    val length: Int

    fun set(array: Array<T>, offset: Int = definedExternally)
}

@JsName("Float32Array")
external class Float32Array(array: Array<Double>) : TypedArray<Double> {
    constructor(size: Int)
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun Float32Array.get(index: Int): Double = asDynamic()[index]

@Suppress("NOTHING_TO_INLINE")
inline operator fun Float32Array.set(index: Int, newItem: Double) {
    asDynamic()[index] = newItem
}

@JsName("Uint16Array")
external class Uint16Array(array: Array<Int>) : TypedArray<Int> {
    constructor(size: Int)
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun Uint16Array.get(index: Int): Int = asDynamic()[index]

@Suppress("NOTHING_TO_INLINE")
inline operator fun Uint16Array.set(index: Int, newItem: Int) {
    asDynamic()[index] = newItem
}
