package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.DynamicDependencyVertex

interface DependencyCellVertex<ValueT> : DynamicCellVertex<ValueT>, DynamicDependencyVertex
