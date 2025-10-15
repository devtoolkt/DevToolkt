package dev.toolkt.reactive.cell.test_utils

data class GivenCellTimeline<ValueT : Any>(
    val givenInitialValue: ValueT,
    val givenUpdateByTick: Map<Tick, GivenPlainUpdate<ValueT>>,
) {
    /**
     * A plain update in the cell's value (not final).
     */
    data class GivenPlainUpdate<out ValueT : Any>(
        val givenUpdatedValue: ValueT,
    ) {
        companion object {
            fun <ValueT : Any> of(
                givenUpdatedValue: ValueT,
            ): GivenPlainUpdate<ValueT> = GivenPlainUpdate(
                givenUpdatedValue = givenUpdatedValue,
            )
        }
    }

    val freezeTick: Tick?
        get() = TODO()
}
