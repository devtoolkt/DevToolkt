package dev.toolkt.reactive

interface DependentVertex : Vertex {
    fun visit(
        context: Transaction.ProcessingContext,
    )
}
