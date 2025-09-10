package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.event_stream.Cell
import dev.toolkt.reactive.event_stream.newValues
import dev.toolkt.reactive.event_stream.subscribe

/**
 * A cell is "energized" if it's forced to actively notify its dependents about the events / updates.
 *
 * Although an energized  cell is forced to _notify_ about the updates, it doesn't mean that it actively maintains its
 * value (it might be optimized for memory usage not performance).
 *
 * A cell is "de-energized" if it might be possible for it to exist in an internally passive state,
 * when it doesn't need to notify its dependents about the events / updates or even process them in any way.
 *
 * The fact that a cell is de-energized does not _guarantee_ that the cell is not internally active, as it might be
 * updating its internal value proactively (it might be optimized for performance not memory usage).
 *
 * The exact nature of energization and its technical consequences depend on the implementation of the reactive system,
 * but nearly always it's a possible source of subtle bugs and corner-cases.
 */
interface Energization {
    /**
     * "Cut off" the energization, allowing the cell to become de-energized if there are no other reasons for it to
     * stay energized.
     */
    fun cutOff()
}

/**
 * Energize the cell, returning an [Energization] object that can be used to cancel the energization.
 */
fun <ValueT> Cell<ValueT>.energize(): Energization {
    // The simplest way to guarantee that the cell is energized is to subscribe to its `newValues` event stream.
    val subscription = newValues.subscribe { }

    return object : Energization {
        override fun cutOff() {
            subscription.cancel()
        }
    }
}
