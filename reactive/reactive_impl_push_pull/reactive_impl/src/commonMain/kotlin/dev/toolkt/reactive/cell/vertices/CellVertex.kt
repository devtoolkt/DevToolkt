package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.Vertex
import kotlin.jvm.JvmInline

interface CellVertex<ValueT> : Vertex {
    @JvmInline
    value class Update<ValueT>(
        val newValue: ValueT,
    ) {
        fun <TransformedValueT> map(
            transform: (ValueT) -> TransformedValueT,
        ): Update<TransformedValueT> = Update(
            newValue = transform(newValue),
        )
    }

    fun pullStableValue(
        processingContext: Transaction.ProcessingContext,
    ): ValueT
}
