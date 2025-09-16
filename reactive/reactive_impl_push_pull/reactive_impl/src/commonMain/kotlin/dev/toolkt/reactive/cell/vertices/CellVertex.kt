package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.Vertex
import kotlin.jvm.JvmInline

interface CellVertex<ValueT> : Vertex {
    @JvmInline
    value class UpdatedValue<ValueT>(
        val value: ValueT,
    ) {
        fun <TransformedValueT> map(
            transform: (ValueT) -> TransformedValueT,
        ): UpdatedValue<TransformedValueT> = UpdatedValue(
            value = transform(value),
        )
    }

    fun pullStableValue(
        context: Transaction.Context,
    ): ValueT
}
