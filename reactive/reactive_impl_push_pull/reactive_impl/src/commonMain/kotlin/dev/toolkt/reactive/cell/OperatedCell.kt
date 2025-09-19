package dev.toolkt.reactive.cell

import dev.toolkt.reactive.cell.vertices.CellVertex
import dev.toolkt.reactive.cell.vertices.DynamicCellVertex
import dev.toolkt.reactive.cell.vertices.DynamicMap2CellVertex
import dev.toolkt.reactive.cell.vertices.InertCellVertex
import dev.toolkt.reactive.cell.vertices.InertMap2CellVertex
import dev.toolkt.reactive.cell.vertices.PureCellVertex
import dev.toolkt.reactive.cell.vertices.SwitchCellVertex
import dev.toolkt.reactive.event_stream.EventStream
import kotlin.jvm.JvmInline

@JvmInline
internal value class OperatedCell<out ValueT>(
    override val vertex: CellVertex<ValueT>,
) : Cell<ValueT> {
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
}
