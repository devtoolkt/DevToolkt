package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.DependencyVertex

interface DependencyCellVertex<ValueT> : DynamicCellVertex<ValueT>, DependencyVertex
