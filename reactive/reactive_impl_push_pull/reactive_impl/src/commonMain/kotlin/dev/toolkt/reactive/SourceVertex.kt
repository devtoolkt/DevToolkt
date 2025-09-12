package dev.toolkt.reactive

abstract class SourceVertex : DependencyVertex() {
    /**
     * Process the source vertex, assuming that its source information that will be propagated is stored as volatile
     * state.
     */
    internal fun process(
        processingContext: Transaction.ProcessingContext,
    ) {
        processDependents(
            processingContext = processingContext,
        )
    }
}
