package dev.toolkt.reactive.cell.test_utils

import kotlin.jvm.JvmInline

@JvmInline
value class Tick(
    /**
     * The t-value of the moment.
     */
    val t: Int,
) : TickAlike {
    init {
        require(t >= 0) {
            "The tick's t-value must be non-negative, but was $t."
        }
    }

    override val asTick: Tick
        get() = this
}
