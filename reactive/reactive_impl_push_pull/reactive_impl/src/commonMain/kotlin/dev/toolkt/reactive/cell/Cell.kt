package dev.toolkt.reactive.cell

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.vertices.CellMap2Vertex
import dev.toolkt.reactive.cell.vertices.CellMapVertex
import dev.toolkt.reactive.cell.vertices.CellSwitchVertex
import dev.toolkt.reactive.event_stream.DerivedEventStream
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.vertices.UpdatedValuesEventStreamVertex

sealed interface Cell<out ValueT> {
    companion object {
        fun <ValueT1, ValueT2, ResultT> map2(
            cell1: Cell<ValueT1>,
            cell2: Cell<ValueT2>,
            transform: (ValueT1, ValueT2) -> ResultT,
        ): Cell<ResultT> = when (cell1) {
            is ConstCell -> cell2.map {
                transform(cell1.value, it)
            }

            is OperatedCell -> when (cell2) {
                is ConstCell -> cell1.map {
                    transform(it, cell2.value)
                }

                is OperatedCell -> DerivedCell(
                    CellMap2Vertex(
                        sourceCell1Vertex = cell1.vertex,
                        sourceCell2Vertex = cell2.vertex,
                        transform = transform,
                    ),
                )
            }
        }

        fun <ValueT1, ValueT2, ValueT3, ResultT> map3(
            cell1: Cell<ValueT1>,
            cell2: Cell<ValueT2>,
            cell3: Cell<ValueT3>,
            transform: (ValueT1, ValueT2, ValueT3) -> ResultT,
        ): Cell<ResultT> = TODO()

        fun <ValueT> of(
            value: ValueT,
        ): Cell<ValueT> = TODO()

        fun <ValueT> switch(
            outerCell: Cell<Cell<ValueT>>,
        ): Cell<ValueT> = when (outerCell) {
            is ConstCell -> outerCell.value

            is OperatedCell -> DerivedCell(
                CellSwitchVertex<ValueT>(
                    outerCellVertex = outerCell.vertex,
                ),
            )
        }

        fun <ValueT> divert(
            outerCell: Cell<EventStream<ValueT>>,
        ): EventStream<ValueT> = TODO()
    }
}

context(momentContext: MomentContext) fun <ValueT> Cell<ValueT>.sample(): ValueT = when (this) {
    is ConstCell -> TODO()

    is OperatedCell -> vertex.pullStableValue(
        processingContext = momentContext.processingContext,
    )
}

fun <ValueT, TransformedValueT> Cell<ValueT>.map(
    transform: (ValueT) -> TransformedValueT,
): Cell<TransformedValueT> = when (this) {
    is ConstCell -> TODO()

    is OperatedCell -> DerivedCell(
        CellMapVertex(
            sourceCellVertex = this.vertex,
            transform = transform,
        ),
    )
}

// TODO: Optimize this
val <ValueT> Cell<ValueT>.newValues: EventStream<ValueT>
    get() = updatedValues

val <ValueT> Cell<ValueT>.updatedValues: EventStream<ValueT>
    get() = when (this) {
        is ConstCell -> TODO()

        is OperatedCell -> DerivedEventStream(
            vertex = UpdatedValuesEventStreamVertex(
                sourceCellVertex = this.vertex,
            )
        )
    }
