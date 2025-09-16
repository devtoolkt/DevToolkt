package dev.toolkt.reactive

interface DependencyVertex : Vertex {
    /**
     * Registers a dependent [vertex] to this vertex.
     *
     * The internal set of stable dependents is not immediately updated. The request to add [vertex] as a dependent is
     * stored and executed later.
     *
     * A vertex cannot be registered as a dependent if it's already a stable dependent of this vertex. If the vertex
     * is registered as a dependent in a given transaction, it can't be unregistered in the same transaction.
     */
    fun registerDependent(
        @Suppress("unused") processingContext: Transaction.ProcessingContext,
        vertex: DynamicVertex,
    )

    /**
     * Unregisters a dependent [vertex] from this vertex.
     *
     * The internal set of stable dependents is not immediately updated. The request to remove [vertex] as a dependent
     * is stored and executed later.
     *
     * A vertex can only be unregistered as a dependent if it's already a stable dependent of this vertex. If the vertex
     * is unregistered as a dependent in a given transaction, it can't be re-registered in the same transaction.
     */
    fun unregisterDependent(
        @Suppress("unused") processingContext: Transaction.ProcessingContext,
        vertex: DynamicVertex,
    )

    /**
     * Adds [vertex] to the stable dependents. If this is the first stable dependent, activate this vertex.
     */
    fun addDependent(
        expansionContext: Transaction.ExpansionContext,
        vertex: DynamicVertex,
    )

    /**
     * Removes [vertex] from the stable dependents. If this was the last stable dependent, deactivate this vertex.
     */
    fun removeDependent(
        shrinkageContext: Transaction.ShrinkageContext,
        vertex: DynamicVertex,
    )
}
