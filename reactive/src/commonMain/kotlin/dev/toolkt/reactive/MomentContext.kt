package dev.toolkt.reactive

class MomentContext private constructor(
    val preProcessingContext: Transaction.PreProcessingContext,
) : PureContext() {
    companion object {
        /**
         * Execute a [block] within a [MomentContext].
         *
         * This method must be called from outside the reactive system.
         *
         * @return The result of the block.
         */
        fun <ResultT> execute(
            block: context(MomentContext) () -> ResultT,
        ): ResultT = Transaction.execute { processingContext ->
            with(MomentContext(processingContext)) {
                block()
            }
        }
    }
}
