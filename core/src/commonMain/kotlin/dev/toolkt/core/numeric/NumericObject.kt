package dev.toolkt.core.numeric

import kotlin.jvm.JvmName

/**
 * Interface for numeric objects that can be compared with [NumericTolerance].
 */
interface NumericObject {
    fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericTolerance = NumericTolerance.Default,
    ): Boolean
}

fun <T : NumericObject> T?.equalsWithToleranceOrNull(
    other: NumericObject?,
    tolerance: NumericTolerance = NumericTolerance.Default,
): Boolean = when {
    this != null && other != null -> equalsWithTolerance(
        other = other,
        tolerance = tolerance,
    )

    this == null && other == null -> true

    else -> false
}

fun Double.equalsZeroWithTolerance(
    tolerance: NumericTolerance.Absolute = NumericTolerance.Absolute.Default,
): Boolean = tolerance.equalsApproximatelyZero(this)

fun Double.equalsWithTolerance(
    other: Double,
    tolerance: NumericTolerance = NumericTolerance.Default,
): Boolean = tolerance.equalsApproximately(value = this, reference = other)

fun Double.divideWithTolerance(
    divisor: Double,
    tolerance: NumericTolerance = NumericTolerance.Default,
): Double? = when {
    divisor.equalsWithTolerance(
        0.0,
        tolerance = tolerance,
    ) -> null

    else -> this / divisor
}

@JvmName("equalsWithToleranceListDouble")
fun List<Double>.equalsWithTolerance(
    other: List<Double>,
    tolerance: NumericTolerance = NumericTolerance.Default,
): Boolean {
    if (this.size != other.size) return false

    return zip(other).all { (a, b) ->
        a.equalsWithTolerance(b, tolerance)
    }
}

@JvmName("equalsWithToleranceOrNullListDouble")
fun List<Double>?.equalsWithToleranceOrNull(
    other: List<Double>?,
    tolerance: NumericTolerance = NumericTolerance.Default,
): Boolean = when {
    this != null && other != null -> equalsWithTolerance(
        other = other,
        tolerance = tolerance,
    )

    this == null && other == null -> true

    else -> false
}

@JvmName("equalsWithToleranceListNumericObject")
fun List<NumericObject>.equalsWithTolerance(
    other: List<NumericObject>,
    tolerance: NumericTolerance = NumericTolerance.Default,
): Boolean {
    if (this.size != other.size) return false

    return zip(other).all { (a, b) ->
        a.equalsWithTolerance(b, tolerance)
    }
}
