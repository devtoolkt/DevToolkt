package dev.toolkt.reactive

class Transaction private constructor() {
    abstract class PreparationContext {
        abstract fun enqueueForProcessing(
            vertex: Vertex,
        )

        abstract fun enqueueForPostProcessing(
            vertex: Vertex,
        )
    }

    abstract class ProcessingContext {
        abstract fun enqueueForPostProcessing(
            processedVertex: Vertex,
        )
    }

    data object ExpansionContext

    data object ShrinkageContext

    data object MutationContext

    data object ResettingContext

    companion object {
        fun execute(
            sourceVertex: Vertex,
        ) {
            Transaction().apply {
                val processingContext = object : ProcessingContext() {
                    override fun enqueueForPostProcessing(
                        processedVertex: Vertex,
                    ) {
                        this@apply.enqueueForPostProcessing(processedVertex = processedVertex)
                    }
                }

                sourceVertex.ensureProcessed(
                    processingContext = processingContext,
                )

                postProcess()
            }
        }
    }

    private val processedVertices = mutableListOf<Vertex>()

    private fun enqueueForPostProcessing(
        processedVertex: Vertex,
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

            processedVertex.stabilize(
                resettingContext = ResettingContext,
            )
        }
    }
}
