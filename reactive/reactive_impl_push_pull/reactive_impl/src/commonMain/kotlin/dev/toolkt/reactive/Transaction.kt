package dev.toolkt.reactive

class Transaction private constructor() {
    sealed interface SamplingContext {

    }

    abstract class ProcessingContext {
        abstract fun enqueueForVisit(
            dependentVertex: DependentVertex,
        )

        abstract fun markDirty(
            dirtyVertex: Vertex,
        )
    }

    companion object {
        fun <ResultT> execute(
            block: (ProcessingContext) -> ResultT,
        ): ResultT = with(Transaction()) {
            // Dependent vertices to be visited
            val visitQueue = ArrayDeque<DependentVertex>()

            // Dirty vertices
            val dirtyVertices = mutableListOf<Vertex>()

            val context = object : ProcessingContext() {
                override fun enqueueForVisit(
                    dependentVertex: DependentVertex,
                ) {
                    visitQueue.addLast(dependentVertex)
                }

                override fun markDirty(
                    dirtyVertex: Vertex,
                ) {
                    dirtyVertices.add(dirtyVertex)
                }
            }

            val result = block(context)

            while (visitQueue.isNotEmpty()) {
                val vertexToProcess = visitQueue.removeFirst()

                vertexToProcess.visit(
                    context = context,
                )
            }

            dirtyVertices.forEach { dirtyVertex ->
                dirtyVertex.commit()
            }

            dirtyVertices.forEach { dirtyVertex ->
                dirtyVertex.reset()
            }

            return@with result
        }
    }
}
