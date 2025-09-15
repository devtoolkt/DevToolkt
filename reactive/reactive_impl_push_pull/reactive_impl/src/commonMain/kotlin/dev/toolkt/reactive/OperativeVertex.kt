package dev.toolkt.reactive

abstract class OperativeVertex : DynamicVertex {
    private var volatileIsVisited = false

    final override fun processDynamic(
        processingContext: Transaction.ProcessingContext,
    ) {
        if (volatileIsVisited) {
            return
        }

        volatileIsVisited = true

        processOperative(
            processingContext = processingContext,
        )

        processingContext.enqueueForPostProcessing(
            vertex = this,
        )
    }

    final override fun stabilizeDynamic(
        stabilizationContext: Transaction.StabilizationContext,
    ) {
        volatileIsVisited = false

        stabilizeOperative(
            stabilizationContext = stabilizationContext,
        )
    }

    /**
     * - Prepare and cache the volatile state (if necessary)
     * - Ensure that all dependent vertices are enqueued for processing (if any meaningful volatile state was produced)
     */
    protected abstract fun processOperative(
        processingContext: Transaction.ProcessingContext,
    )

    protected abstract fun stabilizeOperative(
        stabilizationContext: Transaction.StabilizationContext,
    )
}
