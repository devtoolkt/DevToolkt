package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction

class PureCellVertex<ValueT>(
    val value: ValueT,
) : BaseInertCellVertex<ValueT>() {
    override fun reset(
        tag: Tag,
    ) {
    }

    override fun fetchOldValue(): ValueT = value

    override fun sampleOldValue(
        context: Transaction.ProcessingContext,
    ): ValueT = value
}
