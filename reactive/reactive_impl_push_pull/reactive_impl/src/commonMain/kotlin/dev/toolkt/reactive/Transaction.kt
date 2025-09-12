package dev.toolkt.reactive

class Transaction private constructor() {
    abstract class PreparationContext {
        abstract fun enqueueForProcessing(
            vertex: DependentVertex,
        )

        abstract fun enqueueForPostProcessing(
            vertex: DependentVertex,
        )
    }

    abstract class ProcessingContext {
        abstract fun enqueueForPostProcessing(
            processedVertex: DependentVertex,
        )
    }

    data object ExpansionContext

    data object ShrinkageContext

    data object MutationContext

    data object StabilizationContext

    companion object {
        fun execute(
            sourceVertex: SourceVertex,
        ) {
            Transaction().apply {
                val processingContext = object : ProcessingContext() {
                    override fun enqueueForPostProcessing(
                        processedVertex: DependentVertex,
                    ) {
                        this@apply.enqueueForPostProcessing(processedVertex = processedVertex)
                    }
                }

                sourceVertex.process(
                    processingContext = processingContext,
                )

                postProcess()
            }
        }
    }

    private val processedVertices = mutableListOf<DependentVertex>()

    private fun enqueueForPostProcessing(
        processedVertex: DependentVertex,
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
                stabilizationContext = StabilizationContext,
            )
        }
    }
}
