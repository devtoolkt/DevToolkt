package dev.toolkt.reactive.cell

import dev.toolkt.reactive.cell.vertices.CellVertex

sealed interface OperatedCell<ValueT> : Cell<ValueT> {
    val vertex: CellVertex<ValueT>
}
