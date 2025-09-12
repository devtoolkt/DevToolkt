package dev.toolkt.reactive

import dev.toolkt.core.platform.PlatformNativeMap
import dev.toolkt.core.platform.PlatformNativeSet

abstract class DependencyVertex : Vertex {
    private enum class Request {
        /**
         * Register the dependent vertex
         */
        Register,

        /**
         * Unregister the dependent vertex
         */
        Unregister,
    }

    protected data object SpecificVertexTag

    private val stableDependents = PlatformNativeSet<DependentVertex>()

    private val volatileRequests = PlatformNativeMap<DependentVertex, Request>()

    protected fun processDependents(
        processingContext: Transaction.ProcessingContext,
    ) {
        stableDependents.forEach { vertex ->
            vertex.ensureProcessed(
                processingContext = processingContext,
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
        vertex: DependentVertex,
    ) {
        if (stableDependents.contains(vertex)) {
            throw IllegalArgumentException("DependentVertex $vertex is already a stable dependent of $this")
        }

        val previousRequest = volatileRequests.put(
            key = vertex,
            value = Request.Register,
        )

        if (previousRequest != null) {
            throw IllegalStateException("There is already a pending command ($previousRequest) for vertex $vertex")
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
        vertex: DependentVertex,
    ) {
        if (!stableDependents.contains(vertex)) {
            throw IllegalArgumentException("DependentVertex $vertex is not a stable dependent of $this")
        }

        val previousRequest = volatileRequests.put(
            key = vertex,
            value = Request.Unregister,
        )

        if (previousRequest != null) {
            throw IllegalStateException("There is already a pending command ($previousRequest) for vertex $vertex")
        }
    }

    /**
     * Adds all the registered vertices as stable dependents.
     */
    internal fun expand(
        expansionContext: Transaction.ExpansionContext,
    ) {
        volatileRequests.forEach { vertex, command ->
            if (command == Request.Register) {
                addDependent(
                    expansionContext = expansionContext,
                    vertex = vertex,
                )
            }
        }
    }

    /**
     * Removes all the unregistered vertices from the stable dependents.
     */
    internal fun shrink(
        shrinkageContext: Transaction.ShrinkageContext,
    ) {
        volatileRequests.forEach { vertex, command ->
            if (command == Request.Unregister) {
                removeDependent(
                    shrinkageContext = shrinkageContext,
                    vertex = vertex,
                )
            }
        }
    }

    /**
     * Adds [vertex] to the stable dependents. If this is the first stable dependent, activate this vertex.
     */
    final override fun addDependent(
        expansionContext: Transaction.ExpansionContext,
        vertex: DependentVertex,
    ) {
        val wasAdded = stableDependents.add(vertex)

        if (!wasAdded) {
            throw IllegalStateException("DependentVertex $vertex is already a dependent of $this")
        }

        if (stableDependents.size == 1) {
            activate(
                expansionContext = expansionContext,
            )
        }
    }

    /**
     * Removes [vertex] from the stable dependents. If this was the last stable dependent, deactivate this vertex.
     */
    final override fun removeDependent(
        shrinkageContext: Transaction.ShrinkageContext,
        vertex: DependentVertex,
    ) {
        val wasRemoved = stableDependents.remove(vertex)

        if (!wasRemoved) {
            throw IllegalStateException("DependentVertex $vertex is not a dependent of $this")
        }

        if (stableDependents.size == 0) {
            deactivate(
                shrinkageContext = shrinkageContext,
            )
        }
    }

    /**
     * - Update the stable state by merging in the volatile state
     * - Clear the volatile state or replace it with the follow-up volatile state
     */
    fun stabilize(
        stabilizationContext: Transaction.StabilizationContext,
    ) {
        volatileRequests.clear()

        stabilize(
            stabilizationContext = stabilizationContext,
            tag = SpecificVertexTag,
        )
    }

    /**
     * Add this vertex as a dependent to the upstream vertices
     */
    protected abstract fun activate(
        expansionContext: Transaction.ExpansionContext,
    )

    /**
     * Remove this vertex as a dependent from the upstream vertices
     */
    protected abstract fun deactivate(
        shrinkageContext: Transaction.ShrinkageContext,
    )

    /**
     * - Update the stable vertex-specific state by merging in the volatile state
     * - Clear the vertex-specific volatile state
     */
    protected abstract fun stabilize(
        stabilizationContext: Transaction.StabilizationContext,
        tag: SpecificVertexTag,
    )
}
