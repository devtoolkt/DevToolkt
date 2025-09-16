package dev.toolkt.reactive

class Transaction private constructor() {
    interface SideEffect {
        fun execute()
    }

    interface RegistrationEffect {
        fun register()
    }

    interface UnregistrationEffect {
        fun unregister()
    }

    abstract class Context {
        abstract fun enqueueDependentVertex(
            dependentVertex: DependentVertex,
        )

        abstract fun enqueueRegistrationEffect(
            registrationEffect: RegistrationEffect,
        )

        abstract fun enqueueUnregistrationEffect(
            unregistrationEffect: UnregistrationEffect,
        )

        abstract fun enqueueSideEffect(
            sideEffect: SideEffect,
        )

        abstract fun enqueueDirtyVertex(
            dirtyVertex: ResettableVertex,
        )
    }

    companion object {
        fun <ResultT> execute(
            block: (Context) -> ResultT,
        ): ResultT = with(Transaction()) {
            // Dependent vertices to be visited
            val dependentVertexQueue = ArrayDeque<DependentVertex>()

            // Registration effects to be applied
            val registrationEffectQueue = mutableListOf<RegistrationEffect>()

            // Unregistration effects to be applied
            val unregistrationEffectQueue = mutableListOf<UnregistrationEffect>()

            // Side effects to be executed
            val sideEffectQueue = mutableListOf<SideEffect>()

            // Dirty vertices to be reset
            val dirtyVertexQueue = mutableListOf<ResettableVertex>()

            val context = object : Context() {
                override fun enqueueDependentVertex(
                    dependentVertex: DependentVertex,
                ) {
                    dependentVertexQueue.addLast(dependentVertex)
                }

                override fun enqueueDirtyVertex(
                    dirtyVertex: ResettableVertex,
                ) {
                    dirtyVertexQueue.add(dirtyVertex)
                }

                override fun enqueueRegistrationEffect(
                    registrationEffect: RegistrationEffect,
                ) {
                    registrationEffectQueue.add(registrationEffect)
                }

                override fun enqueueUnregistrationEffect(
                    unregistrationEffect: UnregistrationEffect,
                ) {
                    unregistrationEffectQueue.add(unregistrationEffect)
                }

                override fun enqueueSideEffect(
                    sideEffect: SideEffect,
                ) {
                    sideEffectQueue.add(sideEffect)
                }
            }

            val result = block(context)

            while (dependentVertexQueue.isNotEmpty()) {
                val vertexToProcess = dependentVertexQueue.removeFirst()

                vertexToProcess.visit(
                    context = context,
                )
            }

            registrationEffectQueue.forEach { registrationEffect ->
                registrationEffect.register()
            }

            unregistrationEffectQueue.forEach { unregistrationEffect ->
                unregistrationEffect.unregister()
            }

            sideEffectQueue.forEach { sideEffect ->
                sideEffect.execute()
            }

            dirtyVertexQueue.forEach { dirtyVertex ->
                dirtyVertex.reset()
            }

            return@with result
        }
    }
}
