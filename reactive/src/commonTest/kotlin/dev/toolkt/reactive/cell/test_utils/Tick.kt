package dev.toolkt.reactive.cell.test_utils

import kotlin.jvm.JvmInline

@JvmInline
value class Tick(
    /**
     * The t-value of the moment.
     */
    val t: Int,
) {
    init {
        require(t > 0) {
            "The tick's t-value must be positive, but was $t."
        }
    }
}
