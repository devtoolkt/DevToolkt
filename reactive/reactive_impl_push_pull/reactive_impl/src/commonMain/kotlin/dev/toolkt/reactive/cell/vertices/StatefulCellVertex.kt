package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction

abstract class StatefulCellVertex<ValueT> : PropagativeCellVertex<ValueT>() {
    final override fun onFirstDependentAdded(
        expansionContext: Transaction.ExpansionContext,
    ) {
    }

    final override fun onLastDependentRemoved(
        shrinkageContext: Transaction.ShrinkageContext,
    ) {
    }
}
