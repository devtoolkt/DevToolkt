package dev.toolkt.reactive.cell.test_utils.timeline

sealed class ExpectedCellTimeline<ValueT : Any> {
    data class Const<ValueT : Any>(
        val expectedConstValue: ValueT,
    ) : ExpectedCellTimeline<ValueT>() {
        override fun validate(
            minTick: RawTick,
            maxTick: RawTick,
        ) {
            // The const timeline is always valid
        }
    }

    data class Dynamic<ValueT : Any>(
        val expectedInitialValue: ValueT,
        val expectedUpdatedValueByTick: Map<RawTick, ValueT>,
        val expectedFreezeTick: RawTick?,
    ) : ExpectedCellTimeline<ValueT>() {
        data class UpdateEntry<ValueT : Any>(
            val tick: RawTick,
            val updatedValue: ValueT,
        )

        constructor(
            expectedInitialValue: ValueT,
            expectedUpdatedValueByNamedTick: Map<BaseNamedTick, ValueT>,
            expectedFreezeNamedTick: BaseNamedTick?,
        ) : this(
            expectedInitialValue = expectedInitialValue,
            expectedUpdatedValueByTick = expectedUpdatedValueByNamedTick.mapKeys { (namedTick, _) ->
                namedTick.ordinalTick
            },
            expectedFreezeTick = expectedFreezeNamedTick?.ordinalTick,
        )

        val updateEntriesInOrder = expectedUpdatedValueByTick.entries.map { (tick, updatedValue) ->
            UpdateEntry(
                tick = tick,
                updatedValue = updatedValue,
            )
        }.sortedBy { it.tick.t }

        val firstExpectedUpdateTick: RawTick?
            get() = updateEntriesInOrder.firstOrNull()?.tick

        fun shouldExpectFreeze(
            tick: RawTick,
        ): Boolean = expectedFreezeTick == tick

        fun shouldBeFrozenBefore(
            tick: RawTick,
        ): Boolean {
            val expectedFreezeTick = this.expectedFreezeTick ?: return false
            return tick.t > expectedFreezeTick.t
        }

        fun shouldBeFrozenAfter(
            tick: RawTick,
        ): Boolean {
            val expectedFreezeTick = this.expectedFreezeTick ?: return false
            return tick.t >= expectedFreezeTick.t
        }

        fun getExpectedOldValue(
            tick: RawTick,
        ): ValueT {
            val precedingUpdateEntry = updateEntriesInOrder.asSequence().takeWhile {
                it.tick.isEarlierThan(tick)
            }.lastOrNull() ?: return expectedInitialValue

            return precedingUpdateEntry.updatedValue
        }

        fun getExpectedUpdatedValue(
            tick: RawTick,
        ): ValueT? = expectedUpdatedValueByTick[tick]

        override fun validate(
            minTick: RawTick,
            maxTick: RawTick,
        ) {
            expectedUpdatedValueByTick.keys.forEach { updateTick ->
                require(!updateTick.isEarlierThan(minTick)) {
                    "An expected update tick (t=${updateTick.t}) cannot be earlier than the minimum tick (t=${minTick.t})."
                }

                require(!updateTick.isLaterThan(maxTick)) {
                    "An expected update tick (t=${updateTick.t}) cannot be later than the maximum tick (t=${maxTick.t})."
                }
            }

            expectedFreezeTick?.let { freezeTick ->
                require(!freezeTick.isEarlierThan(minTick)) {
                    "The expected freeze tick (t=${freezeTick.t}) cannot be earlier than the minimum tick (t=${minTick.t})."
                }

                require(!freezeTick.isLaterThan(maxTick)) {
                    "The expected freeze tick (t=${freezeTick.t}) cannot be later than the maximum tick (t=${maxTick.t})."
                }
            }
        }
    }

    abstract fun validate(
        minTick: RawTick,
        maxTick: RawTick,
    )
}
