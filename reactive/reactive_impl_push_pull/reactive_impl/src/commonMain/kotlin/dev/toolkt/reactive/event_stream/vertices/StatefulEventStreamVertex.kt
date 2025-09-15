package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.Transaction

abstract class StatefulEventStreamVertex<ValueT> : PropagativeEventStreamVertex<ValueT>() {
    final override fun activate(
        expansionContext: Transaction.ExpansionContext,
    ) {
    }

    final override fun deactivate(
        shrinkageContext: Transaction.ShrinkageContext,
    ) {
    }
}
