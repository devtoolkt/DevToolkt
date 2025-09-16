package dev.toolkt.reactive

interface DependencyVertex : Vertex {
    /**
     * Adds [dependentVertex] to the stable dependents. If this is the first stable dependent, activate this vertex.
     */
    fun addDependent(
        dependentVertex: DependentVertex,
    )

    /**
     * Removes [dependentVertex] from the stable dependents. If this was the last stable dependent, deactivate this vertex.
     */
    fun removeDependent(
        dependentVertex: DependentVertex,
    )
}

fun DependencyVertex.registerDependent(
    processingContext: Transaction.ProcessingContext,
    dependentVertex: DependentVertex,
) {
    processingContext.enqueueRegistrationEffect(
        registrationEffect = object : Transaction.RegistrationEffect {
            override fun register() {
                addDependent(
                    dependentVertex = dependentVertex,
                )
            }
        },
    )
}

fun DependencyVertex.unregisterDependent(
    processingContext: Transaction.ProcessingContext,
    dependentVertex: DependentVertex,
) {
    processingContext.enqueueUnregistrationEffect(
        unregistrationEffect = object : Transaction.UnregistrationEffect {
            override fun unregister() {
                removeDependent(
                    dependentVertex = dependentVertex,
                )
            }
        },
    )
}
