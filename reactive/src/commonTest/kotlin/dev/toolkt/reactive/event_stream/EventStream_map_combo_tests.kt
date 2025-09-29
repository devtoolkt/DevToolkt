package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.event_stream.test_utils.EventStreamSubscriptionUtils
import kotlin.test.Test
import kotlin.test.assertNull

@Suppress("ClassName")
class EventStream_map_combo_tests {
    @Test
    fun test_silentSource() {
        // TODO: Silent source factory?

        val sourceEventStream: EventStream<Any> = NeverEventStream

        val mapEventStream = sourceEventStream.map { it.toString() }

        assertNull(
            mapEventStream.subscribe { },
        )
    }

    @Test
    fun test_sourceUpdate() {
        val sourceEventStream = EmitterEventStream<Int>()

        val mapEventStream = sourceEventStream.map { it.toString() }

        val updateVerifier = EventStreamSubscriptionUtils.subscribeForTesting(
            eventStream = mapEventStream,
        )

        sourceEventStream.emit(10)

        updateVerifier.assertOccurredEventEquals(
            expectedOccurredEvent = "10",
        )
    }
}
