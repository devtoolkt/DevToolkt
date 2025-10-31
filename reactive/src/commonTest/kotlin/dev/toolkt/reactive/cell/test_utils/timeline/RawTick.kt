package dev.toolkt.reactive.cell.test_utils.timeline

import kotlin.jvm.JvmInline

@JvmInline
value class RawTick(
    /**
     * The t-value of the moment.
     */
    val t: Int,
) {
    companion object {
        val First = RawTick(t = 0)
    }

    init {
        require(t >= 0) {
            "The tick's t-value must be non-negative, but was $t."
        }
    }

    fun isEarlierThan(
        other: RawTick,
    ): Boolean = t < other.t

    fun isLaterThan(
        other: RawTick,
    ): Boolean = t > other.t

    val next: RawTick
        get() = RawTick(t = t + 1)

    fun generateTicksUpTo(
        lastTick: RawTick,
    ) = generateSequence(this) { it.next }.takeWhile {
        !it.isLaterThan(lastTick)
    }
}
