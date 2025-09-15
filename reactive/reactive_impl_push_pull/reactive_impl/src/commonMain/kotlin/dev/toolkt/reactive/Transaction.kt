package dev.toolkt.reactive

class Transaction private constructor() {
    abstract class PreparationContext {
        abstract fun enqueueForProcessing(
            vertex: DynamicVertex,
        )

        abstract fun enqueueForPostProcessing(
            vertex: DynamicVertex,
        )
    }

    abstract class ProcessingContext {
        abstract fun enqueueForProcessing(
            dependentVertex: DynamicVertex,
        )

        abstract fun enqueueForPostProcessing(
            vertex: DynamicVertex,
        )
    }

    data object ExpansionContext

    data object ShrinkageContext

    data object MutationContext

    data object StabilizationContext

    companion object {
        fun <ResultT> execute(
            block: (ProcessingContext) -> ResultT,
        ): ResultT = with(Transaction()) {
            val verticesEnqueuedForProcessing = ArrayDeque<DynamicVertex>()

            val processingContext = object : ProcessingContext() {
                override fun enqueueForProcessing(
                    dependentVertex: DynamicVertex,
                ) {
                    verticesEnqueuedForProcessing.addLast(dependentVertex)
                }

                override fun enqueueForPostProcessing(
                    vertex: DynamicVertex,
                ) {
                    this@with.enqueueForPostProcessing(processedVertex = vertex)
                }
            }

            val result = block(processingContext)

            while (verticesEnqueuedForProcessing.isNotEmpty()) {
                val vertexToProcess = verticesEnqueuedForProcessing.removeFirst()

                vertexToProcess.processDynamic(
                    processingContext = processingContext,
                )
            }

            postProcess()

            return@with result
        }
    }

    private val processedVertices = mutableListOf<DynamicVertex>()

    private fun enqueueForPostProcessing(
        processedVertex: DynamicVertex,
    ) {
        processedVertices.add(processedVertex)
    }

    private fun postProcess() {
        processedVertices.forEach { processedVertex ->
            processedVertex.expand(
                expansionContext = ExpansionContext,
            )

            processedVertex.invokeEffects(
                mutationContext = MutationContext,
            )
        }

        processedVertices.forEach { processedVertex ->
            processedVertex.shrink(
                shrinkageContext = ShrinkageContext,
            )

            processedVertex.stabilizeDynamic(
                stabilizationContext = StabilizationContext,
            )
        }
    }
}
