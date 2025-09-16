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

    abstract class EarlyPostProcessingContext : ExpansionContext()

    abstract class ExpansionContext {
        data object External : ExpansionContext()
    }

    abstract class ShrinkageContext {
        data object External : ShrinkageContext()
    }

    abstract class PostProcessingContext : ShrinkageContext()

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

                vertexToProcess.process(
                    processingContext = processingContext,
                )
            }

            val earlyPostProcessingContext = object : EarlyPostProcessingContext() {}

            processedVertices.forEach { processedVertex ->
                processedVertex.postProcessEarly(
                    earlyPostProcessingContext = earlyPostProcessingContext,
                )
            }

            val postProcessingContext = object : PostProcessingContext() {}

            processedVertices.forEach { processedVertex ->
                processedVertex.postProcess(
                    postProcessingContext = postProcessingContext,
                )
            }

            return@with result
        }
    }

    private val processedVertices = mutableListOf<DynamicVertex>()

    private fun enqueueForPostProcessing(
        processedVertex: DynamicVertex,
    ) {
        processedVertices.add(processedVertex)
    }
}
