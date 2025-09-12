package dev.toolkt.reactive

abstract class DependentVertex : DependencyVertex() {
    protected data object SpecializedVertexTag

    private var volatileIsProcessed: Boolean = false

    internal fun ensureProcessed(
        processingContext: Transaction.ProcessingContext,
    ) {
        if (volatileIsProcessed) {
            return
        }

        process(
            processingContext = processingContext,
        )

        processDependents(
            processingContext = processingContext,
        )

        processingContext.enqueueForPostProcessing(
            processedVertex = this,
        )

        volatileIsProcessed = true
    }

    /**
     * - Update the stable state by merging in the volatile state
     * - Clear the volatile state
     */
    override fun stabilize(
        stabilizationContext: Transaction.StabilizationContext,
        tag: SpecificVertexTag,
    ) {
        volatileIsProcessed = false

        stabilize(
            stabilizationContext = stabilizationContext,
            tag = SpecializedVertexTag,
        )
    }

    /**
     * - Compute the volatile state, potentially triggering processing of the upstream vertices
     *   - Optionally cache the volatile state
     * - Unregister / register this vertex as a dependent of the upstream vertices
     */
    protected abstract fun process(
        processingContext: Transaction.ProcessingContext,
    )

    /**
     * Invoke external side-effectful operations
     */
    internal abstract fun invokeEffects(
        mutationContext: Transaction.MutationContext,
    )

    /**
     * - Update the stable specialized vertex-specific state by merging in the volatile state
     * - Clear the specialized vertex-specific volatile state
     */
    protected abstract fun stabilize(
        stabilizationContext: Transaction.StabilizationContext,
        tag: SpecializedVertexTag,
    )
}
