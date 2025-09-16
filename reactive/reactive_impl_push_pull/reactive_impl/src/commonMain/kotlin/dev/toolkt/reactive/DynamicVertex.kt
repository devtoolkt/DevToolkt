package dev.toolkt.reactive

interface DynamicVertex : Vertex {
    /**
     * Processes this vertex.
     *
     * The implementation should either be prepared to be called multiple times in the same transaction or ignore all
     * calls after the first one.
     */
    fun process(
        processingContext: Transaction.ProcessingContext,
    )

    fun postProcessEarly(
        earlyPostProcessingContext: Transaction.EarlyPostProcessingContext,
    )

    fun postProcess(
        postProcessingContext: Transaction.PostProcessingContext,
    )
}
