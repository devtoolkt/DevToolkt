package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.event_stream.test_utils.EventStreamSubscriptionUtils
import kotlin.test.Test

@Suppress("ClassName")
class EventStream_map_combo_tests {
    @Test
    fun test_sourceUpdate() {
        val sourceEventStream = EmitterEventStream<Int>()

        val mapEventStream = sourceEventStream.map { it.toString() }

        val asserter = EventStreamSubscriptionUtils.subscribeForTesting(
            eventStream = mapEventStream,
        )

        sourceEventStream.emit(10)

        asserter.assertOccurredEventEquals(
            expectedOccurredEvent = "10",
        )
    }
}
