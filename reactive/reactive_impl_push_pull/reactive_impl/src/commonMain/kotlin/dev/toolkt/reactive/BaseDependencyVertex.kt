package dev.toolkt.reactive

import dev.toolkt.core.platform.PlatformNativeSet

abstract class BaseDependencyVertex : BaseVertex() {
    private val dependents = PlatformNativeSet<DependentVertex>()

    protected fun addDependent(
        dependentVertex: DependentVertex,
    ): Boolean {
        val wasAdded = dependents.add(dependentVertex)

        if (!wasAdded) {
            throw IllegalStateException("Vertex $dependentVertex is already a dependent of $this")
        }

        return dependents.size == 1
    }

    protected fun removeDependent(
        dependentVertex: DependentVertex,
    ): Boolean {
        val wasRemoved = dependents.remove(dependentVertex)

        if (!wasRemoved) {
            throw IllegalStateException("Vertex $dependentVertex is not a dependent of $this")
        }

        return dependents.size == 0
    }

    protected fun enqueueDependentsForVisiting(
        context: Transaction.ProcessingContext,
    ) {
        dependents.forEach { dependentVertex ->
            context.enqueueForVisit(
                dependentVertex = dependentVertex,
            )
        }
    }
}
