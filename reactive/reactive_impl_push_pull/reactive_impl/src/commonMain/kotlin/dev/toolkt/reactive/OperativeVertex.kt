package dev.toolkt.reactive

abstract class OperativeVertex : DynamicVertex {
    @VolatileProcessingState
    private var mutableIsEffectivelyProcessed = false

    protected val isEffectivelyProcessed: Boolean
        get() = mutableIsEffectivelyProcessed

    final override fun visit(
        processingContext: Transaction.ProcessingContext,
    ) {
        ensureEffectivelyProcessed(
            processingContext = processingContext,
        )
    }

    final override fun postProcessEarly(
        earlyPostProcessingContext: Transaction.EarlyPostProcessingContext,
    ) {
        if (!isEffectivelyProcessed) {
            throw IllegalStateException("Vertex must be pre-processed before inter-processing")
        }

        affect(
            earlyPostProcessingContext = earlyPostProcessingContext,
        )
    }

    final override fun postProcessLate(
        latePostProcessingContext: Transaction.LatePostProcessingContext,
    ) {
        mutableIsEffectivelyProcessed = false

        settle(
            latePostProcessingContext = latePostProcessingContext,
        )
    }

    protected fun ensureEffectivelyProcessed(
        processingContext: Transaction.ProcessingContext,
    ) {
        if (isEffectivelyProcessed) {
            return
        }

        mutableIsEffectivelyProcessed = true

        processEffectively(
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
    protected abstract fun processEffectively(
        processingContext: Transaction.ProcessingContext,
    )

    protected abstract fun affect(
        earlyPostProcessingContext: Transaction.EarlyPostProcessingContext,
    )

    protected abstract fun settle(
        latePostProcessingContext: Transaction.LatePostProcessingContext,
    )
}
