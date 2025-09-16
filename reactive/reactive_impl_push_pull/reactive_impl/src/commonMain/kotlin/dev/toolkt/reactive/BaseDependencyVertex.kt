package dev.toolkt.reactive

import dev.toolkt.core.platform.PlatformNativeMap
import dev.toolkt.core.platform.PlatformNativeSet

abstract class BaseDependencyVertex : DependencyVertex {
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

    protected fun processRegisterRequests(
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

    protected fun processUnregisterRequests(
        latePostProcessingContext: Transaction.LatePostProcessingContext,
    ) {
        registrationRequests.forEach { vertex, request ->
            if (request == RegistrationRequest.Unregister) {
                removeDependent(
                    shrinkageContext = latePostProcessingContext,
                    vertex = vertex,
                )
            }
        }
    }

    protected fun clearRegistrationRequests() {
        registrationRequests.clear()
    }

    protected abstract fun onFirstDependentAdded(
        expansionContext: Transaction.ExpansionContext,
    )

    protected abstract fun onLastDependentRemoved(
        shrinkageContext: Transaction.ShrinkageContext,
    )
}
