package dev.toolkt.core.numeric

import kotlin.math.hypot
import kotlin.math.withSign

data class Complex(
    val real: Double,
    val imaginary: Double,
) : Comparable<Complex> {
    companion object {
        val ZERO: Complex = Complex(0.0, 0.0)

        /**
         * 1.0 / sqrt(2)
         */
        private const val ONE_OVER_ROOT2 = 0.7071067811865476

        /**
         * x + 0i
         */
        fun ofReal(real: Double): Complex = Complex(
            real = real,
            imaginary = 0.0,
        )

        /**
         * x + x * i
         */
        fun diagonal(number: Double): Complex = Complex(
            real = number,
            imaginary = number,
        )
    }

    /**
     * Calculates the magnitude of a complex number.
     */
    fun abs(): Double = hypot(real, imaginary)

    /**
     * Calculates the square root of a complex number.
     */
    fun sqrt(): Complex {
        if (!real.isFinite() || !imaginary.isFinite()) {
            throw NotImplementedError("Calculation of of a complex square root of a non-finite number is not implemented")
        }

        // Compute with positive values and determine sign at the end
        val x = kotlin.math.abs(real)
        val y = kotlin.math.abs(imaginary)

        when {
            y == 0.0 -> {
                // Real only
                val sqrtX = kotlin.math.sqrt(x)

                return when {
                    real < 0 -> Complex(
                        real = 0.0,
                        imaginary = sqrtX.withSign(imaginary),
                    )

                    else -> Complex(
                        real = sqrtX,
                        imaginary = imaginary,
                    )
                }
            }

            x == 0.0 -> {
                // Imaginary only
                val sqrtY2 = kotlin.math.sqrt(y) * ONE_OVER_ROOT2

                return Complex(
                    real = sqrtY2,
                    imaginary = sqrtY2.withSign(imaginary),
                )
            }
        }

        // This computation might underflow for subnormal numbers or overflow
        // for huge numbers. This code could be improved by special-handling
        // those cases.
        val t = kotlin.math.sqrt(2 * (hypot(x, y) + x))

        return when {
            real >= 0.0 -> Complex(t / 2, imaginary / t)
            else -> Complex(y / t, (t / 2).withSign(imaginary))
        }
    }

    override fun compareTo(other: Complex): Int = compareValuesBy(
        this,
        other,
        Complex::real,
        Complex::imaginary,
    )


    operator fun plus(other: Complex): Complex = Complex(
        real = real + other.real,
        imaginary = imaginary + other.imaginary,
    )

    operator fun plus(other: Double): Complex = Complex(
        real = real + other,
        imaginary = imaginary,
    )

    operator fun minus(other: Complex): Complex = Complex(
        real = real - other.real,
        imaginary = imaginary - other.imaginary,
    )

    operator fun minus(other: Double): Complex = Complex(
        real = real - other,
        imaginary = imaginary,
    )

    operator fun times(other: Complex): Complex = Complex(
        real = real * other.real - imaginary * other.imaginary,
        imaginary = real * other.imaginary + imaginary * other.real,
    )

    operator fun times(other: Double): Complex = Complex(
        real = real * other,
        imaginary = imaginary * other,
    )

    operator fun div(other: Complex): Complex {
        val a = this.real
        val b = this.imaginary
        val c = other.real
        val d = other.imaginary

        if (d == 0.0) {
            return this / c
        }

        val den = c * c + d * d

        return Complex(
            (a * c + b * d) / den,
            (b * c - a * d) / den,
        )
    }


    operator fun div(other: Double): Complex = Complex(
        real = real / other,
        imaginary = imaginary / other,
    )
}

operator fun Double.plus(other: Complex): Complex = other + this

operator fun Double.minus(other: Complex): Complex = Complex(
    real = this,
    imaginary = 0.0,
) - other

operator fun Double.times(other: Complex): Complex = other * this

operator fun Int.times(other: Complex): Complex = other * this.toDouble()

operator fun Double.div(other: Complex): Complex = Complex(
    real = this,
    imaginary = 0.0,
) / other

operator fun Int.div(other: Complex): Complex = this.toDouble() / other

fun Complex.sq(): Complex = this * this
