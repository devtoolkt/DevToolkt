package dev.toolkt.reactive.cell.vertices

import dev.toolkt.reactive.Transaction
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.ConstCell
import dev.toolkt.reactive.cell.OperatedCell

class CellSwitchVertex<SourceValueT>(
    private val outerCellVertex: DependencyCellVertex<Cell<SourceValueT>>,
) : StatelessCellVertex<SourceValueT>() {
    override fun prepareStateless(
        preProcessingContext: Transaction.PreProcessingContext,
    ): CellVertex.Update<SourceValueT>? {
        val outerUpdate = outerCellVertex.pullUpdate(
            preProcessingContext = preProcessingContext,
        )

        val latestInnerCell = when (outerUpdate) {
            null -> outerCellVertex.pullStableValue(
                preProcessingContext = preProcessingContext,
            )

            else -> outerUpdate.newValue
        }

        val latestInnerOperatedCell = when (latestInnerCell) {
            is ConstCell -> return null
            is OperatedCell -> latestInnerCell
        }

        val latestInnerOperatedCellVertex = latestInnerOperatedCell.vertex

        val latestInnerOperatedCellUpdate = latestInnerOperatedCellVertex.pullUpdate(
            preProcessingContext = preProcessingContext,
        )

        // TODO: Is this tested?
        if (outerUpdate == null && latestInnerOperatedCellUpdate == null) {
            return null
        }

        val latestInnerOperatedCellLatestValue = when (latestInnerOperatedCellUpdate) {
            null -> latestInnerOperatedCellVertex.pullStableValue(
                preProcessingContext = preProcessingContext,
            )

            else -> latestInnerOperatedCellUpdate.newValue
        }

        return CellVertex.Update(
            newValue = latestInnerOperatedCellLatestValue,
        )
    }

    override fun activateStateless(
        expansionContext: Transaction.ExpansionContext,
    ) {
        outerCellVertex.addDependent(
            expansionContext = expansionContext,
            vertex = this,
        )
    }

    override fun deactivateStateless(
        shrinkageContext: Transaction.ShrinkageContext,
    ) {
        outerCellVertex.removeDependent(
            shrinkageContext = shrinkageContext,
            vertex = this,
        )
    }

    override fun computeStableValue(
        preProcessingContext: Transaction.PreProcessingContext,
    ): SourceValueT {
        val stableOuterCell = outerCellVertex.pullStableValue(
            preProcessingContext = preProcessingContext,
        )

        val stableInnerValue = when (stableOuterCell) {
            is ConstCell -> stableOuterCell.value

            is OperatedCell -> stableOuterCell.vertex.pullStableValue(
                preProcessingContext = preProcessingContext,
            )
        }

        return stableInnerValue
    }
}
