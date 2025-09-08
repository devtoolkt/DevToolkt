package dev.toolkt.core.range

fun IntRange.Companion.empty(value: Int): IntRange = value until value

fun IntRange.Companion.single(value: Int): IntRange = value..value

val IntRange.width: Int
    get() = endInclusive - start + 1

fun IntRange.shift(
    delta: Int,
): IntRange = (start + delta)..(endInclusive + delta)
