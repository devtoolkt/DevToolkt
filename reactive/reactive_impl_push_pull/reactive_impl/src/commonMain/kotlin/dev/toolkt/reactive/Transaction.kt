package dev.toolkt.reactive

import dev.toolkt.core.utils.iterable.append

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
            processedVertex: DynamicVertex,
        )
    }

    data object ExpansionContext

    data object ShrinkageContext

    data object MutationContext

    data object StabilizationContext

    companion object {
        fun execute(
            sourceVertex: DynamicVertex,
        ) {
            Transaction().apply {
                val verticesEnqueuedForProcessing = ArrayDeque(
                    elements = listOf(sourceVertex),
                )

                val processingContext = object : ProcessingContext() {
                    override fun enqueueForProcessing(
                        dependentVertex: DynamicVertex,
                    ) {
                        verticesEnqueuedForProcessing.addLast(dependentVertex)
                    }

                    override fun enqueueForPostProcessing(
                        processedVertex: DynamicVertex,
                    ) {
                        this@apply.enqueueForPostProcessing(processedVertex = processedVertex)
                    }
                }

                while (verticesEnqueuedForProcessing.isNotEmpty()) {
                    val vertexToProcess = verticesEnqueuedForProcessing.removeFirst()

                    vertexToProcess.process(
                        processingContext = processingContext,
                    )
                }

                postProcess()
            }
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

            processedVertex.stabilize(
                stabilizationContext = StabilizationContext,
            )
        }
    }
}
