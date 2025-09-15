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

    abstract class PreProcessingContext {
        abstract fun enqueueForProcessing(
            dependentVertex: DynamicVertex,
        )

        abstract fun enqueueForPostProcessing(
            vertex: DynamicVertex,
        )
    }

    abstract class InterProcessingContext : ExpansionContext()

    abstract class ExpansionContext {
        data object External : ExpansionContext()
    }

    abstract class ShrinkageContext {
        data object External : ShrinkageContext()
    }

    abstract class PostProcessingContext : ShrinkageContext()

    companion object {
        fun <ResultT> execute(
            block: (PreProcessingContext) -> ResultT,
        ): ResultT = with(Transaction()) {
            val verticesEnqueuedForProcessing = ArrayDeque<DynamicVertex>()

            val preProcessingContext = object : PreProcessingContext() {
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

            val result = block(preProcessingContext)

            while (verticesEnqueuedForProcessing.isNotEmpty()) {
                val vertexToProcess = verticesEnqueuedForProcessing.removeFirst()

                vertexToProcess.preProcess(
                    preProcessingContext = preProcessingContext,
                )
            }

            processedVertices.forEach { processedVertex ->
                processedVertex.interProcess(
                    object : InterProcessingContext() {},
                )
            }

            processedVertices.forEach { processedVertex ->
                processedVertex.postProcess(
                    object : PostProcessingContext() {},
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
