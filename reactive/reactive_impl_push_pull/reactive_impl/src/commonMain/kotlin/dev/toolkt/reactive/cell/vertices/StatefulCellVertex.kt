package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction

abstract class StatefulCellVertex<ValueT> : PropagativeCellVertex<ValueT>() {
    final override fun activate(
        expansionContext: Transaction.ExpansionContext,
    ) {
    }

    final override fun deactivate(
        shrinkageContext: Transaction.ShrinkageContext,
    ) {
    }
}
