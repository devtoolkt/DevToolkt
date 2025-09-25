package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.cell.test_utils.EventStreamSetup
import dev.toolkt.reactive.cell.test_utils.testOccurrencePropagation
import dev.toolkt.reactive.cell.test_utils.testOccurrencePropagationDeactivated
import kotlin.test.Test

@Suppress("ClassName")
class EventStream_merge2_occurrencePropagation_bothOccur_tests {
    private val testedSetup = EventStreamSetup.Merge2EventStreamSetup.configure(
        source1Setup = EventStreamSetup.MapToStringEventStreamSetup.configure(
            sourceSetup = EventStreamSetup.ReplacingEventStreamSetup.configure(
                replacementEvent = 10,
            ),
        ),
        source2Setup = EventStreamSetup.MapToStringEventStreamSetup.configure(
            sourceSetup = EventStreamSetup.ReplacingEventStreamSetup.configure(
                replacementEvent = 20,
            ),
        ),
    )

    private val expectedOccurredEvent = "10"

    @Test
    fun test_occurrencePropagation() {
        testedSetup.testOccurrencePropagation(
            expectedOccurredEvent = expectedOccurredEvent,
        )
    }

    @Test
    fun test_occurrencePropagation_deactivated() {
        testedSetup.testOccurrencePropagationDeactivated()
    }
}
