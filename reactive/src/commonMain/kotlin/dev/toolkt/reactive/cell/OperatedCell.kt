package dev.toolkt.reactive.cell

import dev.toolkt.reactive.cell.vertices.DynamicCellVertex

data class OperatedCell<ValueT>(
    override val vertex: DynamicCellVertex<ValueT>,
): BaseOperatedCell<ValueT>
