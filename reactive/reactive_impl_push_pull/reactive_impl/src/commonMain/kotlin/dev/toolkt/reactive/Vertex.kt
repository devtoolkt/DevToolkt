package dev.toolkt.reactive

import dev.toolkt.core.platform.PlatformNativeMap
import dev.toolkt.core.platform.PlatformNativeSet

abstract class Vertex {
    private enum class DependentCommand {
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

    private val stableDependents = PlatformNativeSet<Vertex>()

    private var volatileIsProcessed: Boolean = false

    private val volatileDependentCommands = PlatformNativeMap<Vertex, DependentCommand>()

    internal fun ensureProcessed(
        processingContext: Transaction.ProcessingContext,
    ) {
        if (volatileIsProcessed) {
            return
        }

        process(
            processingContext = processingContext,
        )

        processingContext.enqueueForPostProcessing(
            processedVertex = this,
        )

        volatileIsProcessed = true
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
    internal fun registerDependent(
        @Suppress("unused") processingContext: Transaction.ProcessingContext,
        vertex: Vertex,
    ) {
        if (stableDependents.contains(vertex)) {
            throw IllegalArgumentException("Vertex $vertex is already a stable dependent of $this")
        }

        val previousCommand = volatileDependentCommands.put(
            key = vertex,
            value = DependentCommand.Register,
        )

        if (previousCommand != null) {
            throw IllegalStateException("There is already a pending command ($previousCommand) for vertex $vertex")
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
    internal fun unregisterDependent(
        @Suppress("unused") processingContext: Transaction.ProcessingContext,
        vertex: Vertex,
    ) {
        if (!stableDependents.contains(vertex)) {
            throw IllegalArgumentException("Vertex $vertex is not a stable dependent of $this")
        }

        val previousCommand = volatileDependentCommands.put(
            key = vertex,
            value = DependentCommand.Unregister,
        )

        if (previousCommand != null) {
            throw IllegalStateException("There is already a pending command ($previousCommand) for vertex $vertex")
        }
    }

    /**
     * Adds all the registered vertices as stable dependents.
     */
    internal fun expand(
        expansionContext: Transaction.ExpansionContext,
    ) {
        volatileDependentCommands.forEach { vertex, command ->
            if (command == DependentCommand.Register) {
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
        volatileDependentCommands.forEach { vertex, command ->
            if (command == DependentCommand.Unregister) {
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
    internal fun addDependent(
        expansionContext: Transaction.ExpansionContext,
        vertex: Vertex,
    ) {
        val wasAdded = stableDependents.add(vertex)

        if (!wasAdded) {
            throw IllegalStateException("Vertex $vertex is already a dependent of $this")
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
    internal fun removeDependent(
        shrinkageContext: Transaction.ShrinkageContext,
        vertex: Vertex,
    ) {
        val wasRemoved = stableDependents.remove(vertex)

        if (!wasRemoved) {
            throw IllegalStateException("Vertex $vertex is not a dependent of $this")
        }

        if (stableDependents.size == 0) {
            deactivate(
                shrinkageContext = shrinkageContext,
            )
        }
    }

    /**
     * - Update the stable state by merging in the volatile state
     * - Clear the volatile state
     */
    fun stabilize(
        resettingContext: Transaction.ResettingContext,
    ) {
        volatileIsProcessed = false

        volatileDependentCommands.clear()

        stabilize(
            resettingContext = resettingContext,
            tag = SpecificVertexTag,
        )
    }

    /**
     * - Compute and cache the volatile state, potentially triggering processing of the upstream vertices
     * - Unregister / register this vertex as a dependent of the upstream vertices
     */
    protected abstract fun process(
        processingContext: Transaction.ProcessingContext,
    )

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
     * Invoke external side-effectful operations
     */
    internal abstract fun invokeEffects(
        mutationContext: Transaction.MutationContext,
    )

    /**
     * - Update the stable vertex-specific state by merging in the volatile state
     * - Clear the vertex-specific volatile state
     */
    protected abstract fun stabilize(
        resettingContext: Transaction.ResettingContext,
        tag: SpecificVertexTag,
    )
}
