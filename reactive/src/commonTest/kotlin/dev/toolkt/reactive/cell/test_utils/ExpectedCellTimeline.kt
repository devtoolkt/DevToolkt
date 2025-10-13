package dev.toolkt.reactive.cell.test_utils

data class ExpectedCellTimeline<ValueT : Any>(
    val expectedInitialValue: ValueT,
    val expectedNotificationByTick: Map<Tick, ExpectedNotification<ValueT>>,
) {
    sealed interface ExpectedNotification<out ValueT : Any> {
        val expectedUpdatedValue: ValueT?
    }

    sealed interface ExpectedUpdate<out ValueT : Any> : ExpectedNotification<ValueT> {
        override val expectedUpdatedValue: ValueT
    }

    sealed interface ExpectedFreezingNotification<out ValueT : Any> : ExpectedNotification<ValueT>

    data class ExpectedPlainUpdate<out ValueT : Any>(
        override val expectedUpdatedValue: ValueT,
    ) : ExpectedUpdate<ValueT>

    data class ExpectedFreezingUpdate<out ValueT : Any>(
        override val expectedUpdatedValue: ValueT,
    ) : ExpectedFreezingNotification<ValueT>, ExpectedUpdate<ValueT>


    data object ExpectedJustFreeze : ExpectedFreezingNotification<Nothing> {
        override val expectedUpdatedValue: Nothing? = null
    }

    val maxTick: Tick?
        get() = expectedNotificationByTick.keys.maxByOrNull { it.t }
}
