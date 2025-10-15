package dev.toolkt.reactive.cell

import dev.toolkt.reactive.MomentContext
import dev.toolkt.reactive.ObservationVertex
import dev.toolkt.reactive.cell.vertices.CellVertex
import dev.toolkt.reactive.cell.vertices.DivertEventStreamVertex
import dev.toolkt.reactive.cell.vertices.DynamicCellVertex
import dev.toolkt.reactive.cell.vertices.DynamicMap2CellVertex
import dev.toolkt.reactive.cell.vertices.DynamicMap3CellVertex
import dev.toolkt.reactive.cell.vertices.DynamicMapCellVertex
import dev.toolkt.reactive.cell.vertices.InertCellVertex
import dev.toolkt.reactive.cell.vertices.InertMap2CellVertex
import dev.toolkt.reactive.cell.vertices.InertMap3CellVertex
import dev.toolkt.reactive.cell.vertices.InertMapCellVertex
import dev.toolkt.reactive.cell.vertices.PureCellVertex
import dev.toolkt.reactive.cell.vertices.SwitchCellVertex
import dev.toolkt.reactive.event_stream.OperatedEventStream
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.hold
import dev.toolkt.reactive.event_stream.vertices.SilentEventStreamVertex
import dev.toolkt.reactive.event_stream.vertices.UpdatedValuesEventStreamVertex

sealed interface Cell<out ValueT> {
    sealed interface Notification<out ValueT>

    sealed interface UpdateNotification<out ValueT> : Notification<ValueT> {
        companion object {
            fun <ValueT : Any> of(
                updatedValue: ValueT,
                isFreezing: Boolean,
            ): UpdateNotification<ValueT> = when {
                isFreezing -> FreezingUpdateNotification(
                    updatedFrozenValue = updatedValue,
                )

                else -> IntermediateUpdateNotification(
                    updatedValue = updatedValue,
                )
            }
        }

        val updatedValue: ValueT
    }

    sealed interface FreezeNotification<out ValueT> : Notification<ValueT> {
        companion object {
            fun <ValueT : Any> of(
                updatedValue: ValueT?,
            ): FreezeNotification<ValueT> = when {
                updatedValue != null -> FreezingUpdateNotification(
                    updatedValue,
                )

                else -> IsolatedFreezeNotification
            }
        }
    }

    data class IntermediateUpdateNotification<out ValueT>(
        override val updatedValue: ValueT,
    ) : UpdateNotification<ValueT>, FreezeNotification<ValueT>

    data class FreezingUpdateNotification<out ValueT>(
        val updatedFrozenValue: ValueT,
    ) : UpdateNotification<ValueT>, FreezeNotification<ValueT> {
        override val updatedValue: ValueT
            get() = updatedFrozenValue
    }

    data object IsolatedFreezeNotification : FreezeNotification<Nothing>

    interface Observer<ValueT> {
        fun handleNotification(
            notification: Notification<ValueT>,
        )
    }

    interface BasicObserver<ValueT> {
        fun handleUpdate(
            updatedValue: ValueT,
        )

        fun handleFreeze()
    }

    interface Observation {
        fun cancel()
    }

    companion object {
        fun <ValueT1, ValueT2, ResultT> map2(
            cell1: Cell<ValueT1>,
            cell2: Cell<ValueT2>,
            transform: (ValueT1, ValueT2) -> ResultT,
        ): Cell<ResultT> {
            val cell1Vertex = cell1.vertex
            val cell2Vertex = cell2.vertex

            return OperatedCell(
                when {
                    cell1Vertex is InertCellVertex && cell2Vertex is InertCellVertex -> InertMap2CellVertex(
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
        ): Cell<ResultT> {
            val cell1Vertex = cell1.vertex
            val cell2Vertex = cell2.vertex
            val cell3Vertex = cell3.vertex

            return OperatedCell(

                when {
                    cell1Vertex is InertCellVertex && cell2Vertex is InertCellVertex && cell3Vertex is InertCellVertex -> InertMap3CellVertex(
                        sourceCell1Vertex = cell1Vertex,
                        sourceCell2Vertex = cell2Vertex,
                        sourceCell3Vertex = cell3Vertex,
                        transform = transform,
                    )

                    else -> DynamicMap3CellVertex(
                        sourceCell1Vertex = cell1Vertex,
                        sourceCell2Vertex = cell2Vertex,
                        sourceCell3Vertex = cell3Vertex,
                        transform = transform,
                    )
                },
            )
        }

        fun <ValueT> of(
            value: ValueT,
        ): Cell<ValueT> = OperatedCell(
            vertex = PureCellVertex(
                value = value,
            ),
        )

        context(momentContext: MomentContext) fun <ValueT> define(
            initialValue: ValueT,
            newValues: EventStream<ValueT>,
        ): Cell<ValueT> = newValues.hold(
            initialValue = initialValue,
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
        ): EventStream<ValueT> = OperatedEventStream(
            vertex = when (val outerCellVertex = outerCell.vertex) {
                is InertCellVertex -> {
                    val inertInnerEventStream = outerCellVertex.fetchOldValue()

                    inertInnerEventStream.vertex
                }

                is DynamicCellVertex -> DivertEventStreamVertex(
                    outerEventStreamVertex = outerCell.vertex,
                )
            },
        )
    }

    val vertex: CellVertex<ValueT>
}

fun <ValueT> Cell<ValueT>.observe(
    observer: Cell.Observer<ValueT>,
): Cell.Observation? = when (val vertex = this.vertex) {
    is InertCellVertex -> null

    is DynamicCellVertex -> {
        val observationVertex = ObservationVertex(
            sourceCellVertex = this.vertex,
            observer = observer,
        )

        vertex.observe(
            dependentVertex = observationVertex,
        )

        object : Cell.Observation {
            override fun cancel() {
                this@observe.vertex.unobserve(
                    dependentVertex = observationVertex,
                )
            }
        }
    }
}

fun <ValueT> Cell<ValueT>.observe(
    observer: Cell.BasicObserver<ValueT>,
): Cell.Observation? = observe(
    observer = object : Cell.Observer<ValueT> {
        override fun handleNotification(
            notification: Cell.Notification<ValueT>,
        ) {
            (notification as? Cell.UpdateNotification)?.let {
                observer.handleUpdate(
                    updatedValue = it.updatedValue,
                )
            }

            (notification as? Cell.FreezeNotification)?.let {
                observer.handleFreeze()
            }
        }
    },
)

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
    get() = OperatedEventStream(
        vertex = when (val vertex = this.vertex) {
            is InertCellVertex -> SilentEventStreamVertex

            is DynamicCellVertex -> UpdatedValuesEventStreamVertex(
                sourceCellVertex = vertex,
            )
        }
    )
