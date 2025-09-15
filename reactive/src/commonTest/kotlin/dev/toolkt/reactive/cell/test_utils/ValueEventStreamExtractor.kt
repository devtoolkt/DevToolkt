package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.cell.newValues
import dev.toolkt.reactive.cell.updatedValues

interface ValueEventStreamExtractor {
    fun <ValueT> extractValueEventStream(
        cell: Cell<ValueT>,
    ): EventStream<ValueT>
}

data object NewValuesExtractor : ValueEventStreamExtractor {
    override fun <ValueT> extractValueEventStream(
        cell: Cell<ValueT>,
    ): EventStream<ValueT> = cell.newValues
}

data object UpdatedValuesExtractor : ValueEventStreamExtractor {
    override fun <ValueT> extractValueEventStream(
        cell: Cell<ValueT>,
    ): EventStream<ValueT> = cell.updatedValues
}
