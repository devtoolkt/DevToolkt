package dev.toolkt.reactive

abstract class OperativeVertex : DynamicVertex {
    private var volatileIsVisited = false

    final override fun process(
        processingContext: Transaction.ProcessingContext,
    ) {
        ensureVisited(
            processingContext = processingContext,
        )
    }

    final override fun postProcessEarly(
        earlyPostProcessingContext: Transaction.EarlyPostProcessingContext,
    ) {
        if (!volatileIsVisited) {
            throw IllegalStateException("Vertex must be pre-processed before inter-processing")
        }

        affect(
            earlyPostProcessingContext = earlyPostProcessingContext,
        )
    }

    final override fun postProcessLate(
        latePostProcessingContext: Transaction.LatePostProcessingContext,
    ) {
        volatileIsVisited = false

        settle(
            latePostProcessingContext = latePostProcessingContext,
        )
    }

    protected fun ensureVisited(
        processingContext: Transaction.ProcessingContext,
    ) {
        if (volatileIsVisited) {
            return
        }

        volatileIsVisited = true

        visit(
            processingContext = processingContext,
        )

        processingContext.enqueueForPostProcessing(
            vertex = this,
        )
    }

    /**
     * - Prepare and cache the volatile state (if necessary)
     * - Ensure that all dependent vertices are enqueued for processing (if any meaningful volatile state was produced)
     */
    protected abstract fun visit(
        processingContext: Transaction.ProcessingContext,
    )

    protected abstract fun affect(
        earlyPostProcessingContext: Transaction.EarlyPostProcessingContext,
    )

    protected abstract fun settle(
        latePostProcessingContext: Transaction.LatePostProcessingContext,
    )
}
