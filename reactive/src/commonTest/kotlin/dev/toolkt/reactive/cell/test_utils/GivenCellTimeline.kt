package dev.toolkt.reactive.cell.test_utils

data class GivenCellTimeline<ValueT : Any>(
    val givenInitialValue: ValueT,
    val givenNotificationByTick: Map<Tick, GivenNotification<ValueT>>,
) {
    /**
     * A piece of information about a change in the cell's state.
     */
    sealed interface GivenNotification<out ValueT : Any> {
        val givenUpdatedValue: ValueT?
    }

    /**
     * A notification about an update in the cell's value.
     */
    sealed interface GivenUpdate<out ValueT : Any> : GivenNotification<ValueT> {
        companion object {
            fun <ValueT : Any> of(
                givenUpdatedValue: ValueT,
                shouldFreeze: Boolean,
            ): GivenUpdate<ValueT> = when {
                shouldFreeze -> GivenFreezingUpdate_deprecated.of(
                    givenUpdatedValue = givenUpdatedValue,
                )

                else -> GivenPlainUpdate.of(
                    givenUpdatedValue = givenUpdatedValue,
                )
            }
        }

        override val givenUpdatedValue: ValueT
    }

    /**
     * The last notification, after which the cell becomes frozen.
     */
    sealed interface GivenFreezingNotification<out ValueT : Any> : GivenNotification<ValueT> {
        companion object {
            fun <ValueT : Any> of(
                givenUpdatedValue: ValueT?,
            ): GivenFreezingNotification<ValueT> = when {
                givenUpdatedValue != null -> GivenFreezingUpdate_deprecated.of(
                    givenUpdatedValue = givenUpdatedValue,
                )

                else -> GivenJustFreeze_deprecated
            }
        }
    }

    /**
     * A plain update in the cell's value (not final).
     */
    abstract class GivenPlainUpdate<out ValueT : Any> : GivenUpdate<ValueT> {
        companion object {
            fun <ValueT : Any> of(
                givenUpdatedValue: ValueT,
            ): GivenPlainUpdate<ValueT> = object : GivenPlainUpdate<ValueT>() {
                override val givenUpdatedValue: ValueT = givenUpdatedValue
            }
        }
    }

    /**
     * A final update in the cell's value, after which the cell becomes frozen.
     */
    abstract class GivenFreezingUpdate_deprecated<out ValueT : Any> : GivenFreezingNotification<ValueT>, GivenUpdate<ValueT> {
        companion object {
            fun <ValueT : Any> of(
                givenUpdatedValue: ValueT,
            ): GivenFreezingUpdate_deprecated<ValueT> = object : GivenFreezingUpdate_deprecated<ValueT>() {
                override val givenUpdatedValue: ValueT = givenUpdatedValue
            }
        }
    }

    /**
     * A final notification that the cell has become frozen, without any update in its value.
     */
    data object GivenJustFreeze_deprecated : GivenFreezingNotification<Nothing> {
        override val givenUpdatedValue: Nothing? = null
    }

    init {
        require(
            givenNotificationByTick.count { (_, notification) ->
                notification is GivenFreezingNotification
            } <= 1,
        ) {
            "At most one freezing update is allowed"
        }

        givenNotificationByTick.entries.find { (time, notification) ->
            notification is GivenFreezingNotification
        }?.let { (freezeTick, _) ->
            require(
                givenNotificationByTick.none { (time, _) ->
                    time.t > freezeTick.t
                },
            ) {
                "No notifications are allowed after the freezing notification at t=${freezeTick.t}"
            }
        }
    }

    val freezeTick: Tick?
        get() = givenNotificationByTick.entries.singleOrNull { (_, givenNotification) ->
            givenNotification is GivenFreezingNotification
        }?.key
}
