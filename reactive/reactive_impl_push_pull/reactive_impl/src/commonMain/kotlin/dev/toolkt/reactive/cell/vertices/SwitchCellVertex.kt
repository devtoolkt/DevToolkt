package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.vertices.CellVertex.RetrievalMode

class SwitchCellVertex<ValueT>(
    private val outerCellVertex: CellVertex<Cell<ValueT>>,
) : BaseDerivedDynamicCellVertex<ValueT>() {
    private var innerCellVertex: CellVertex<ValueT>? = null

    override fun processActivating(
        context: Transaction.ProcessingContext,
    ): CellVertex.Update<ValueT> {
        val outerUpdate = outerCellVertex.pullUpdateObserving(
            context = context,
            dependentVertex = this,
        )

        val newInnerCell = when (outerUpdate) {
            CellVertex.NilUpdate -> outerCellVertex.sampleOldValue(
                context = context,
            )

            is CellVertex.EffectiveUpdate -> outerUpdate.updatedValue
        }

        val newInnerCellVertex = newInnerCell.vertex

        innerCellVertex = newInnerCellVertex

        return when (newInnerCellVertex) {
            is InertCellVertex -> CellVertex.EffectiveUpdate(
                updatedValue = newInnerCellVertex.sampleOldValue(
                    context = context,
                ),
            )

            is DynamicCellVertex -> newInnerCellVertex.pullUpdateObserving(
                context = context,
                dependentVertex = this,
            )
        }
    }

    override fun processFollowing(
        context: Transaction.ProcessingContext,
    ): CellVertex.Update<ValueT> {
        val oldInnerCellVertex =
            this.innerCellVertex ?: throw IllegalStateException("Inner cell vertex is null in following phase")

        val outerUpdate = outerCellVertex.pullUpdateSubsequent(
            context = context,
        )

        return when (outerUpdate) {
            CellVertex.NilUpdate -> oldInnerCellVertex.pullUpdateSubsequent(
                context = context,
            )

            is CellVertex.EffectiveUpdate -> {
                val updatedInnerCell = outerUpdate.updatedValue
                val updatedInnerCellVertex = updatedInnerCell.vertex

                this.innerCellVertex = updatedInnerCellVertex

                val updatedInnerUpdate = when {
                    oldInnerCellVertex == updatedInnerCellVertex -> oldInnerCellVertex.pullUpdateSubsequent(
                        context = context,
                    )

                    else -> updatedInnerCellVertex.pullUpdateObserving(
                        context = context,
                        dependentVertex = this,
                    ).also {
                        oldInnerCellVertex.unobserve(
                            dependentVertex = this,
                        )
                    }
                }

                when (updatedInnerUpdate) {
                    CellVertex.NilUpdate -> CellVertex.EffectiveUpdate(
                        updatedValue = updatedInnerCellVertex.sampleOldValue(
                            context = context,
                        ),
                    )

                    is CellVertex.EffectiveUpdate -> updatedInnerUpdate
                }
            }
        }
    }

    override fun activate() {
        outerCellVertex.observe(
            dependentVertex = this,
        )

        // TODO: This should use the new (updated / old) inner cell
        val innerCell = outerCellVertex.fetchOldValue()
        val innerCellVertex = innerCell.vertex

        this.innerCellVertex = innerCellVertex

        innerCellVertex.observe(
            dependentVertex = this,
        )
    }

    override fun deactivate() {
        outerCellVertex.unobserve(
            dependentVertex = this,
        )

        innerCellVertex?.unobserve(
            dependentVertex = this,
        )

        innerCellVertex = null
    }

    override fun computeOldValue(
        retrievalMode: RetrievalMode,
    ): ValueT {
        val stableOuterCell = outerCellVertex.retrieve(
            retrievalMode = retrievalMode,
        )

        val stableOuterCellVertex = stableOuterCell.vertex

        return stableOuterCellVertex.retrieve(
            retrievalMode = retrievalMode,
        )
    }
}
