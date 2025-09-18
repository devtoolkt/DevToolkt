package dev.toolkt.reactive

abstract class BaseVertex : Vertex {
    data object Tag

    private var isMarkedDirty = false

    protected fun ensureMarkedDirty(
        context: Transaction.ProcessingContext,
    ) {
        if (isMarkedDirty) {
            return
        }

        context.markDirty(
            dirtyVertex = this,
        )

        isMarkedDirty = true
    }

    final override fun reset() {
        isMarkedDirty = false

        reset(
            tag = Tag,
        )
    }

    protected abstract fun reset(
        tag: Tag,
    )
}
