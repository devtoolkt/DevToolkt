package dev.toolkt.reactive.cell

import dev.toolkt.reactive.cell.vertices.DependencyCellVertex

data class DerivedCell<ValueT>(
    override val vertex: DependencyCellVertex<ValueT>,
): OperatedCell<ValueT>
