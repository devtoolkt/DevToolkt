package dev.toolkt.reactive

abstract class OperativeVertex : DynamicVertex {
    private var volatileIsVisited = false

    final override fun process(
        processingContext: Transaction.ProcessingContext,
    ) {
        if (volatileIsVisited) {
            return
        }

        volatileIsVisited = true

        operate(
            processingContext = processingContext,
        )

        processingContext.enqueueForPostProcessing(
            processedVertex = this,
        )
    }

    /**
     * - Prepare and cache the volatile state (if necessary)
     * - Ensure that all dependent vertices are enqueued for processing (if any meaningful volatile state was produced)
     */
    protected abstract fun operate(
        processingContext: Transaction.ProcessingContext,
    )
}
