package dev.toolkt.reactive

import dev.toolkt.core.platform.PlatformNativeMap
import dev.toolkt.core.platform.PlatformNativeSet

abstract class PropagativeVertex<MessageT : Any> : OperativeVertex(), DependencyVertex {
    private enum class RegistrationRequest {
        /**
         * RegistrationRequest to register the dependent vertex
         */
        Register,

        /**
         * RegistrationRequest to unregister the dependent vertex
         */
        Unregister,
    }

    private val dependents = PlatformNativeSet<DynamicVertex>()

    private val registrationRequests = PlatformNativeMap<DynamicVertex, RegistrationRequest>()

    private var cachedMessage: MessageT? = null

    override fun processEffectively(
        processingContext: Transaction.ProcessingContext,
    ) {
        val message = prepare(
            processingContext = processingContext,
        )

        if (message != null) {
            cachedMessage = message

            propagate(
                processingContext = processingContext,
            )
        }
    }

    fun pullMessage(
        processingContext: Transaction.ProcessingContext,
    ): MessageT? {
        ensureEffectivelyProcessed(
            processingContext = processingContext,
        )

        return cachedMessage
    }

    private fun propagate(
        processingContext: Transaction.ProcessingContext,
    ) {
        dependents.forEach { dependentVertex ->
            processingContext.enqueueForVisiting(
                dependentVertex = dependentVertex,
            )
        }
    }

    /**
     * Registers a dependent [vertex] to this vertex.
     *
     * The internal set of stable dependents is not immediately updated. The request to add [vertex] as a dependent is
     * stored and executed later.
     *
     * A vertex cannot be registered as a dependent if it's already a stable dependent of this vertex. If the vertex
     * is registered as a dependent in a given transaction, it can't be unregistered in the same transaction.
     */
    final override fun registerDependent(
        @Suppress("unused") processingContext: Transaction.ProcessingContext,
        vertex: DynamicVertex,
    ) {
        if (dependents.contains(vertex)) {
            throw IllegalArgumentException("Vertex $vertex is already a stable dependent of $this")
        }

        val previousRegistrationRequest = registrationRequests.put(
            key = vertex,
            value = RegistrationRequest.Register,
        )

        if (previousRegistrationRequest != null) {
            throw IllegalStateException("There is already a pending command ($previousRegistrationRequest) for vertex $vertex")
        }
    }

    /**
     * Unregisters a dependent [vertex] from this vertex.
     *
     * The internal set of stable dependents is not immediately updated. The request to remove [vertex] as a dependent
     * is stored and executed later.
     *
     * A vertex can only be unregistered as a dependent if it's already a stable dependent of this vertex. If the vertex
     * is unregistered as a dependent in a given transaction, it can't be re-registered in the same transaction.
     */
    final override fun unregisterDependent(
        @Suppress("unused") processingContext: Transaction.ProcessingContext,
        vertex: DynamicVertex,
    ) {
        if (!dependents.contains(vertex)) {
            throw IllegalArgumentException("Vertex $vertex is not a stable dependent of $this")
        }

        val previousRegistrationRequest = registrationRequests.put(
            key = vertex,
            value = RegistrationRequest.Unregister,
        )

        if (previousRegistrationRequest != null) {
            throw IllegalStateException("There is already a pending command ($previousRegistrationRequest) for vertex $vertex")
        }
    }

    /**
     * Adds [vertex] to the stable dependents. If this is the first stable dependent, activate this vertex.
     */
    final override fun addDependent(
        expansionContext: Transaction.ExpansionContext,
        vertex: DynamicVertex,
    ) {
        val wasAdded = dependents.add(vertex)

        if (!wasAdded) {
            throw IllegalStateException("Vertex $vertex is already a dependent of $this")
        }

        if (dependents.size == 1) {
            onFirstDependentAdded(
                expansionContext = expansionContext,
            )
        }
    }

    /**
     * Removes [vertex] from the stable dependents. If this was the last stable dependent, deactivate this vertex.
     */
    final override fun removeDependent(
        shrinkageContext: Transaction.ShrinkageContext,
        vertex: DynamicVertex,
    ) {
        val wasRemoved = dependents.remove(vertex)

        if (!wasRemoved) {
            throw IllegalStateException("Vertex $vertex is not a dependent of $this")
        }

        if (dependents.size == 0) {
            onLastDependentRemoved(
                shrinkageContext = shrinkageContext,
            )
        }
    }

    final override fun affect(
        earlyPostProcessingContext: Transaction.EarlyPostProcessingContext,
    ) {
        registrationRequests.forEach { vertex, request ->
            if (request == RegistrationRequest.Register) {
                addDependent(
                    expansionContext = earlyPostProcessingContext,
                    vertex = vertex,
                )
            }
        }
    }

    /**
     * - Update the stable state by merging in the volatile state
     * - Clear the volatile state or replace it with the follow-up volatile state
     */
    final override fun settle(
        latePostProcessingContext: Transaction.LatePostProcessingContext,
    ) {
        stabilize(
            latePostProcessingContext = latePostProcessingContext,
            message = cachedMessage,
        )

        registrationRequests.forEach { vertex, request ->
            if (request == RegistrationRequest.Unregister) {
                removeDependent(
                    shrinkageContext = latePostProcessingContext,
                    vertex = vertex,
                )
            }
        }

        registrationRequests.clear()

        cachedMessage = null
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
     * Add this vertex as a dependent to the upstream vertices
     */
    protected abstract fun onFirstDependentAdded(
        expansionContext: Transaction.ExpansionContext,
    )

    /**
     * Remove this vertex as a dependent from the upstream vertices
     */
    protected abstract fun onLastDependentRemoved(
        shrinkageContext: Transaction.ShrinkageContext,
    )

    /**
     * - Update the stable vertex-specific state by merging in the volatile state
     * - Clear the vertex-specific volatile state
     */
    protected abstract fun stabilize(
        latePostProcessingContext: Transaction.LatePostProcessingContext,
        message: MessageT?,
    )
}
