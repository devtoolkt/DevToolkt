package dev.toolkt.reactive

abstract class BaseVertex : ResettableVertex {
    private var mutableIsMarkedDirty = false

    private val isMarkedDirty: Boolean
        get() = mutableIsMarkedDirty

    protected fun ensureMarkedDirty(
        processingContext: Transaction.ProcessingContext,
    ) {
        if (isMarkedDirty) {
            return
        }

        mutableIsMarkedDirty = true

        processingContext.enqueueDirtyVertex(
            dirtyVertex = this,
        )
    }

    final override fun reset() {
        mutableIsMarkedDirty = false

        clean()
    }

    protected abstract fun clean()
}
