package dev.toolkt.reactive

interface DependentVertex : Vertex {
    fun visit(
        processingContext: Transaction.ProcessingContext,
    )
}
