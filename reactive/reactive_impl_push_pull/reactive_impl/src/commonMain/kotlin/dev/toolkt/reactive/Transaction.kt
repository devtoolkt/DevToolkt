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
        abstract fun enqueueForVisiting(
            dependentVertex: DynamicVertex,
        )

        abstract fun enqueueForPostProcessing(
            processedVertex: DynamicVertex,
        )
    }

    abstract class EarlyPostProcessingContext : ExpansionContext()

    abstract class ExpansionContext {
        data object External : ExpansionContext()
    }

    abstract class ShrinkageContext {
        data object External : ShrinkageContext()
    }

    abstract class LatePostProcessingContext : ShrinkageContext()

    companion object {
        fun <ResultT> execute(
            block: (ProcessingContext) -> ResultT,
        ): ResultT = with(Transaction()) {
            val visitationQueue = ArrayDeque<DynamicVertex>()

            val postProcessingQueue = mutableListOf<DynamicVertex>()

            val processingContext = object : ProcessingContext() {
                override fun enqueueForVisiting(
                    dependentVertex: DynamicVertex,
                ) {
                    visitationQueue.addLast(dependentVertex)
                }

                override fun enqueueForPostProcessing(
                    processedVertex: DynamicVertex,
                ) {
                    postProcessingQueue.add(processedVertex)
                }
            }

            val result = block(processingContext)

            while (visitationQueue.isNotEmpty()) {
                val vertexToProcess = visitationQueue.removeFirst()

                vertexToProcess.visit(
                    processingContext = processingContext,
                )
            }

            val earlyPostProcessingContext = object : EarlyPostProcessingContext() {}

            postProcessingQueue.forEach { processedVertex ->
                processedVertex.postProcessEarly(
                    earlyPostProcessingContext = earlyPostProcessingContext,
                )
            }

            val latePostProcessingContext = object : LatePostProcessingContext() {}

            postProcessingQueue.forEach { processedVertex ->
                processedVertex.postProcessLate(
                    latePostProcessingContext = latePostProcessingContext,
                )
            }

            return@with result
        }
    }
}
