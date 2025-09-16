package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.ConstCell
import dev.toolkt.reactive.cell.OperatedCell

class CellSwitchVertex<SourceValueT>(
    private val outerCellVertex: DependencyCellVertex<Cell<SourceValueT>>,
) : StatelessCellVertex<SourceValueT>() {
    override fun process(
        context: Transaction.Context,
    ): CellVertex.UpdatedValue<SourceValueT>? {
        val outerUpdate = outerCellVertex.pullUpdatedValue(
            context = context,
        )

        val latestInnerCell = when (outerUpdate) {
            null -> outerCellVertex.pullStableValue(
                processingContext = context,
            )

            else -> outerUpdate.value
        }

        val latestInnerOperatedCell = when (latestInnerCell) {
            is ConstCell -> return null
            is OperatedCell -> latestInnerCell
        }

        val latestInnerOperatedCellVertex = latestInnerOperatedCell.vertex

        val latestInnerOperatedCellUpdate = latestInnerOperatedCellVertex.pullUpdatedValue(
            context = context,
        )

        // TODO: Is this tested?
        if (outerUpdate == null && latestInnerOperatedCellUpdate == null) {
            return null
        }

        val latestInnerOperatedCellLatestValue = when (latestInnerOperatedCellUpdate) {
            null -> latestInnerOperatedCellVertex.pullStableValue(
                processingContext = context,
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
        context: Transaction.Context,
    ): SourceValueT {
        val stableOuterCell = outerCellVertex.pullStableValue(
            processingContext = context,
        )

        val stableInnerValue = when (stableOuterCell) {
            is ConstCell -> stableOuterCell.value

            is OperatedCell -> stableOuterCell.vertex.pullStableValue(
                processingContext = context,
            )
        }

        return stableInnerValue
    }
}
