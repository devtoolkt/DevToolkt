package dev.toolkt.reactive

abstract class OperativeVertex : DynamicVertex {
    private var volatileIsVisited = false

    final override fun preProcess(
        preProcessingContext: Transaction.PreProcessingContext,
    ) {
        if (volatileIsVisited) {
            return
        }

        volatileIsVisited = true

        prepare(
            preProcessingContext = preProcessingContext,
        )

        preProcessingContext.enqueueForPostProcessing(
            vertex = this,
        )
    }

    final override fun interProcess(
        interProcessingContext: Transaction.InterProcessingContext,
    ) {
        affect(
            interProcessingContext = interProcessingContext,
        )
    }

    final override fun postProcess(
        postProcessingContext: Transaction.PostProcessingContext,
    ) {
        volatileIsVisited = false

        settle(
            stabilizationContext = postProcessingContext,
        )
    }

    /**
     * - Prepare and cache the volatile state (if necessary)
     * - Ensure that all dependent vertices are enqueued for processing (if any meaningful volatile state was produced)
     */
    protected abstract fun prepare(
        preProcessingContext: Transaction.PreProcessingContext,
    )

    protected abstract fun affect(
        interProcessingContext: Transaction.InterProcessingContext,
    )

    protected abstract fun settle(
        stabilizationContext: Transaction.StabilizationContext,
    )
}
