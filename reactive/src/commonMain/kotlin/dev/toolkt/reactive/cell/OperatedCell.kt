package dev.toolkt.reactive.cell

import dev.toolkt.reactive.cell.vertices.DependencyCellVertex

data class OperatedCell<ValueT>(
    override val vertex: DependencyCellVertex<ValueT>,
): BaseOperatedCell<ValueT>
