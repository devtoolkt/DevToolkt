package dev.toolkt.reactive

import dev.toolkt.core.platform.PlatformNativeSet

abstract class BaseDependencyVertex : BaseVertex(), DependencyVertex {
    private val dependents = PlatformNativeSet<DependentVertex>()

    /**
     * Adds [dependentVertex] to the stable dependents. If this is the first stable dependent, activate this vertex.
     */
    final override fun addDependent(
        dependentVertex: DependentVertex,
    ) {
        val wasAdded = dependents.add(dependentVertex)

        if (!wasAdded) {
            throw IllegalStateException("Vertex $dependentVertex is already a dependent of $this")
        }

        if (dependents.size == 1) {
            onFirstDependentAdded()
        }
    }

    /**
     * Removes [dependentVertex] from the stable dependents. If this was the last stable dependent, deactivate this vertex.
     */
    final override fun removeDependent(
        dependentVertex: DependentVertex,
    ) {
        val wasRemoved = dependents.remove(dependentVertex)

        if (!wasRemoved) {
            throw IllegalStateException("Vertex $dependentVertex is not a dependent of $this")
        }

        if (dependents.size == 0) {
            onLastDependentRemoved()
        }
    }

    protected fun enqueueDependentsForVisiting(
        processingContext: Transaction.ProcessingContext,
    ) {
        dependents.forEach { dependentVertex ->
            processingContext.enqueueDependentVertex(
                dependentVertex = dependentVertex,
            )
        }
    }

    protected abstract fun onFirstDependentAdded()

    protected abstract fun onLastDependentRemoved()
}
