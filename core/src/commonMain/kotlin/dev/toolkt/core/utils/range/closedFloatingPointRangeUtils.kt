package dev.toolkt.core.utils.range

import dev.toolkt.core.utils.iterable.LinSpace
import dev.toolkt.core.utils.avgOf
import dev.toolkt.core.utils.linearlyInterpolate
import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.core.numeric.equalsZeroWithTolerance

object ClosedFloatingPointRangeUtils {
    fun around(
        x0: Double,
        width: Double,
    ): ClosedFloatingPointRange<Double> {
        require(width >= 0.0) { "Width must be non-negative." }

        return (x0 - width / 2.0).rangeTo(x0 + width / 2.0)
    }
}

/**
 * Normalizes the value to the range [start, end].
 *
 * @return value within 0..1
 */
fun ClosedFloatingPointRange<Double>.normalize(x: Double): Double {
    val x0 = start
    val x1 = endInclusive

    require(x0 != x1) { "x0 and x1 must be different to avoid division by zero." }

    return (x - x0) / (x1 - x0)
}

/**
 * @param t The value 0..1
 * @return The interpolated value in the range [start, endInclusive].
 */
fun ClosedFloatingPointRange<Double>.linearlyInterpolate(t: Double): Double = linearlyInterpolate(
    t = t,
    x0 = start,
    x1 = endInclusive,
)

fun ClosedFloatingPointRange<Double>.rescaleTo(
    targetRange: ClosedFloatingPointRange<Double>,
    x: Double,
): Double {
    val normalized = this.normalize(x)
    return targetRange.linearlyInterpolate(normalized)
}

val ClosedFloatingPointRange<Double>.width: Double
    get() = endInclusive - start

val ClosedFloatingPointRange<Double>.midpoint: Double
    get() = avgOf(start, endInclusive)

val OpenEndRange<Double>.midpoint: Double
    get() = avgOf(start, endExclusive)

fun ClosedFloatingPointRange<Double>.split(): Pair<ClosedFloatingPointRange<Double>, ClosedFloatingPointRange<Double>> {
    val midpoint = this.midpoint

    return Pair(
        start..midpoint,
        midpoint..endInclusive,
    )
}

fun OpenEndRange<Double>.split(): Pair<OpenEndRange<Double>, OpenEndRange<Double>> {
    val midpoint = this.midpoint

    return Pair(
        start.rangeUntil(midpoint),
        midpoint.rangeUntil(endExclusive),
    )
}

fun ClosedFloatingPointRange<Double>.extend(
    bleed: Double,
): ClosedFloatingPointRange<Double> = copy(
    start = start - bleed,
    endInclusive = endInclusive + bleed,
)

fun ClosedFloatingPointRange<Double>.copy(
    start: Double = this.start,
    endInclusive: Double = this.endInclusive,
): ClosedFloatingPointRange<Double> = start..endInclusive

fun OpenEndRange<Double>.subdivide(
    segmentCount: Int,
): Sequence<OpenEndRange<Double>> = LinSpace(
    range = this.withEndIncluded(),
    sampleCount = segmentCount + 1,
).generateOpenSubRanges()

fun ClosedFloatingPointRange<Double>.withEndExcluded(): OpenEndRange<Double> = start.rangeUntil(endInclusive)

val OpenEndRange<Double>.width: Double
    get() = endExclusive - start

fun OpenEndRange<Double>.withEndIncluded(): ClosedFloatingPointRange<Double> = start..endExclusive

fun OpenEndRange<Double>.isEmptyWithTolerance(
    tolerance: NumericTolerance.Absolute,
): Boolean = this.width.equalsZeroWithTolerance(
    tolerance = tolerance,
)
