package dev.toolkt.reactive.cell

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.cell.vertices.CellVertex
import dev.toolkt.reactive.cell.vertices.DynamicCellVertex
import dev.toolkt.reactive.cell.vertices.DynamicMap2CellVertex
import dev.toolkt.reactive.cell.vertices.DynamicMapCellVertex
import dev.toolkt.reactive.cell.vertices.InertCellVertex
import dev.toolkt.reactive.cell.vertices.InertMap2CellVertex
import dev.toolkt.reactive.cell.vertices.InertMapCellVertex
import dev.toolkt.reactive.cell.vertices.PureCellVertex
import dev.toolkt.reactive.cell.vertices.SwitchCellVertex
import dev.toolkt.reactive.event_stream.DerivedEventStream
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.vertices.UpdatedValuesEventStreamVertex

sealed interface Cell<out ValueT> {
    companion object {
        fun <ValueT1, ValueT2, ResultT> map2(
            cell1: Cell<ValueT1>,
            cell2: Cell<ValueT2>,
            transform: (ValueT1, ValueT2) -> ResultT,
        ): Cell<ResultT> {
            val cell1Vertex = cell1.vertex
            val cell2Vertex = cell2.vertex

            return OperatedCell(
                when (cell1Vertex) {
                    is InertCellVertex if cell2Vertex is InertCellVertex -> InertMap2CellVertex(
                        sourceCell1Vertex = cell1Vertex,
                        sourceCell2Vertex = cell2Vertex,
                        transform = transform,
                    )

                    else -> DynamicMap2CellVertex(
                        sourceCell1Vertex = cell1Vertex,
                        sourceCell2Vertex = cell2Vertex,
                        transform = transform,
                    )
                },
            )
        }

        fun <ValueT1, ValueT2, ValueT3, ResultT> map3(
            cell1: Cell<ValueT1>,
            cell2: Cell<ValueT2>,
            cell3: Cell<ValueT3>,
            transform: (ValueT1, ValueT2, ValueT3) -> ResultT,
        ): Cell<ResultT> = TODO()

        fun <ValueT> of(
            value: ValueT,
        ): Cell<ValueT> = OperatedCell(
            vertex = PureCellVertex(
                value = value,
            ),
        )

        fun <ValueT> switch(
            outerCell: Cell<Cell<ValueT>>,
        ): Cell<ValueT> = OperatedCell(
            vertex = when (val outerCellVertex = outerCell.vertex) {
                is InertCellVertex -> {
                    val inertInnerCell = outerCellVertex.fetchOldValue()

                    inertInnerCell.vertex
                }

                is DynamicCellVertex -> SwitchCellVertex(
                    outerCellVertex = outerCell.vertex,
                )
            },
        )

        fun <ValueT> divert(
            outerCell: Cell<EventStream<ValueT>>,
        ): EventStream<ValueT> = TODO()
    }

    val vertex: CellVertex<ValueT>
}

context(momentContext: MomentContext) fun <ValueT> Cell<ValueT>.sample(): ValueT = vertex.sampleOldValue(
    context = momentContext.context,
)

fun <ValueT, TransformedValueT> Cell<ValueT>.map(
    transform: (ValueT) -> TransformedValueT,
): Cell<TransformedValueT> = OperatedCell(
    vertex = when (val vertex = this.vertex) {
        is InertCellVertex -> InertMapCellVertex(
            sourceCellVertex = vertex,
            transform = transform,
        )

        is DynamicCellVertex -> DynamicMapCellVertex(
            sourceCellVertex = vertex,
            transform = transform,
        )
    },
)

// TODO: Optimize this
val <ValueT> Cell<ValueT>.newValues: EventStream<ValueT>
    get() = updatedValues

val <ValueT> Cell<ValueT>.updatedValues: EventStream<ValueT>
    get() = when (val vertex = this.vertex) {
        is InertCellVertex -> TODO()

        is DynamicCellVertex -> DerivedEventStream(
            vertex = UpdatedValuesEventStreamVertex(
                sourceCellVertex = vertex,
            )
        )
    }
