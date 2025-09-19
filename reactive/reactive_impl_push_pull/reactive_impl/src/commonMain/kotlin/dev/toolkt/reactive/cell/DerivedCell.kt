package dev.toolkt.reactive.cell

import dev.toolkt.reactive.cell.vertices.CellVertex

data class DerivedCell<ValueT>(
    override val vertex: CellVertex<ValueT>,
): OperatedCell<ValueT>
