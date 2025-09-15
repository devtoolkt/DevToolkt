package dev.toolkt.reactive.cell

import dev.toolkt.reactive.cell.vertices.DependencyCellVertex

sealed interface OperatedCell<ValueT> : Cell<ValueT> {
    val vertex: DependencyCellVertex<ValueT>
}
