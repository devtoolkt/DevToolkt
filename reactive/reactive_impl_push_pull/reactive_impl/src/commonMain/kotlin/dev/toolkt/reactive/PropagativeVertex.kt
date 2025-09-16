package dev.toolkt.reactive

abstract class PropagativeVertex<MessageT : Any> : OperativeDependencyVertex() {
    private var mutableCachedMessage: MessageT? = null

    val cachedMessage: MessageT?
        get() = mutableCachedMessage

    override fun process(
        processingContext: Transaction.ProcessingContext,
    ): Boolean {
        val message = prepare(
            processingContext = processingContext,
        )

        if (message != null) {
            cacheMessage(
                processingContext = processingContext,
                message = message,
            )

            return true
        } else {
            return false
        }
    }

    private fun cacheMessage(
        processingContext: Transaction.ProcessingContext,
        message: MessageT,
    ) {
        mutableCachedMessage = message

        ensureMarkedDirty(
            processingContext = processingContext,
        )
    }

    fun pullMessage(
        processingContext: Transaction.ProcessingContext,
    ): MessageT? {
        ensureProcessed(
            processingContext = processingContext,
        )

        return mutableCachedMessage
    }

    override fun postProcessLateOpd(
        latePostProcessingContext: Transaction.LatePostProcessingContext,
    ) {
        val cachedMessage = this.cachedMessage

        mutableCachedMessage = null

        postProcessLatePv(
            latePostProcessingContext = latePostProcessingContext,
            message = cachedMessage,
        )
    }

    /**
     * Prepare and cache the volatile state (if necessary)
     *
     * @return true if any meaningful volatile state was produced, false otherwise
     */
    protected abstract fun prepare(
        processingContext: Transaction.ProcessingContext,
    ): MessageT?

    /**
     * - Update the stable vertex-specific state by merging in the volatile state
     * - Clear the vertex-specific volatile state
     */
    protected abstract fun postProcessLatePv(
        latePostProcessingContext: Transaction.LatePostProcessingContext,
        message: MessageT?,
    )
}
