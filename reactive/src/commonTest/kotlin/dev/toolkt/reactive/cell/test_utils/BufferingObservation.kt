package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.observe

interface BufferingObservation<ValueT> : Cell.Observation {
    fun extractReceivedNotifications(): List<Cell.Notification<ValueT>>
}

fun <ValueT> Cell<ValueT>.observeBuffering(): BufferingObservation<ValueT>? {
    val receivedNotifications = mutableListOf<Cell.Notification<ValueT>>()

    val rawObservation = this.observe(
        object : Cell.Observer<ValueT> {
            override fun handleNotification(
                notification: Cell.Notification<ValueT>,
            ) {
                receivedNotifications.add(notification)
            }
        },
    ) ?: return null

    return object : BufferingObservation<ValueT> {
        override fun extractReceivedNotifications(): List<Cell.Notification<ValueT>> =
            receivedNotifications.toList().also {
                receivedNotifications.clear()
            }

        override fun cancel() {
            rawObservation.cancel()
        }
    }
}
