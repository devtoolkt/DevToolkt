package dev.toolkt.reactive.cell.test_utils

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.updatedValues
import dev.toolkt.reactive.event_stream.subscribe

sealed interface SamplingPreparationStrategy {
    data object None : SamplingPreparationStrategy {
        override fun <ValueT> prepare(subjectCell: Cell<ValueT>) {
        }
    }

    data object PreActivate : SamplingPreparationStrategy {
        override fun <ValueT> prepare(subjectCell: Cell<ValueT>) {
            subjectCell.updatedValues.subscribe { }
        }
    }

    fun <ValueT> prepare(
        subjectCell: Cell<ValueT>,
    )
}
