package dev.toolkt.reactive

// Thought: -> "DependentVertex" (again?)
interface DynamicVertex : Vertex {
    fun visit(
        processingContext: Transaction.ProcessingContext,
    )

    fun postProcessEarly(
        earlyPostProcessingContext: Transaction.EarlyPostProcessingContext,
    )

    fun postProcessLate(
        latePostProcessingContext: Transaction.LatePostProcessingContext,
    )
}
