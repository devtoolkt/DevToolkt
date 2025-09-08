package dev.toolkt.core.numeric

import dev.toolkt.core.math.Complex
import dev.toolkt.core.utils.maxBy
import kotlin.math.absoluteValue

/**
 * Numeric tolerance is a way to compare two floating-point numbers
 * with a certain degree of tolerance, rather than requiring exact equality.
 */
sealed class NumericTolerance {
    /**
     * Interface for finding an absolute measure of a value. The [AbsoluteMeasurement] implementation must obey the
     * following laws:
     *
     * 1. `measure(value) >= 0` for all values
     * 2. `measure(value) == 0` if and only if `value == ZERO`, where `ZERO` is a zero value of type `T`
     */
    interface AbsoluteMeasurement<in T> {
        fun abs(value: T): Double
    }

    /**
     * Interface for measuring the difference between two values. In addition to the [AbsoluteMeasurement] laws, the
     * [RelativeMeasurement] implementation must obey the following laws:
     *
     * 3. `substruct(value, reference) == ZERO` if and only if `value == reference`
     */
    interface RelativeMeasurement<T> : AbsoluteMeasurement<T> {
        fun substruct(value: T, reference: T): T
    }

    object DoubleMeasurement : RelativeMeasurement<Double> {
        override fun substruct(value: Double, reference: Double): Double = value - reference

        override fun abs(value: Double): Double = value.absoluteValue
    }

    object ComplexMeasurement : RelativeMeasurement<Complex> {
        override fun substruct(value: Complex, reference: Complex): Complex = value - reference

        override fun abs(value: Complex): Double = value.abs()
    }

    /**
     * Zero tolerance
     *
     * The only value that will be considered equal to the reference value
     * is the reference value itself.
     *
     * Useful in cases where an algorithm was designed with numeric tolerance
     * in mind, but in a specific case it's actually preferable to use exact
     * equality.
     */
    data object Zero : NumericTolerance() {
        override fun <T> equalsApproximately(
            value: T,
            reference: T,
            measurement: RelativeMeasurement<T>,
        ): Boolean = value == reference

        override fun <T> equalsApproximatelyZero(
            value: T,
            measurement: AbsoluteMeasurement<T>,
        ): Boolean = measurement.abs(value) == 0.0
    }

    sealed class Proper : NumericTolerance() {
        final override fun <T> equalsApproximately(
            value: T,
            reference: T,
            measurement: RelativeMeasurement<T>,
        ): Boolean {
            val effectiveTolerance = determineEffectiveTolerance(
                value = value,
                reference = reference,
                measurement = measurement,
            )

            val difference = measurement.substruct(
                value = value,
                reference = reference,
            )

            return effectiveTolerance.equalsApproximatelyZero(
                value = difference,
                measurement = measurement,
            )
        }

        abstract fun <T> determineEffectiveTolerance(
            value: T,
            reference: T,
            measurement: RelativeMeasurement<T>,
        ): Absolute
    }

    /**
     * Hybrid tolerance
     *
     * Hybrid tolerance uses the higher of two thresholds determined by
     * the [relative] and the [absolute] tolerance.
     *
     * Useful for cases where the compared values are expected to be
     * potentially both very small and very large, but it's also possible
     * that the reference value can be exactly zero. In such cases, relative
     * tolerance degrades to zero tolerance, which might be unacceptable.
     */
    data class Hybrid(
        /**
         * Relative tolerance, effective in the general case
         */
        val relative: Relative,
        /**
         * Absolute tolerance, effective for reference values close to zero
         */
        val absolute: Absolute,
    ) : Proper() {
        override fun <T> determineEffectiveTolerance(
            value: T,
            reference: T,
            measurement: RelativeMeasurement<T>,
        ): Absolute {
            val effectiveRelativeTolerance = relative.determineEffectiveTolerance(
                value = value,
                reference = reference,
                measurement = measurement,
            )

            return maxBy(
                absolute,
                effectiveRelativeTolerance,
            ) {
                it.absoluteTolerance
            }
        }

        override fun <T> equalsApproximatelyZero(
            value: T,
            measurement: AbsoluteMeasurement<T>,
        ): Boolean = absolute.equalsApproximatelyZero(
            value = value,
            measurement = measurement,
        )

        companion object {
            val Default: Hybrid = Hybrid(
                relative = Relative.Default,
                absolute = Absolute.Default,
            )
        }

    }

    /**
     * Absolute tolerance: |v - v_ref| ≤ a
     *
     * Useful for cases where the order of magnitude of the compared values
     * is known up front.
     */
    data class Absolute(
        /**
         * The absolute tolerance threshold
         */
        val absoluteTolerance: Double,
    ) : Proper() {
        companion object {
            val Default = Absolute(
                absoluteTolerance = 1e-6,
            )
        }

        operator fun times(factor: Double) = Absolute(
            absoluteTolerance = absoluteTolerance * factor,
        )

        override fun <T> equalsApproximatelyZero(
            value: T,
            measurement: AbsoluteMeasurement<T>,
        ): Boolean = measurement.abs(value) <= absoluteTolerance

        override fun <T> determineEffectiveTolerance(
            value: T,
            reference: T,
            measurement: RelativeMeasurement<T>,
        ): Absolute = this
    }

    /**
     * Asymmetric relative tolerance: |v - v_ref| ≤ r * |v_ref|
     *
     * Useful for cases where the compared values are expected to be
     * potentially both very small and very large. If the reference value
     * is zero, it should be noted that _only_ ±0.0 will be considered
     * equal to the reference value (within tolerance), no matter what
     * relative tolerance factor we pick.
     */
    data class Relative(
        /**
         * The relative tolerance factor
         */
        val relativeTolerance: Double,
    ) : Proper() {
        companion object {
            val Default = Relative(
                relativeTolerance = 1e-14,
            )
        }

        override fun <T> equalsApproximatelyZero(
            value: T,
            measurement: AbsoluteMeasurement<T>,
        ): Boolean = false

        override fun <T> determineEffectiveTolerance(
            value: T,
            reference: T,
            measurement: RelativeMeasurement<T>,
        ): Absolute = Absolute(
            absoluteTolerance = relativeTolerance * measurement.abs(reference),
        )

        init {
            require(relativeTolerance > 0.0 && relativeTolerance < 0.25)
        }
    }

    companion object {
        val Default: NumericTolerance
            get() = Absolute.Default
    }

    /**
     * Check if the [value] is approximately equal to the [reference] within
     * the tolerance defined by this object, using the provided [measurement].
     * This operation might be asymmetric.
     */
    abstract fun <T> equalsApproximately(
        value: T,
        reference: T,
        measurement: RelativeMeasurement<T>,
    ): Boolean

    /**
     * Check if the [value] is approximately equal to the zero within
     * the tolerance defined by this object, using the provided [measurement].
     * This operation might be asymmetric.
     */
    abstract fun <T> equalsApproximatelyZero(
        value: T,
        measurement: AbsoluteMeasurement<T>,
    ): Boolean
}

fun NumericTolerance.equalsApproximately(
    value: Double,
    reference: Double,
): Boolean = equalsApproximately(
    value = value,
    reference = reference,
    measurement = NumericTolerance.DoubleMeasurement,
)

fun NumericTolerance.Absolute.equalsApproximatelyZero(
    value: Double,
): Boolean = equalsApproximatelyZero(
    value = value,
    measurement = NumericTolerance.DoubleMeasurement,
)

fun NumericTolerance.equalsApproximately(
    value: Complex,
    reference: Complex,
): Boolean = equalsApproximately(
    value = value,
    reference = reference,
    measurement = NumericTolerance.ComplexMeasurement,
)

fun NumericTolerance.Absolute.equalsApproximatelyZero(
    value: Complex,
): Boolean = equalsApproximatelyZero(
    value = value,
    measurement = NumericTolerance.ComplexMeasurement,
)
