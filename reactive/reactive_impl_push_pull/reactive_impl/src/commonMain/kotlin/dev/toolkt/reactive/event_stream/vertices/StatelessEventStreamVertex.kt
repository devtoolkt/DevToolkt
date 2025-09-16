package dev.toolkt.reactive.event_stream.vertices

import dev.toolkt.reactive.Transaction

abstract class StatelessEventStreamVertex<ValueT> : PropagativeEventStreamVertex<ValueT>() {
    override fun onFirstDependentAdded(
        expansionContext: Transaction.ExpansionContext,
    ) {
        resume(
            expansionContext = expansionContext,
        )
    }

    override fun onLastDependentRemoved(
        shrinkageContext: Transaction.ShrinkageContext,
    ) {
        pause(
            shrinkageContext = shrinkageContext,
        )
    }

    final override fun postProcessLatePv(
        latePostProcessingContext: Transaction.LatePostProcessingContext,
        message: EventStreamVertex.Occurrence<ValueT>?,
    ) {
    }

    protected abstract fun resume(
        expansionContext: Transaction.ExpansionContext,
    )

    protected abstract fun pause(
        shrinkageContext: Transaction.ShrinkageContext,
    )
}
