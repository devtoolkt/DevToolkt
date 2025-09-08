package dev.toolkt.core.math

import kotlin.math.nextDown
import kotlin.math.nextUp

fun avgOf(
    a: Double,
    b: Double,
): Double = (a + b) / 2.0

fun Double.split(): Pair<Int, Double> {
    val integerPart = toInt()
    val fractionalPart = this - integerPart
    return Pair(integerPart, fractionalPart)
}

inline val Double.sq: Double
    get() = this * this

/**
 * Divides the number by the denominator and returns the quotient and remainder.
 *
 * @return A pair of the quotient and the remainder .
 */
fun Double.divideWithRemainder(denominator: Int): Pair<Int, Double> {
    require(denominator >= 1) { "Denominator must be a positive number" }

    val quotient = this / denominator
    val remainder = this % denominator

    return Pair(quotient.toInt(), remainder)
}

/**
 * @param t The value 0..1
 * @return The interpolated value in the range [[x0], [x1]].
 */
fun linearlyInterpolate(
    t: Double,
    x0: Double,
    x1: Double,
): Double {
    require(x0 != x1) { "x0 and x1 must be different to avoid division by zero." }

    return x0 + (t * (x1 - x0))
}

/**
 * Checks if two floating point numbers have different signs, assuming that
 * negative numbers have a "minus" sign, positive numbers have a "plus" sign,
 * and ±0 has a "null" sign.
 */
fun Double.Companion.haveDifferentSigns(
    a: Double,
    b: Double,
): Boolean = a * b < 0.0

/**
 * Generates a sequence of values around this value, including the value itself.
 */
fun Double.valuesAround(
    /**
     * The number of values to generate around this value.
     */
    count: Int,
): Sequence<Double> {
    val smallerValueCount = count / 2
    val greaterValueCount = count - smallerValueCount

    val smallerValueSequence = generateSequence(this) { it.nextDown() }
    val greaterValueSequence = generateSequence(this) { it.nextUp() }.drop(1)

    val smallerValues = smallerValueSequence.take(smallerValueCount).toList().reversed()

    return smallerValues.asSequence() + greaterValueSequence.take(greaterValueCount)
}
