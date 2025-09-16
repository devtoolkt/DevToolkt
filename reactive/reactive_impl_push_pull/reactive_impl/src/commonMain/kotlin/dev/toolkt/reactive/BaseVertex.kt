package dev.toolkt.reactive

abstract class BaseVertex : ResettableVertex {
    private var mutableIsMarkedDirty = false

    private val isMarkedDirty: Boolean
        get() = mutableIsMarkedDirty

    protected fun ensureMarkedDirty(
        context: Transaction.Context,
    ) {
        if (isMarkedDirty) {
            return
        }

        mutableIsMarkedDirty = true

        context.enqueueDirtyVertex(
            dirtyVertex = this,
        )
    }

    final override fun reset() {
        mutableIsMarkedDirty = false

        clean()
    }

    protected abstract fun clean()
}
