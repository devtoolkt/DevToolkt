package dev.toolkt.reactive.cell.vertices

abstract class StatefulCellVertex<ValueT> : IntermediateCellVertex<ValueT>() {
    final override fun onFirstDependentAdded() {
    }

    final override fun onLastDependentRemoved() {
    }
}
