package dev.toolkt.reactive

abstract class IntermediateDynamicVertex<NotificationT : Any> : BaseDependencyVertex(), DependentVertex {
    private var mutableIsProcessed = false

    private val isProcessed: Boolean
        get() = mutableIsProcessed

    private var mutableCurrentNotification: NotificationT? = null

    private val currentNotification: NotificationT?
        get() = mutableCurrentNotification

    final override fun visit(
        processingContext: Transaction.ProcessingContext,
    ) {
        // Thought: Possibly use "immediate visitation mode"
        ensureProcessed(
            processingContext = processingContext,
        )
    }

    // Thought: Possibly use "queuing visitation mode"
    protected fun pullNotification(
        processingContext: Transaction.ProcessingContext,
    ): NotificationT? = ensureProcessed(
        processingContext = processingContext,
    )

    protected fun ensureProcessed(
        processingContext: Transaction.ProcessingContext,
    ): NotificationT? {
        if (isProcessed) {
            return currentNotification
        }

        val computedCurrentNotification = process(
            processingContext = processingContext,
        )

        mutableIsProcessed = true
        mutableCurrentNotification = computedCurrentNotification

        // TODO: Split Intermediate[Dynamic]CellVertex / IntermediateEventStreamVertex
        ensureMarkedDirty(
            processingContext = processingContext,
        )

        if (computedCurrentNotification != null) {
            enqueueDependentsForVisiting(
                processingContext = processingContext,
            )
        }

        return computedCurrentNotification
    }

    final override fun clean() {
        val currentNotification = this.currentNotification

        mutableIsProcessed = false
        mutableCurrentNotification = null

        if (currentNotification != null) {
            update(
                currentNotification = currentNotification,
            )
        }
    }

    protected abstract fun process(
        processingContext: Transaction.ProcessingContext,
    ): NotificationT?

    protected abstract fun update(
        currentNotification: NotificationT,
    )
}
