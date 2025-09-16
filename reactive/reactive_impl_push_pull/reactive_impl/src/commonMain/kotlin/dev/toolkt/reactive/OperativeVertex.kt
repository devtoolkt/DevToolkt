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

    final override fun interProcess(
        interProcessingContext: Transaction.InterProcessingContext,
    ) {
        if (!volatileIsVisited) {
            throw IllegalStateException("Vertex must be pre-processed before inter-processing")
        }

        affect(
            interProcessingContext = interProcessingContext,
        )
    }

    final override fun postProcess(
        postProcessingContext: Transaction.PostProcessingContext,
    ) {
        volatileIsVisited = false

        settle(
            postProcessingContext = postProcessingContext,
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
        interProcessingContext: Transaction.InterProcessingContext,
    )

    protected abstract fun settle(
        postProcessingContext: Transaction.PostProcessingContext,
    )
}
