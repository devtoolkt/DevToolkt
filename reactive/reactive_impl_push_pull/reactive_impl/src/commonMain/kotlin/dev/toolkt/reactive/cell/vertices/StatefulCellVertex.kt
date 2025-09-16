package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.IntermediateDynamicCellVertex

abstract class StatefulCellVertex<ValueT> : IntermediateDynamicCellVertex<ValueT>() {
    override val isStableValueCached: Boolean
        get() = false

    final override fun clearStableValueCache() {
    }

    final override fun onFirstDependentAdded() {
    }

    final override fun onLastDependentRemoved() {
    }
}
