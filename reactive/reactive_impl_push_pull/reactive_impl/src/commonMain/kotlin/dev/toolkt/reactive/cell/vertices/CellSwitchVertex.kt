package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.ConstCell
import dev.toolkt.reactive.cell.OperatedCell
import dev.toolkt.reactive.cell.vertices.CellVertex.RetrievalMode

class CellSwitchVertex<ValueT>(
    private val outerCellVertex: DependencyCellVertex<Cell<ValueT>>,
) : DerivedCellVertex<ValueT>() {
    private var innerCellVertex: DependencyCellVertex<ValueT>? = null

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

        when (newInnerCell) {
            is ConstCell -> {
                innerCellVertex = null

                return CellVertex.EffectiveUpdate(
                    updatedValue = newInnerCell.value,
                )
            }

            is OperatedCell -> {
                val newInnerCellVertex = newInnerCell.vertex

                innerCellVertex = newInnerCellVertex

                return newInnerCellVertex.pullUpdateObserving(
                    context = context,
                    dependentVertex = this,
                )
            }
        }
    }

    override fun processFollowing(
        context: Transaction.ProcessingContext,
    ): CellVertex.Update<ValueT> {
        val outerUpdate = outerCellVertex.pullUpdateSubsequent(
            context = context,
        )

        when (outerUpdate) {
            CellVertex.NilUpdate -> {
                return when (val innerCellVertex = this.innerCellVertex) {
                    null -> CellVertex.NilUpdate

                    else -> innerCellVertex.pullUpdateSubsequent(
                        context = context,
                    )
                }
            }

            is CellVertex.EffectiveUpdate -> {
                when (val updatedInnerCell = outerUpdate.updatedValue) {
                    is ConstCell -> {
                        innerCellVertex = null

                        return CellVertex.EffectiveUpdate(
                            updatedValue = updatedInnerCell.value,
                        )
                    }

                    is OperatedCell -> {
                        val newInnerCellVertex = updatedInnerCell.vertex

                        val newInnerUpdate = when (val innerCellVertex = this.innerCellVertex) {
                            newInnerCellVertex -> {
                                innerCellVertex.pullUpdateSubsequent(
                                    context = context,
                                )
                            }

                            else -> {
                                innerCellVertex?.unobserve(
                                    dependentVertex = this,
                                )

                                this.innerCellVertex = newInnerCellVertex

                                newInnerCellVertex.pullUpdateObserving(
                                    context = context,
                                    dependentVertex = this,
                                )
                            }
                        }

                        return when (newInnerUpdate) {
                            CellVertex.NilUpdate -> CellVertex.EffectiveUpdate(
                                updatedValue = newInnerCellVertex.sampleOldValue(
                                    context = context,
                                ),
                            )

                            is CellVertex.EffectiveUpdate -> newInnerUpdate
                        }
                    }
                }
            }
        }
    }

    override fun activate() {
        outerCellVertex.observe(
            dependentVertex = this,
        )

        // TODO: Introduce an activation context that would allow caching such values?
        when (val innerCell = outerCellVertex.fetchOldValue()) {
            is ConstCell -> {
                innerCellVertex = null
            }

            is OperatedCell -> {
                val innerCellVertex = innerCell.vertex

                this.innerCellVertex = innerCellVertex

                innerCellVertex.observe(
                    dependentVertex = this,
                )
            }
        }
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

        return when (stableOuterCell) {
            is ConstCell -> stableOuterCell.value

            is OperatedCell -> stableOuterCell.vertex.retrieve(
                retrievalMode = retrievalMode,
            )
        }
    }
}
