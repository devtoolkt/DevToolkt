package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.Transaction

abstract class StatefulEventStreamVertex<ValueT> : PropagativeEventStreamVertex<ValueT>() {
    final override fun onFirstDependentAdded(
        expansionContext: Transaction.ExpansionContext,
    ) {
    }

    final override fun onLastDependentRemoved(
        shrinkageContext: Transaction.ShrinkageContext,
    ) {
    }
}
