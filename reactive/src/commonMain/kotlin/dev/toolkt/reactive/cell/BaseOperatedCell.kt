package dev.toolkt.reactive.cell

import dev.toolkt.reactive.cell.vertices.DynamicCellVertex

sealed interface BaseOperatedCell<ValueT> : Cell<ValueT> {
    val vertex: DynamicCellVertex<ValueT>
}
