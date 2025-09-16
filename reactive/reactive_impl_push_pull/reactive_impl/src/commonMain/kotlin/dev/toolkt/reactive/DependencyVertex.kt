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
    context: Transaction.Context,
    dependentVertex: DependentVertex,
) {
    context.enqueueRegistrationEffect(
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
    context: Transaction.Context,
    dependentVertex: DependentVertex,
) {
    context.enqueueUnregistrationEffect(
        unregistrationEffect = object : Transaction.UnregistrationEffect {
            override fun unregister() {
                removeDependent(
                    dependentVertex = dependentVertex,
                )
            }
        },
    )
}
