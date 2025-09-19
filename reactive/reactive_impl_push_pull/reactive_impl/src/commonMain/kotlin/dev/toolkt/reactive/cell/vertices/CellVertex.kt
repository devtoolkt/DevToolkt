package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.DependentVertex
import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.Vertex
import dev.toolkt.reactive.cell.vertices.CellVertex.EffectiveUpdate
import dev.toolkt.reactive.cell.vertices.CellVertex.NilUpdate
import dev.toolkt.reactive.cell.vertices.CellVertex.RetrievalMode
import dev.toolkt.reactive.cell.vertices.CellVertex.Update
import dev.toolkt.reactive.event_stream.vertices.EventStreamVertex
import kotlin.jvm.JvmInline
import kotlin.reflect.KProperty

sealed interface CellVertex<out ValueT> : Vertex {
    @JvmInline
    value class StableValue<ValueT>(
        val value: ValueT,
    )

    sealed interface Update<out ValueT>

    @JvmInline
    value class EffectiveUpdate<out ValueT>(
        val updatedValue: ValueT,
    ) : Update<ValueT>

    data object NilUpdate : Update<Nothing>

    sealed interface RetrievalMode {
        fun <ValueT> retrieve(
            vertex: CellVertex<ValueT>,
        ): ValueT

        class Sample(
            val context: Transaction.ProcessingContext,
        ) : RetrievalMode {
            override fun <ValueT> retrieve(
                vertex: CellVertex<ValueT>,
            ): ValueT = vertex.sampleOldValue(
                context = context,
            )
        }

        data object Fetch : RetrievalMode {
            override fun <ValueT> retrieve(
                vertex: CellVertex<ValueT>,
            ): ValueT = vertex.fetchOldValue()
        }
    }

    /**
     * Fetch the stable value of this cell.
     *
     * The fetched value is not cached.
     */
    fun fetchOldValue(): ValueT

    /**
     * Sample the old value of this cell vertex in the given transaction context.
     *
     * The sampled value will be cached until the transaction is completed.
     */
    fun sampleOldValue(
        context: Transaction.ProcessingContext,
    ): ValueT

    fun pullUpdateObserving(
        context: Transaction.ProcessingContext,
        dependentVertex: DependentVertex,
    ): CellVertex.Update<ValueT>

    fun observe(
        dependentVertex: DependentVertex,
    )

    fun pullUpdateSubsequent(
        context: Transaction.ProcessingContext,
    ): CellVertex.Update<ValueT>

    fun unobserve(
        dependentVertex: DependentVertex,
    )
}

fun <ValueT> Update<ValueT>.toOccurrence(): EventStreamVertex.Occurrence<ValueT> = when (this) {
    NilUpdate -> EventStreamVertex.NilOccurrence

    is EffectiveUpdate -> EventStreamVertex.EffectiveOccurrence(
        event = updatedValue,
    )
}

fun <ValueT> CellVertex<ValueT>.retrieve(
    retrievalMode: RetrievalMode,
): ValueT = retrievalMode.retrieve(
    vertex = this,
)

fun <ValueT, ResultT> Update<ValueT>.map(
    transform: (ValueT) -> ResultT,
): Update<ResultT> = when (this) {
    is EffectiveUpdate -> EffectiveUpdate(
        updatedValue = transform(updatedValue),
    )

    NilUpdate -> NilUpdate
}
