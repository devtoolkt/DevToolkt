package dev.toolkt.reactive

abstract class OperativeVertex : DynamicVertex {
    @VolatileProcessingState
    private var mutableIsMarkedDirty = false

    protected val isMarkedDirty: Boolean
        get() = mutableIsMarkedDirty

    final override fun postProcessEarly(
        earlyPostProcessingContext: Transaction.EarlyPostProcessingContext,
    ) {
        if (!isMarkedDirty) {
            throw IllegalStateException("Vertex must be pre-processed before inter-processing")
        }

        postProcessEarlyOp(
            earlyPostProcessingContext = earlyPostProcessingContext,
        )
    }

    final override fun postProcessLate(
        latePostProcessingContext: Transaction.LatePostProcessingContext,
    ) {
        mutableIsMarkedDirty = false

        postProcessLateOp(
            latePostProcessingContext = latePostProcessingContext,
        )
    }

    protected fun ensureMarkedDirty(
        processingContext: Transaction.ProcessingContext,
    ) {
        if (isMarkedDirty) {
            return
        }

        mutableIsMarkedDirty = true

        processingContext.enqueueForPostProcessing(
            processedVertex = this,
        )
    }

    protected abstract fun postProcessEarlyOp(
        earlyPostProcessingContext: Transaction.EarlyPostProcessingContext,
    )

    protected abstract fun postProcessLateOp(
        latePostProcessingContext: Transaction.LatePostProcessingContext,
    )
}
