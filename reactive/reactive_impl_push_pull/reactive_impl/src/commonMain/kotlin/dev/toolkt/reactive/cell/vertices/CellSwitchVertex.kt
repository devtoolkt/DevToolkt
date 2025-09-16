package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.ConstCell
import dev.toolkt.reactive.cell.OperatedCell

class CellSwitchVertex<SourceValueT>(
    private val outerCellVertex: DependencyCellVertex<Cell<SourceValueT>>,
) : StatelessCellVertex<SourceValueT>() {
    override fun process(
        processingContext: Transaction.ProcessingContext,
    ): CellVertex.UpdatedValue<SourceValueT>? {
        val outerUpdate = outerCellVertex.pullUpdatedValue(
            processingContext = processingContext,
        )

        val latestInnerCell = when (outerUpdate) {
            null -> outerCellVertex.pullStableValue(
                processingContext = processingContext,
            )

            else -> outerUpdate.value
        }

        val latestInnerOperatedCell = when (latestInnerCell) {
            is ConstCell -> return null
            is OperatedCell -> latestInnerCell
        }

        val latestInnerOperatedCellVertex = latestInnerOperatedCell.vertex

        val latestInnerOperatedCellUpdate = latestInnerOperatedCellVertex.pullUpdatedValue(
            processingContext = processingContext,
        )

        // TODO: Is this tested?
        if (outerUpdate == null && latestInnerOperatedCellUpdate == null) {
            return null
        }

        val latestInnerOperatedCellLatestValue = when (latestInnerOperatedCellUpdate) {
            null -> latestInnerOperatedCellVertex.pullStableValue(
                processingContext = processingContext,
            )

            else -> latestInnerOperatedCellUpdate.value
        }

        return CellVertex.UpdatedValue(
            value = latestInnerOperatedCellLatestValue,
        )
    }

    override fun activate() {
        outerCellVertex.addDependent(
            dependentVertex = this,
        )
    }

    override fun deactivate() {
        outerCellVertex.removeDependent(
            dependentVertex = this,
        )
    }

    override fun computeStableValue(
        processingContext: Transaction.ProcessingContext,
    ): SourceValueT {
        val stableOuterCell = outerCellVertex.pullStableValue(
            processingContext = processingContext,
        )

        val stableInnerValue = when (stableOuterCell) {
            is ConstCell -> stableOuterCell.value

            is OperatedCell -> stableOuterCell.vertex.pullStableValue(
                processingContext = processingContext,
            )
        }

        return stableInnerValue
    }
}
